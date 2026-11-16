package amirparsa.shk.omnispace.feature.work.presentation.kanban

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import amirparsa.shk.omnispace.core.base.mvi.MviViewModel
import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.work.data.remote.dto.IssueDto
import amirparsa.shk.omnispace.feature.work.data.remote.dto.WorkflowTransitionDto
import amirparsa.shk.omnispace.feature.work.domain.model.KanbanCardItem
import amirparsa.shk.omnispace.feature.work.domain.model.KanbanColumnStatus
import amirparsa.shk.omnispace.feature.work.domain.repository.WorkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InteractiveKanbanViewModel @Inject constructor(
    private val workRepo: WorkRepository
) : MviViewModel<InteractiveKanbanUiState, InteractiveKanbanIntent, InteractiveKanbanEffect>(
    initialState = InteractiveKanbanUiState(
        cards = emptyList()
    )
) {
    private var activeProjectId: String? = null
    private var transitions: List<WorkflowTransitionDto> = emptyList()
    private var statusToCategoryMap: Map<String, KanbanColumnStatus> = emptyMap()
    private var categoryToStatusIdMap: Map<KanbanColumnStatus, String> = emptyMap()

    init {
        loadKanbanData()
    }

    private fun mapIssueToCard(issue: IssueDto, colStatus: KanbanColumnStatus): KanbanCardItem {
        return KanbanCardItem(
            id = issue.id,
            key = issue.key,
            title = issue.title,
            type = "تسک",
            typeColor = 0xFF7C3AEDL,
            status = colStatus,
            priorityName = when {
                issue.priorityId?.contains("blocker") == true -> "بحرانی"
                issue.priorityId?.contains("high") == true -> "فوری"
                else -> "عادی"
            },
            priorityColor = when {
                issue.priorityId?.contains("blocker") == true -> 0xFFEF4444L
                issue.priorityId?.contains("high") == true -> 0xFFF59E0BL
                else -> 0xFF10B981L
            },
            assigneeInitials = if (!issue.assigneeId.isNullOrBlank()) "هم" else "—",
            dueDate = issue.dueAt?.take(10) ?: "اسپرینت جاری"
        )
    }

    private fun loadKanbanData() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            val projectsResult = workRepo.getProjects()
            if (projectsResult is NetworkResult.Success && projectsResult.data.isNotEmpty()) {
                val project = projectsResult.data.first()
                activeProjectId = project.id
                setState { copy(projectName = "پروژه ${project.name} (${project.key})") }

                // Load workflow statuses
                val workflowResult = workRepo.getProjectWorkflow(project.id)
                if (workflowResult is NetworkResult.Success) {
                    transitions = workflowResult.data.transitions
                    val sMap = mutableMapOf<String, KanbanColumnStatus>()
                    val cMap = mutableMapOf<KanbanColumnStatus, String>()
                    workflowResult.data.statuses.forEach { s ->
                        val colStatus = when (s.category.lowercase()) {
                            "todo" -> KanbanColumnStatus.TODO
                            "in_progress" -> KanbanColumnStatus.IN_PROGRESS
                            "done" -> KanbanColumnStatus.DONE
                            else -> KanbanColumnStatus.QA_REVIEW
                        }
                        sMap[s.id] = colStatus
                        if (!cMap.containsKey(colStatus)) {
                            cMap[colStatus] = s.id
                        }
                    }
                    statusToCategoryMap = sMap
                    categoryToStatusIdMap = cMap
                }

                // Load live issues
                val issuesResult = workRepo.getIssues(project.id)
                if (issuesResult is NetworkResult.Success) {
                    val liveCards = issuesResult.data.map { issue ->
                        val colStatus = statusToCategoryMap[issue.statusId.orEmpty()] ?: KanbanColumnStatus.TODO
                        mapIssueToCard(issue, colStatus)
                    }

                    setState { copy(cards = liveCards, isLoading = false, error = null) }
                } else {
                    val err = (issuesResult as? NetworkResult.Error)?.message ?: "خطا در دریافت تسک‌ها"
                    setState { copy(isLoading = false, error = err) }
                }
            } else {
                val err = (projectsResult as? NetworkResult.Error)?.message ?: "پروژه‌ای یافت نشد"
                setState { copy(isLoading = false, error = err) }
            }
        }
    }

    private fun reloadIssuesOnly() {
        val pid = activeProjectId ?: return
        viewModelScope.launch {
            val issuesResult = workRepo.getIssues(pid)
            if (issuesResult is NetworkResult.Success) {
                val liveCards = issuesResult.data.map { issue ->
                    val colStatus = statusToCategoryMap[issue.statusId.orEmpty()] ?: KanbanColumnStatus.TODO
                    mapIssueToCard(issue, colStatus)
                }
                setState { copy(cards = liveCards) }
            }
        }
    }

    override fun processIntent(intent: InteractiveKanbanIntent) {
        when (intent) {
            InteractiveKanbanIntent.BackClicked -> {
                sendEffect(InteractiveKanbanEffect.NavigateBack)
            }
            InteractiveKanbanIntent.NewTaskClicked -> {
                sendEffect(InteractiveKanbanEffect.OpenNewTask)
            }
            InteractiveKanbanIntent.Refresh -> {
                loadKanbanData()
            }
            is InteractiveKanbanIntent.TaskClicked -> {
                sendEffect(InteractiveKanbanEffect.OpenTask(intent.key))
            }
            is InteractiveKanbanIntent.TransitionCard -> {
                viewModelScope.launch {
                    val res = workRepo.transitionIssue(intent.cardId, intent.transitionId)
                    if (res is NetworkResult.Success) {
                        sendEffect(InteractiveKanbanEffect.ShowToast("وضعیت تسک با موفقیت تغییر یافت"))
                        reloadIssuesOnly()
                    } else {
                        val errMsg = (res as? NetworkResult.Error)?.message ?: "خطا در تغییر وضعیت تسک"
                        sendEffect(InteractiveKanbanEffect.ShowToast(errMsg))
                    }
                }
            }
            is InteractiveKanbanIntent.MoveCard -> {
                val card = currentState.cards.firstOrNull { it.id == intent.cardId } ?: return
                val originalStatus = card.status
                if (originalStatus == intent.newStatus) return

                // Optimistic UI state update
                val updatedCards = currentState.cards.map { c ->
                    if (c.id == intent.cardId) {
                        c.copy(status = intent.newStatus)
                    } else c
                }
                setState { copy(cards = updatedCards) }

                viewModelScope.launch {
                    val targetStatusId = categoryToStatusIdMap[intent.newStatus]

                    // Query issue-specific transitions first
                    val issueTransitionsRes = workRepo.getIssueTransitions(card.id)
                    val issueTransitions = (issueTransitionsRes as? NetworkResult.Success)?.data.orEmpty()

                    val transitionIdToUse: String? = issueTransitions.firstOrNull { t ->
                        val mappedStatus = statusToCategoryMap[t.resolvedToStatusId.orEmpty()]
                        mappedStatus == intent.newStatus || (targetStatusId != null && t.resolvedToStatusId == targetStatusId)
                    }?.id ?: issueTransitions.firstOrNull { t ->
                        t.name.contains(intent.newStatus.titleFa, ignoreCase = true)
                    }?.id ?: transitions.firstOrNull { t ->
                        targetStatusId != null && t.toStatusId == targetStatusId
                    }?.id ?: transitions.firstOrNull()?.id

                    if (transitionIdToUse != null) {
                        val transitionResult = workRepo.transitionIssue(card.id, transitionIdToUse)
                        if (transitionResult is NetworkResult.Success) {
                            sendEffect(InteractiveKanbanEffect.ShowToast("تسک ${card.key} به ستون «${intent.newStatus.titleFa}» منتقل شد."))
                            // Reload issues upon successful transition
                            reloadIssuesOnly()
                        } else {
                            // Revert on failure
                            val revertedCards = currentState.cards.map { c ->
                                if (c.id == intent.cardId) c.copy(status = originalStatus) else c
                            }
                            val errMsg = (transitionResult as? NetworkResult.Error)?.message ?: "خطا در انتقال ستون تسک"
                            setState { copy(cards = revertedCards) }
                            sendEffect(InteractiveKanbanEffect.ShowToast(errMsg))
                        }
                    } else {
                        sendEffect(InteractiveKanbanEffect.ShowToast("تسک ${card.key} به ستون «${intent.newStatus.titleFa}» منتقل شد (محلی)"))
                    }
                }
            }
        }
    }
}
