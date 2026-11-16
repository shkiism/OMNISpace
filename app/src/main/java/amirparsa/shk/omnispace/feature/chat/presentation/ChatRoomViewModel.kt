package amirparsa.shk.omnispace.feature.chat.presentation

import android.content.Context
import androidx.lifecycle.viewModelScope
import amirparsa.shk.omnispace.core.base.mvi.MviViewModel
import amirparsa.shk.omnispace.core.network.SessionManager
import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.chat.domain.model.ChatMessage
import amirparsa.shk.omnispace.feature.chat.domain.model.MessageType
import amirparsa.shk.omnispace.feature.connect.data.remote.dto.ChatMessageDto
import amirparsa.shk.omnispace.feature.connect.domain.repository.ConnectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val connectRepository: ConnectRepository,
    @ApplicationContext private val context: Context
) : MviViewModel<ChatRoomUiState, ChatRoomIntent, ChatRoomEffect>(
    initialState = ChatRoomUiState(
        messages = emptyList()
    )
) {
    private val sessionManager = SessionManager.getInstance(context)

    fun loadMessages(roomId: String) {
        onIntent(ChatRoomIntent.LoadMessages(roomId))
    }

    fun sendMessage(text: String) {
        onIntent(ChatRoomIntent.SendMessageText(text))
    }

    override fun processIntent(intent: ChatRoomIntent) {
        when (intent) {
            is ChatRoomIntent.LoadMessages -> {
                setState { copy(roomId = intent.roomId, isLoading = true, errorMessage = null) }
                viewModelScope.launch {
                    when (val result = connectRepository.getChatMessages(intent.roomId)) {
                        is NetworkResult.Success -> {
                            val list = result.data.map { dtoToChatMessage(it) }
                            setState {
                                copy(
                                    messages = list,
                                    isLoading = false,
                                    errorMessage = null
                                )
                            }
                            sendEffect(ChatRoomEffect.ScrollToBottom)
                        }
                        is NetworkResult.Error -> {
                            setState {
                                copy(
                                    isLoading = false,
                                    errorMessage = result.message
                                )
                            }
                        }
                        NetworkResult.Loading -> {
                            setState { copy(isLoading = true) }
                        }
                    }
                }
            }

            is ChatRoomIntent.UpdateInput -> {
                setState { copy(inputText = intent.text) }
            }

            ChatRoomIntent.SendMessage -> {
                handleSendMessage(currentState.inputText)
            }

            is ChatRoomIntent.SendMessageText -> {
                handleSendMessage(intent.text)
            }

            is ChatRoomIntent.ToggleVoicePlayback -> {
                setState {
                    copy(
                        messages = messages.map {
                            if (it.id == intent.messageId) {
                                it.copy(isPlayingVoice = !it.isPlayingVoice)
                            } else {
                                it.copy(isPlayingVoice = false)
                            }
                        }
                    )
                }
            }

            is ChatRoomIntent.DownloadAttachment -> {
                sendEffect(ChatRoomEffect.ShowToast("دانلود فایل پیوست آغاز شد"))
            }

            ChatRoomIntent.BackClicked -> {
                sendEffect(ChatRoomEffect.NavigateBack)
            }

            ChatRoomIntent.StartVideoCallClicked -> {
                sendEffect(ChatRoomEffect.StartVideoCall)
            }

            ChatRoomIntent.StartVoiceCallClicked -> {
                sendEffect(ChatRoomEffect.StartVoiceCall)
            }

            is ChatRoomIntent.OpenLinkedIssueClicked -> {
                sendEffect(ChatRoomEffect.OpenLinkedIssue(intent.key))
            }
        }
    }

    private fun handleSendMessage(text: String) {
        val trimmed = text.trim()
        val roomId = currentState.roomId.ifBlank { currentState.linkedIssueKey.orEmpty() }
        if (trimmed.isNotBlank() && roomId.isNotBlank()) {
            setState { copy(isSending = true) }
            viewModelScope.launch {
                when (val result = connectRepository.sendChatMessage(roomId, trimmed)) {
                    is NetworkResult.Success -> {
                        val newMsg = dtoToChatMessage(result.data, fallbackText = trimmed)
                        setState {
                            copy(
                                messages = messages + newMsg,
                                inputText = "",
                                isSending = false
                            )
                        }
                        sendEffect(ChatRoomEffect.ScrollToBottom)
                    }
                    is NetworkResult.Error -> {
                        // Fallback message creation if server error so chat doesn't block
                        val fallbackMsg = ChatMessage(
                            id = System.currentTimeMillis().toString(),
                            senderName = sessionManager.displayName ?: "",
                            senderInitials = getInitials(sessionManager.displayName ?: ""),
                            isFromMe = true,
                            type = MessageType.TEXT,
                            text = trimmed,
                            time = "هم‌اکنون"
                        )
                        setState {
                            copy(
                                messages = messages + fallbackMsg,
                                inputText = "",
                                isSending = false
                            )
                        }
                        sendEffect(ChatRoomEffect.ScrollToBottom)
                        sendEffect(ChatRoomEffect.ShowToast(result.message))
                    }
                    NetworkResult.Loading -> {
                        setState { copy(isSending = true) }
                    }
                }
            }
        }
    }

    private fun dtoToChatMessage(dto: ChatMessageDto, fallbackText: String? = null): ChatMessage {
        val myUserId = sessionManager.userId
        val myDisplayName = sessionManager.displayName ?: ""

        val sender = dto.senderName ?: dto.authorName ?: myDisplayName
        val isMe = dto.isFromMe == true ||
                (dto.senderId != null && dto.senderId == myUserId) ||
                (dto.senderName != null && dto.senderName == myDisplayName) ||
                (dto.senderId == null && dto.senderName == null)

        val type = when (dto.type?.uppercase(Locale.US)) {
            "VOICE", "VOICE_NOTE" -> MessageType.VOICE_NOTE
            "FILE", "FILE_ATTACHMENT" -> MessageType.FILE_ATTACHMENT
            else -> MessageType.TEXT
        }

        val initials = getInitials(sender)
        val text = dto.text ?: dto.content ?: fallbackText
        val time = formatMessageTime(dto.createdAt ?: dto.timestamp)

        return ChatMessage(
            id = dto.id.ifBlank { System.currentTimeMillis().toString() },
            senderName = sender,
            senderInitials = initials,
            isFromMe = isMe,
            type = type,
            text = text,
            time = time
        )
    }

    private fun getInitials(name: String): String {
        val parts = name.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
        return when {
            parts.size >= 2 -> "${parts[0].take(1)}${parts[1].take(1)}"
            parts.isNotEmpty() -> parts[0].take(2)
            else -> "ام"
        }
    }

    private fun formatMessageTime(rawTime: String?): String {
        if (rawTime.isNullOrBlank()) return "هم‌اکنون"
        if (rawTime.length <= 5 && rawTime.contains(":")) return rawTime
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = parser.parse(rawTime)
            if (date != null) {
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
            } else {
                rawTime.takeLast(5)
            }
        } catch (_: Exception) {
            try {
                val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = parser.parse(rawTime)
                if (date != null) {
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
                } else {
                    rawTime
                }
            } catch (_: Exception) {
                rawTime
            }
        }
    }
}

