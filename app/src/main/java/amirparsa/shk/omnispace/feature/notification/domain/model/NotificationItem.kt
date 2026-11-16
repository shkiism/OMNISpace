package amirparsa.shk.omnispace.feature.notification.domain.model

data class NotificationItem(
    val id: String,
    val category: NotificationCategory,
    val title: String,
    val description: String,
    val timeAgo: String,
    val isUnread: Boolean,
    val iconKey: String = "notification",
    val iconTintHex: Long = 0xFFA78BFAL,
    val iconBgHex: Long = 0xFF2E1E5BL,
    val actionText: String? = null,
    val actionType: ActionType? = null,
    val actionTarget: String? = null
)
