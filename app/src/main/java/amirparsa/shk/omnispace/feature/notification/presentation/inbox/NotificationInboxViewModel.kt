package amirparsa.shk.omnispace.feature.notification.presentation.inbox

import android.app.Application
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import amirparsa.shk.omnispace.core.base.mvi.MviViewModel
import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.notification.domain.model.ActionType
import amirparsa.shk.omnispace.feature.notification.domain.model.NotificationCategory
import amirparsa.shk.omnispace.feature.notification.domain.model.NotificationItem
import amirparsa.shk.omnispace.feature.notification.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationInboxViewModel @Inject constructor(
    private val notifRepo: NotificationRepository
) : MviViewModel<NotificationInboxUiState, NotificationInboxIntent, NotificationInboxEffect>(
    initialState = NotificationInboxUiState(
        notifications = emptyList()
    )
) {

    init {
        loadLiveNotifications()
    }

    private fun loadLiveNotifications() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = notifRepo.getNotifications()
            if (result is NetworkResult.Success && result.data.items.isNotEmpty()) {
                val liveList = result.data.items.map { dto ->
                    NotificationItem(
                        id = dto.id,
                        category = NotificationCategory.ALL,
                        title = dto.title,
                        description = dto.body ?: "اعلان جدید سیستم OMNISpace",
                        timeAgo = dto.createdAt?.take(10) ?: "هم‌اکنون",
                        isUnread = true,
                        iconKey = "notification",
                        iconTintHex = 0xFFA78BFAL,
                        iconBgHex = 0xFF2E1E5BL,
                        actionText = "مشاهده جزئیات",
                        actionType = ActionType.OPEN_ISSUE,
                        actionTarget = ""
                    )
                }
                setState { copy(notifications = liveList, isLoading = false) }
            } else {
                setState { copy(isLoading = false) }
            }
        }
    }

    override fun processIntent(intent: NotificationInboxIntent) {
        when (intent) {
            is NotificationInboxIntent.SelectCategory -> {
                setState { copy(selectedCategory = intent.category) }
            }
            NotificationInboxIntent.MarkAllAsRead -> {
                setState {
                    copy(notifications = notifications.map { it.copy(isUnread = false) })
                }
            }
            is NotificationInboxIntent.MarkAsRead -> {
                markItemAsRead(intent.id)
            }
            is NotificationInboxIntent.NotificationClicked -> {
                markItemAsRead(intent.item.id)
                handleAction(intent.item)
            }
            is NotificationInboxIntent.ActionClicked -> {
                markItemAsRead(intent.item.id)
                handleAction(intent.item)
            }
            NotificationInboxIntent.BackClicked -> {
                sendEffect(NotificationInboxEffect.NavigateBack)
            }
            NotificationInboxIntent.OpenSystemNotificationShadeClicked -> {
                sendEffect(NotificationInboxEffect.OpenSystemNotificationShade)
            }
        }
    }

    private fun markItemAsRead(id: String) {
        setState {
            copy(
                notifications = notifications.map {
                    if (it.id == id) it.copy(isUnread = false) else it
                }
            )
        }
    }

    private fun handleAction(item: NotificationItem) {
        when (item.actionType) {
            ActionType.OPEN_ISSUE -> {
                item.actionTarget?.let { sendEffect(NotificationInboxEffect.OpenIssue(it)) }
            }
            ActionType.JOIN_MEETING -> {
                sendEffect(NotificationInboxEffect.JoinMeeting)
            }
            ActionType.VIEW_PAYSLIP -> {
                sendEffect(NotificationInboxEffect.ViewPayslip)
            }
            ActionType.VIEW_QA -> {
                sendEffect(NotificationInboxEffect.OpenZephyrQa)
            }
            null -> Unit
        }
    }
}
