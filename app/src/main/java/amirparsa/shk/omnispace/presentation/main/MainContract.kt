package amirparsa.shk.omnispace.presentation.main

import amirparsa.shk.omnispace.core.base.mvi.UiEffect
import amirparsa.shk.omnispace.core.base.mvi.UiIntent
import amirparsa.shk.omnispace.core.base.mvi.UiState
import amirparsa.shk.omnispace.core.designsystem.component.OmniNavigationTab
import amirparsa.shk.omnispace.core.utils.DateUtils
import amirparsa.shk.omnispace.feature.connect.data.remote.dto.PersonDto
import amirparsa.shk.omnispace.feature.work.data.remote.dto.ProjectDto
import amirparsa.shk.omnispace.presentation.main.model.ChatNavArgs

/**
 * Immutable UI State for the Main Application Scaffold.
 */
data class MainUiState(
    val currentTab: OmniNavigationTab = OmniNavigationTab.TODAY,
    val selectedIssueKey: String? = null,
    val selectedChat: ChatNavArgs? = null,
    val isInVideoCall: Boolean = false,
    val isNotificationsOpen: Boolean = false,
    val isSystemNotificationShadeOpen: Boolean = false,
    val isZephyrQaOpen: Boolean = false,
    val selectedWikiTitle: String? = null,
    val selectedWikiId: String? = null,
    val isLeaveRequestOpen: Boolean = false,
    val isGlobalSearchOpen: Boolean = false,
    val selectedColleagueName: String? = null,
    val isKanbanBoardOpen: Boolean = false,
    val isWikiTreeOpen: Boolean = false,
    val isCreateIssueOpen: Boolean = false,
    val availableProjects: List<ProjectDto> = emptyList(),
    val availableAssignees: List<PersonDto> = emptyList(),
    val unreadCount: Int = 0,
    val userDisplayName: String = "",
    val organizationName: String = "",
    val isAuthenticated: Boolean = false,
    val isProfileDialogOpen: Boolean = false,
    val todayJalali: String = DateUtils.getCurrentJalaliDate().formatFa()
) : UiState

/**
 * User Intents for Main Scaffold.
 */
sealed interface MainIntent : UiIntent {
    data class SelectTab(val tab: OmniNavigationTab) : MainIntent
    data class OpenIssueDetail(val issueKey: String) : MainIntent
    data object CloseIssueDetail : MainIntent
    data class OpenChat(val chatArgs: ChatNavArgs) : MainIntent
    data object CloseChat : MainIntent
    data object StartVideoCall : MainIntent
    data object LeaveVideoCall : MainIntent
    data object OpenNotifications : MainIntent
    data object CloseNotifications : MainIntent
    data object OpenSystemNotificationShade : MainIntent
    data object CloseSystemNotificationShade : MainIntent
    data object OpenZephyrQa : MainIntent
    data object CloseZephyrQa : MainIntent
    data class OpenWikiArticle(val title: String, val articleId: String? = null) : MainIntent
    data object CloseWikiArticle : MainIntent
    data object OpenGlobalSearch : MainIntent
    data object CloseGlobalSearch : MainIntent
    data class OpenColleagueProfile(val name: String) : MainIntent
    data object CloseColleagueProfile : MainIntent
    data object OpenKanbanBoard : MainIntent
    data object CloseKanbanBoard : MainIntent
    data object OpenWikiTree : MainIntent
    data object CloseWikiTree : MainIntent
    data class SetCreateIssueOpen(val isOpen: Boolean) : MainIntent
    data class SubmitNewIssue(
        val title: String,
        val project: String,
        val type: String,
        val priority: String,
        val assignee: String,
        val projectId: String? = null,
        val assigneeId: String? = null,
        val description: String? = null
    ) : MainIntent
    data class SetLeaveRequestOpen(val isOpen: Boolean) : MainIntent
    data class SubmitLeaveRequest(
        val typeName: String,
        val fromDate: String,
        val toDate: String,
        val standInColleagueName: String,
        val description: String
    ) : MainIntent
    data class HandleIncomingDestination(val destination: String, val issueKey: String?) : MainIntent
    data class ShowToastMessage(val message: String, val isLong: Boolean = false) : MainIntent
    data object Logout : MainIntent
    data object RefreshSession : MainIntent
    data object ToggleProfileDialog : MainIntent
}

/**
 * Side Effects for Main Scaffold.
 */
sealed interface MainEffect : UiEffect {
    data class ShowToast(val message: String) : MainEffect
    data class ShowLongToast(val message: String) : MainEffect
}
