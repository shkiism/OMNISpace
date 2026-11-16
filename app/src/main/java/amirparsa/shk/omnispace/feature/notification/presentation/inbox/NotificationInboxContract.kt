package amirparsa.shk.omnispace.feature.notification.presentation.inbox

import amirparsa.shk.omnispace.core.base.mvi.UiEffect
import amirparsa.shk.omnispace.core.base.mvi.UiIntent
import amirparsa.shk.omnispace.core.base.mvi.UiState
import amirparsa.shk.omnispace.feature.notification.domain.model.NotificationCategory
import amirparsa.shk.omnispace.feature.notification.domain.model.NotificationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

val NotificationItem.iconTint: Color get() = Color(this.iconTintHex)
val NotificationItem.iconBg: Color get() = Color(this.iconBgHex)
val NotificationItem.icon: ImageVector
    get() = when (iconKey) {
        "assignment" -> Icons.AutoMirrored.Filled.Assignment
        "bug" -> Icons.Default.BugReport
        "check" -> Icons.Default.CheckCircle
        "payment" -> Icons.Default.Payments
        "video" -> Icons.Default.VideoCall
        else -> Icons.Default.Notifications
    }

/**
 * Immutable UI State for Notification Inbox.
 */
data class NotificationInboxUiState(
    val selectedCategory: NotificationCategory = NotificationCategory.ALL,
    val notifications: List<NotificationItem> = emptyList(),
    val isLoading: Boolean = false
) : UiState {
    val unreadCount: Int
        get() = notifications.count { it.isUnread }

    val filteredList: List<NotificationItem>
        get() = if (selectedCategory == NotificationCategory.ALL) {
            notifications
        } else {
            notifications.filter { it.category == selectedCategory }
        }
}

/**
 * User Intents for Notification Inbox.
 */
sealed interface NotificationInboxIntent : UiIntent {
    data class SelectCategory(val category: NotificationCategory) : NotificationInboxIntent
    data object MarkAllAsRead : NotificationInboxIntent
    data class MarkAsRead(val id: String) : NotificationInboxIntent
    data class NotificationClicked(val item: NotificationItem) : NotificationInboxIntent
    data class ActionClicked(val item: NotificationItem) : NotificationInboxIntent
    data object BackClicked : NotificationInboxIntent
    data object OpenSystemNotificationShadeClicked : NotificationInboxIntent
}

/**
 * One-off side effects for Notification Inbox.
 */
sealed interface NotificationInboxEffect : UiEffect {
    data object NavigateBack : NotificationInboxEffect
    data class OpenIssue(val key: String) : NotificationInboxEffect
    data object JoinMeeting : NotificationInboxEffect
    data object ViewPayslip : NotificationInboxEffect
    data object OpenZephyrQa : NotificationInboxEffect
    data object OpenSystemNotificationShade : NotificationInboxEffect
    data class ShowToast(val message: String) : NotificationInboxEffect
}

