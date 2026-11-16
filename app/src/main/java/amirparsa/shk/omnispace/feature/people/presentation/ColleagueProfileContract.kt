package amirparsa.shk.omnispace.feature.people.presentation

import amirparsa.shk.omnispace.core.base.mvi.UiEffect
import amirparsa.shk.omnispace.core.base.mvi.UiIntent
import amirparsa.shk.omnispace.core.base.mvi.UiState
import amirparsa.shk.omnispace.feature.people.domain.model.SharedTask

/**
 * Immutable UI State for Colleague Profile screen.
 */
data class ColleagueProfileUiState(
    val colleagueName: String = "",
    val jobTitle: String = "",
    val department: String = "",
    val employeeCode: String = "",
    val isOnline: Boolean = false,
    val email: String = "",
    val selectedTab: Int = 0,
    val skills: List<String> = emptyList(),
    val sharedTasks: List<SharedTask> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : UiState

/**
 * User intents for Colleague Profile screen.
 */
sealed interface ColleagueProfileIntent : UiIntent {
    data class SelectTab(val index: Int) : ColleagueProfileIntent
    data object BackClicked : ColleagueProfileIntent
    data object StartChatClicked : ColleagueProfileIntent
    data object StartVideoCallClicked : ColleagueProfileIntent
    data object SendEmailClicked : ColleagueProfileIntent
    data class OpenTaskClicked(val issueKey: String) : ColleagueProfileIntent
}

/**
 * Side effects for Colleague Profile screen.
 */
sealed interface ColleagueProfileEffect : UiEffect {
    data object NavigateBack : ColleagueProfileEffect
    data class StartChat(val colleagueName: String) : ColleagueProfileEffect
    data class StartVideoCall(val colleagueName: String) : ColleagueProfileEffect
    data class SendEmail(val email: String) : ColleagueProfileEffect
    data class OpenTask(val issueKey: String) : ColleagueProfileEffect
}

