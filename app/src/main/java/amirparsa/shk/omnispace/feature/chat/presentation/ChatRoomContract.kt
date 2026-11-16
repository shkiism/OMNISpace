package amirparsa.shk.omnispace.feature.chat.presentation

import amirparsa.shk.omnispace.core.base.mvi.UiEffect
import amirparsa.shk.omnispace.core.base.mvi.UiIntent
import amirparsa.shk.omnispace.core.base.mvi.UiState
import amirparsa.shk.omnispace.feature.chat.domain.model.ChatMessage

/**
 * Immutable UI State for Chat Room.
 */
data class ChatRoomUiState(
    val roomId: String = "",
    val roomTitle: String = "",
    val roomSubtitle: String = "",
    val linkedIssueKey: String? = null,
    val linkedIssueTitle: String? = null,
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val errorMessage: String? = null
) : UiState

/**
 * User Intents for Chat Room.
 */
sealed interface ChatRoomIntent : UiIntent {
    data class LoadMessages(val roomId: String) : ChatRoomIntent
    data class UpdateInput(val text: String) : ChatRoomIntent
    data object SendMessage : ChatRoomIntent
    data class SendMessageText(val text: String) : ChatRoomIntent
    data class ToggleVoicePlayback(val messageId: String) : ChatRoomIntent
    data class DownloadAttachment(val messageId: String) : ChatRoomIntent
    data object BackClicked : ChatRoomIntent
    data object StartVideoCallClicked : ChatRoomIntent
    data object StartVoiceCallClicked : ChatRoomIntent
    data class OpenLinkedIssueClicked(val key: String) : ChatRoomIntent
}

/**
 * One-off side effects for Chat Room.
 */
sealed interface ChatRoomEffect : UiEffect {
    data object NavigateBack : ChatRoomEffect
    data object StartVideoCall : ChatRoomEffect
    data object StartVoiceCall : ChatRoomEffect
    data class OpenLinkedIssue(val key: String) : ChatRoomEffect
    data class ShowToast(val message: String) : ChatRoomEffect
    data object ScrollToBottom : ChatRoomEffect
}

