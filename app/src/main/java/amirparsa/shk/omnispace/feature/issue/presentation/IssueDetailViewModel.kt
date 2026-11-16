package amirparsa.shk.omnispace.feature.issue.presentation

import androidx.lifecycle.viewModelScope
import amirparsa.shk.omnispace.core.base.mvi.MviViewModel
import amirparsa.shk.omnispace.core.database.dao.PersonDao
import amirparsa.shk.omnispace.core.network.SessionManager
import amirparsa.shk.omnispace.core.utils.DateUtils
import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.issue.domain.model.IssueCommentUi
import amirparsa.shk.omnispace.feature.issue.domain.model.IssueStatusType
import amirparsa.shk.omnispace.feature.work.data.remote.dto.IssueTransitionDto
import amirparsa.shk.omnispace.feature.work.domain.repository.WorkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IssueDetailViewModel @Inject constructor(
    private val workRepo: WorkRepository,
    private val personDao: PersonDao,
    private val sessionManager: SessionManager
) : MviViewModel<IssueDetailUiState, IssueDetailIntent, IssueDetailEffect>(
    initialState = IssueDetailUiState(isLoading = true)
) {

    private fun mapStatusIdToStatusType(statusId: String?): IssueStatusType {
        val s = statusId?.lowercase().orEmpty()
        return when {
            s.contains("progress") || s.contains("doing") -> IssueStatusType.IN_PROGRESS
            s.contains("done") || s.contains("closed") || s.contains("resolved") -> IssueStatusType.DONE
            s.contains("qa") || s.contains("review") || s.contains("test") -> IssueStatusType.QA_REVIEW
            else -> IssueStatusType.TODO
        }
    }

    private fun loadIssue(key: String) {
        if (key.isBlank()) {
            setState { copy(isLoading = false, error = "شناسه تسک مشخص نشده است") }
            return
        }
        viewModelScope.launch {
            setState { copy(isLoading = true, issueKey = key, error = null) }
            val result = workRepo.getIssueByKey(key)
            if (result is NetworkResult.Success) {
                val issue = result.data
                val statusType = mapStatusIdToStatusType(issue.statusId)
                val estimatedHours = issue.estimatePoints?.toDoubleOrNull() ?: 0.0
                val person = issue.assigneeId?.let { personDao.getPersonById(it) }
                val assignee = person?.displayName ?: issue.assigneeId ?: "ثبت نشده"
                val initials = if (assignee != "ثبت نشده" && assignee.isNotBlank()) {
                    assignee.trim().split(" ")
                        .filter { it.isNotBlank() }
                        .mapNotNull { it.firstOrNull()?.toString() }
                        .take(2)
                        .joinToString("")
                        .ifBlank { "هم" }
                } else "—"
                val priority = when {
                    issue.priorityId?.contains("blocker", ignoreCase = true) == true -> "بحرانی (Blocker)"
                    issue.priorityId?.contains("high", ignoreCase = true) == true -> "فوری (High)"
                    issue.priorityId?.contains("low", ignoreCase = true) == true -> "پایین (Low)"
                    else -> "متوسط (Medium)"
                }
                val sprint = if (!issue.sprintId.isNullOrBlank()) "اسپرینت ${issue.sprintId}" else "اسپرینت جاری"
                val estimateSp = if (!issue.estimatePoints.isNullOrBlank()) "${issue.estimatePoints} SP" else "—"
                val due = issue.dueAt?.take(10) ?: "ثبت نشده"

                setState {
                    copy(
                        issueId = issue.id,
                        issueKey = issue.key,
                        title = issue.title,
                        description = issue.description ?: "توضیحاتی برای این تسک ثبت نشده است.",
                        currentStatus = statusType,
                        totalEstimatedHours = estimatedHours,
                        assigneeName = assignee,
                        assigneeInitials = initials,
                        priorityName = priority,
                        sprintName = sprint,
                        estimateStoryPoints = estimateSp,
                        dueDateText = due,
                        isLoading = false,
                        error = null
                    )
                }

                // 1. Load Transitions
                launch {
                    val transitionsResult = workRepo.getIssueTransitions(issue.id)
                    val transitions = if (transitionsResult is NetworkResult.Success && transitionsResult.data.isNotEmpty()) {
                        transitionsResult.data
                    } else {
                        // Fallback to project workflow transitions
                        val workflowResult = workRepo.getProjectWorkflow(issue.projectId)
                        if (workflowResult is NetworkResult.Success) {
                            workflowResult.data.transitions.map { wt ->
                                IssueTransitionDto(
                                    id = wt.id,
                                    name = wt.name,
                                    toStatusId = wt.toStatusId
                                )
                            }
                        } else emptyList()
                    }
                    setState { copy(availableTransitions = transitions) }
                }

                // 2. Load Comments
                launch {
                    val commentsResult = workRepo.getIssueComments(issue.id)
                    if (commentsResult is NetworkResult.Success) {
                        val mappedComments = commentsResult.data.map { comment ->
                            val author = comment.importedAuthor ?: "همکار سازمانی"
                            val initials = author.trim().split(" ")
                                .filter { it.isNotBlank() }
                                .mapNotNull { it.firstOrNull()?.toString() }
                                .take(2)
                                .joinToString("")
                                .ifBlank { "هم" }
                            val timeStr = DateUtils.toRelativeTimeFa(comment.createdAt).ifBlank { "اخیر" }
                            IssueCommentUi(
                                id = comment.id,
                                authorName = author,
                                authorInitials = initials,
                                time = timeStr,
                                text = comment.body
                            )
                        }
                        setState { copy(comments = mappedComments) }
                    }
                }
            } else {
                val err = (result as? NetworkResult.Error)?.message ?: "خطا در دریافت تسک $key"
                setState { copy(isLoading = false, error = err) }
            }
        }
    }

    override fun processIntent(intent: IssueDetailIntent) {
        when (intent) {
            is IssueDetailIntent.LoadIssue -> {
                loadIssue(intent.key)
            }
            is IssueDetailIntent.ExecuteTransition -> {
                val issueId = currentState.issueId
                if (issueId.isBlank()) return

                val targetTransition = currentState.availableTransitions.firstOrNull { it.id == intent.transitionId }
                val targetStatus = targetTransition?.resolvedToStatusId?.let { mapStatusIdToStatusType(it) }

                if (targetStatus != null) {
                    setState { copy(currentStatus = targetStatus, isTransitioning = true) }
                } else {
                    setState { copy(isTransitioning = true) }
                }

                viewModelScope.launch {
                    val result = workRepo.transitionIssue(issueId, intent.transitionId)
                    setState { copy(isTransitioning = false) }
                    if (result is NetworkResult.Success) {
                        val label = intent.targetStatusName ?: targetTransition?.displayName ?: "جدید"
                        sendEffect(IssueDetailEffect.ShowToast("وضعیت به «$label» تغییر یافت"))
                        loadIssue(currentState.issueKey)
                    } else {
                        val errMsg = (result as? NetworkResult.Error)?.message ?: "خطا در تغییر وضعیت تسک"
                        sendEffect(IssueDetailEffect.ShowToast(errMsg))
                    }
                }
            }
            is IssueDetailIntent.ChangeStatus -> {
                val issueId = currentState.issueId
                val matchingTransition = currentState.availableTransitions.firstOrNull { t ->
                    val resolvedStatus = t.resolvedToStatusId?.let { mapStatusIdToStatusType(it) }
                    resolvedStatus == intent.status || t.displayName.contains(intent.status.labelFa, ignoreCase = true)
                }

                if (matchingTransition != null && issueId.isNotBlank()) {
                    processIntent(IssueDetailIntent.ExecuteTransition(matchingTransition.id, intent.status.labelFa))
                } else {
                    setState { copy(currentStatus = intent.status) }
                    sendEffect(IssueDetailEffect.ShowToast("وضعیت به «${intent.status.labelFa}» تغییر یافت"))
                }
            }
            is IssueDetailIntent.SelectTab -> {
                setState { copy(selectedTab = intent.tab) }
            }
            is IssueDetailIntent.ToggleChecklistItem -> {
                setState {
                    copy(
                        checklistItems = checklistItems.map {
                            if (it.id == intent.id) it.copy(isDone = !it.isDone) else it
                        }
                    )
                }
            }
            is IssueDetailIntent.UpdateCommentInput -> {
                setState { copy(commentInputText = intent.text) }
            }
            IssueDetailIntent.SubmitComment -> {
                val text = currentState.commentInputText.trim()
                if (text.isBlank() || currentState.isSubmittingComment) return

                val myName = sessionManager.displayName?.ifBlank { "شما" } ?: "شما"
                val myInitials = myName.trim().split(" ")
                    .filter { it.isNotBlank() }
                    .mapNotNull { it.firstOrNull()?.toString() }
                    .take(2)
                    .joinToString("")
                    .ifBlank { "ش" }

                val currentIssueId = currentState.issueId
                val localComment = IssueCommentUi(
                    id = "local_${System.currentTimeMillis()}",
                    authorName = "$myName (شما)",
                    authorInitials = myInitials,
                    time = "هم‌اکنون",
                    text = text
                )
                setState {
                    copy(
                        comments = comments + localComment,
                        commentInputText = "",
                        isSubmittingComment = true
                    )
                }

                if (currentIssueId.isNotBlank()) {
                    viewModelScope.launch {
                        val result = workRepo.addIssueComment(currentIssueId, text)
                        setState { copy(isSubmittingComment = false) }
                        if (result is NetworkResult.Success) {
                            sendEffect(IssueDetailEffect.ShowToast("دیدگاه شما ثبت گردید"))
                            // Reload fresh comments from server
                            val refreshResult = workRepo.getIssueComments(currentIssueId)
                            if (refreshResult is NetworkResult.Success) {
                                val freshComments = refreshResult.data.map { comment ->
                                    val author = comment.importedAuthor ?: "همکار سازمانی"
                                    val initials = author.trim().split(" ")
                                        .filter { it.isNotBlank() }
                                        .mapNotNull { it.firstOrNull()?.toString() }
                                        .take(2)
                                        .joinToString("")
                                        .ifBlank { "هم" }
                                    val timeStr = DateUtils.toRelativeTimeFa(comment.createdAt).ifBlank { "هم‌اکنون" }
                                    IssueCommentUi(
                                        id = comment.id,
                                        authorName = author,
                                        authorInitials = initials,
                                        time = timeStr,
                                        text = comment.body
                                    )
                                }
                                setState { copy(comments = freshComments) }
                            }
                        } else {
                            val err = (result as? NetworkResult.Error)?.message ?: "خطا در ثبت دیدگاه"
                            sendEffect(IssueDetailEffect.ShowToast(err))
                        }
                    }
                } else {
                    setState { copy(isSubmittingComment = false) }
                    sendEffect(IssueDetailEffect.ShowToast("دیدگاه شما ثبت گردید"))
                }
            }
            IssueDetailIntent.OpenLogWorkDialog -> {
                setState { copy(isLogWorkDialogOpen = true) }
            }
            IssueDetailIntent.DismissLogWorkDialog -> {
                setState { copy(isLogWorkDialogOpen = false) }
            }
            is IssueDetailIntent.SubmitLogWork -> {
                if (intent.minutes <= 0) {
                    sendEffect(IssueDetailEffect.ShowToast("لطفاً مدت زمان معتبری وارد کنید"))
                    return
                }
                if (currentState.isLoggingWork) return

                val currentIssueId = currentState.issueId
                setState { copy(isLoggingWork = true) }

                viewModelScope.launch {
                    val result = if (currentIssueId.isNotBlank()) {
                        workRepo.logWorkTime(currentIssueId, intent.minutes, intent.comment)
                    } else {
                        NetworkResult.Success(true)
                    }
                    setState { copy(isLoggingWork = false, isLogWorkDialogOpen = false) }

                    if (result is NetworkResult.Success) {
                        val addedHours = intent.minutes / 60.0
                        setState { copy(timeLoggedHours = timeLoggedHours + addedHours) }
                        sendEffect(IssueDetailEffect.ShowToast("کارکرد به میزان ${intent.minutes} دقیقه با موفقیت ثبت شد"))
                    } else {
                        val err = (result as? NetworkResult.Error)?.message ?: "خطا در ثبت کارکرد"
                        sendEffect(IssueDetailEffect.ShowToast(err))
                    }
                }
            }
            IssueDetailIntent.BackClicked -> {
                sendEffect(IssueDetailEffect.NavigateBack)
            }
            IssueDetailIntent.ShareClicked -> {
                sendEffect(IssueDetailEffect.ShowToast("لینک تسک کپی شد"))
            }
            IssueDetailIntent.CopyKeyClicked -> {
                sendEffect(IssueDetailEffect.ShowToast("شناسه ${currentState.issueKey} کپی شد"))
            }
        }
    }
}
