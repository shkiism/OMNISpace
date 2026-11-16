package amirparsa.shk.omnispace.feature.chat.domain.model

data class ChatMessage(
    val id: String,
    val senderName: String,
    val senderInitials: String,
    val isFromMe: Boolean,
    val type: MessageType = MessageType.TEXT,
    val text: String? = null,
    val time: String,
    val voiceDuration: String? = null,
    val isPlayingVoice: Boolean = false,
    val fileName: String? = null,
    val fileSize: String? = null,
    val fileExtension: String? = null
)

