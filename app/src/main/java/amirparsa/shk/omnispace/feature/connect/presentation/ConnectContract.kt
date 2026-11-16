package amirparsa.shk.omnispace.feature.connect.presentation

import amirparsa.shk.omnispace.core.base.mvi.UiEffect
import amirparsa.shk.omnispace.core.base.mvi.UiIntent
import amirparsa.shk.omnispace.core.base.mvi.UiState
import amirparsa.shk.omnispace.feature.connect.data.remote.dto.PersonDto
import amirparsa.shk.omnispace.feature.connect.data.remote.dto.TeamDto
import amirparsa.shk.omnispace.feature.connect.domain.model.ChatItemUi
import amirparsa.shk.omnispace.feature.connect.domain.model.LiveKitRoomItem

/**
 * UI sub-tabs for Connect screen.
 * Belonging to Presentation Layer (NOT Domain).
 */
enum class ConnectSubTab(val titleFa: String, val count: Int? = null) {
    CHATS_CHANNELS("پیام‌ها و کانال‌ها"),
    LIVEKIT_MEETINGS("جلسات ویدیویی LiveKit"),
    PEOPLE_DIRECTORY("دایرکتوری همکاران"),
    ALL("همه")
}

/**
 * Immutable UI State for Connect screen.
 */
data class ConnectUiState(
    val selectedSubTab: ConnectSubTab = ConnectSubTab.CHATS_CHANNELS,
    val chatList: List<ChatItemUi> = emptyList(),
    val peopleList: List<PersonDto> = emptyList(),
    val teamsList: List<TeamDto> = emptyList(),
    val liveRooms: List<LiveKitRoomItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : UiState

/**
 * User Intents for Connect screen.
 */
sealed interface ConnectIntent : UiIntent {
    data class SelectSubTab(val tab: ConnectSubTab) : ConnectIntent
    data class ChatClicked(val chat: ChatItemUi) : ConnectIntent
    data object JoinLiveKitClicked : ConnectIntent
    data object NewInstantMeetingClicked : ConnectIntent
    data object ScheduleMeetingClicked : ConnectIntent
    data class ColleagueClicked(val name: String) : ConnectIntent
    data object Refresh : ConnectIntent
}

/**
 * Side effects for Connect screen.
 */
sealed interface ConnectEffect : UiEffect {
    data class OpenChat(val chat: ChatItemUi) : ConnectEffect
    data object JoinLiveKit : ConnectEffect
    data object NewInstantMeeting : ConnectEffect
    data object ScheduleMeeting : ConnectEffect
    data class OpenColleague(val name: String) : ConnectEffect
    data class ShowToast(val message: String) : ConnectEffect
}
