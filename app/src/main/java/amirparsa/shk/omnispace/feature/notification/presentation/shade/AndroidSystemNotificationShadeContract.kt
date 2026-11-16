package amirparsa.shk.omnispace.feature.notification.presentation.shade

import amirparsa.shk.omnispace.core.base.mvi.UiEffect
import amirparsa.shk.omnispace.core.base.mvi.UiIntent
import amirparsa.shk.omnispace.core.base.mvi.UiState
import amirparsa.shk.omnispace.core.utils.DateUtils

/**
 * Immutable UI State for Android System Notification Shade simulation.
 */
data class AndroidSystemNotificationShadeUiState(
    val isCallDismissed: Boolean = false,
    val isFocusRunning: Boolean = false,
    val isFocusPaused: Boolean = false,
    val focusProgress: Float = 0f,
    val activeTaskTitle: String = "تمرکز آزاد",
    val callerName: String = "جلسه ویدیویی",
    val callMeetingTitle: String = "اتاق گفتگوی تیمی",
    val mentionUser: String = "مدیر پروژه",
    val mentionTaskKey: String = "تسک سازمانی",
    val mentionTaskTitle: String = "بررسی تسک سازمانی",
    val mentionCommentSnippet: String = "لطفاً موارد محوله را بررسی فرمایید.",
    val hrNotificationTitle: String = "درخواست مرخصی تایید شد",
    val hrNotificationMessage: String = "درخواست مرخصی ثبت شده شما در سامانه اداری تایید گردید.",
    val currentTimeString: String = DateUtils.getCurrentTimeString(),
    val currentDateString: String = DateUtils.getCurrentJalaliDate().formatFa(),
    val weatherMeetingSummary: String = "وضعیت آنلاین",
    val batteryPercentage: Int = 100
) : UiState

/**
 * User Intents for Android System Notification Shade.
 */
sealed interface AndroidSystemNotificationShadeIntent : UiIntent {
    data object BackClicked : AndroidSystemNotificationShadeIntent
    data object JoinCallClicked : AndroidSystemNotificationShadeIntent
    data object DismissCallClicked : AndroidSystemNotificationShadeIntent
    data object PauseFocusClicked : AndroidSystemNotificationShadeIntent
    data object ResumeFocusClicked : AndroidSystemNotificationShadeIntent
    data object StopFocusClicked : AndroidSystemNotificationShadeIntent
    data class OpenIssueClicked(val key: String) : AndroidSystemNotificationShadeIntent
    data object OpenHrClicked : AndroidSystemNotificationShadeIntent

    // Native Android Notification Trigger Intents
    data object TriggerNativeCallNotification : AndroidSystemNotificationShadeIntent
    data object TriggerNativeFocusNotification : AndroidSystemNotificationShadeIntent
    data object TriggerNativeTaskNotification : AndroidSystemNotificationShadeIntent
    data object TriggerNativeHrNotification : AndroidSystemNotificationShadeIntent
    data object DismissAllNativeNotifications : AndroidSystemNotificationShadeIntent
}

/**
 * One-off side effects for Android System Notification Shade.
 */
sealed interface AndroidSystemNotificationShadeEffect : UiEffect {
    data object NavigateBack : AndroidSystemNotificationShadeEffect
    data object JoinVideoCall : AndroidSystemNotificationShadeEffect
    data class OpenIssue(val key: String) : AndroidSystemNotificationShadeEffect
    data object OpenHr : AndroidSystemNotificationShadeEffect
    data class ShowToast(val message: String) : AndroidSystemNotificationShadeEffect
    data object FireNativeCallNotification : AndroidSystemNotificationShadeEffect
    data object FireNativeFocusNotification : AndroidSystemNotificationShadeEffect
    data object FireNativeTaskNotification : AndroidSystemNotificationShadeEffect
    data object FireNativeHrNotification : AndroidSystemNotificationShadeEffect
    data object ClearAllNativeNotifications : AndroidSystemNotificationShadeEffect
}

