package amirparsa.shk.omnispace.feature.issue.presentation

import amirparsa.shk.omnispace.core.base.mvi.UiEffect
import amirparsa.shk.omnispace.core.base.mvi.UiIntent
import amirparsa.shk.omnispace.core.base.mvi.UiState
import amirparsa.shk.omnispace.feature.issue.domain.model.IssueChecklistItem
import amirparsa.shk.omnispace.feature.issue.domain.model.IssueCommentUi
import amirparsa.shk.omnispace.feature.issue.domain.model.IssueStatusType
import amirparsa.shk.omnispace.feature.work.data.remote.dto.IssueTransitionDto
import androidx.compose.ui.graphics.Color

val IssueStatusType.color: Color get() = Color(this.colorHex)

/**
 * UI sub-tabs for Issue Detail activity.
 * Belonging to Presentation Layer (NOT Domain).
 */
enum class IssueActivityTab(val labelFa: String, val count: Int? = null) {
    COMMENTS("دیدگاه‌ها"),
    HISTORY("تاریخچه"),
    ATTACHMENTS("فایل‌ها", null),
    QA_TESTS("تست‌های QA", null)
}

/**
 * Immutable UI State for Issue Detail screen.
 */
data class IssueDetailUiState(
    val issueId: String = "",
    val issueKey: String = "",
    val title: String = "",
    val description: String = "",
    val currentStatus: IssueStatusType = IssueStatusType.TODO,
    val availableTransitions: List<IssueTransitionDto> = emptyList(),
    val isTransitioning: Boolean = false,
    val selectedTab: IssueActivityTab = IssueActivityTab.COMMENTS,
    val checklistItems: List<IssueChecklistItem> = emptyList(),
    val comments: List<IssueCommentUi> = emptyList(),
    val commentInputText: String = "",
    val isSubmittingComment: Boolean = false,
    val timeLoggedHours: Double = 0.0,
    val totalEstimatedHours: Double = 0.0,
    val assigneeName: String = "—",
    val assigneeInitials: String = "—",
    val priorityName: String = "متوسط",
    val sprintName: String = "—",
    val estimateStoryPoints: String = "—",
    val dueDateText: String = "—",
    val isLogWorkDialogOpen: Boolean = false,
    val isLoggingWork: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
) : UiState {
    val completedChecklistCount: Int
        get() = checklistItems.count { it.isDone }
}

/**
 * User Intents for Issue Detail screen.
 */
sealed interface IssueDetailIntent : UiIntent {
    data class LoadIssue(val key: String) : IssueDetailIntent
    data class ChangeStatus(val status: IssueStatusType) : IssueDetailIntent
    data class ExecuteTransition(val transitionId: String, val targetStatusName: String? = null) : IssueDetailIntent
    data class SelectTab(val tab: IssueActivityTab) : IssueDetailIntent
    data class ToggleChecklistItem(val id: String) : IssueDetailIntent
    data class UpdateCommentInput(val text: String) : IssueDetailIntent
    data object SubmitComment : IssueDetailIntent
    data object OpenLogWorkDialog : IssueDetailIntent
    data object DismissLogWorkDialog : IssueDetailIntent
    data class SubmitLogWork(val minutes: Int, val comment: String? = null) : IssueDetailIntent
    data object BackClicked : IssueDetailIntent
    data object ShareClicked : IssueDetailIntent
    data object CopyKeyClicked : IssueDetailIntent
}

/**
 * Side effects for Issue Detail screen.
 */
sealed interface IssueDetailEffect : UiEffect {
    data object NavigateBack : IssueDetailEffect
    data class ShowToast(val message: String) : IssueDetailEffect
}

