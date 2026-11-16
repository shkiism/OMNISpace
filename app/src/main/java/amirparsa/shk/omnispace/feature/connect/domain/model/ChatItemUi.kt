package amirparsa.shk.omnispace.feature.connect.domain.model

data class ChatItemUi(
    val id: String,
    val title: String,
    val subtitle: String,
    val time: String,
    val unreadCount: Int = 0,
    val isChannel: Boolean = false,
    val isAnnouncement: Boolean = false,
    val onlineStatusColor: Long? = null,
    val isReadByMe: Boolean = false
)
