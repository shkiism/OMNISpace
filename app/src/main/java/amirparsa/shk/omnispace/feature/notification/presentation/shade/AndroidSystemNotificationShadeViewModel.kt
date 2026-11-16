package amirparsa.shk.omnispace.feature.notification.presentation.shade

import amirparsa.shk.omnispace.core.base.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AndroidSystemNotificationShadeViewModel @Inject constructor() : MviViewModel<AndroidSystemNotificationShadeUiState, AndroidSystemNotificationShadeIntent, AndroidSystemNotificationShadeEffect>(
    initialState = AndroidSystemNotificationShadeUiState()
) {
    override fun processIntent(intent: AndroidSystemNotificationShadeIntent) {
        when (intent) {
            AndroidSystemNotificationShadeIntent.BackClicked -> {
                sendEffect(AndroidSystemNotificationShadeEffect.NavigateBack)
            }
            AndroidSystemNotificationShadeIntent.JoinCallClicked -> {
                sendEffect(AndroidSystemNotificationShadeEffect.ShowToast("اتصال به اتاق ویدیویی LiveKit..."))
                sendEffect(AndroidSystemNotificationShadeEffect.JoinVideoCall)
            }
            AndroidSystemNotificationShadeIntent.DismissCallClicked -> {
                setState { copy(isCallDismissed = true) }
                sendEffect(AndroidSystemNotificationShadeEffect.ShowToast("تماس رد شد"))
            }
            AndroidSystemNotificationShadeIntent.PauseFocusClicked -> {
                setState { copy(isFocusPaused = true) }
                sendEffect(AndroidSystemNotificationShadeEffect.ShowToast("تایمر فوکوس متوقف شد"))
            }
            AndroidSystemNotificationShadeIntent.ResumeFocusClicked -> {
                setState { copy(isFocusPaused = false) }
                sendEffect(AndroidSystemNotificationShadeEffect.ShowToast("تایمر فوکوس از سر گرفته شد"))
            }
            AndroidSystemNotificationShadeIntent.StopFocusClicked -> {
                setState { copy(isFocusRunning = false) }
                sendEffect(AndroidSystemNotificationShadeEffect.ShowToast("جلسه تمرکز پایان یافت و در تایم‌شیت ثبت شد"))
            }
            is AndroidSystemNotificationShadeIntent.OpenIssueClicked -> {
                sendEffect(AndroidSystemNotificationShadeEffect.OpenIssue(intent.key))
            }
            AndroidSystemNotificationShadeIntent.OpenHrClicked -> {
                sendEffect(AndroidSystemNotificationShadeEffect.OpenHr)
            }
            AndroidSystemNotificationShadeIntent.TriggerNativeCallNotification -> {
                sendEffect(AndroidSystemNotificationShadeEffect.FireNativeCallNotification)
                sendEffect(AndroidSystemNotificationShadeEffect.ShowToast("اعلان تماس ورودی به نوار سیستم ارسال شد"))
            }
            AndroidSystemNotificationShadeIntent.TriggerNativeFocusNotification -> {
                sendEffect(AndroidSystemNotificationShadeEffect.FireNativeFocusNotification)
                sendEffect(AndroidSystemNotificationShadeEffect.ShowToast("سرویس پیش‌زمینه فوکوس فعال شد"))
            }
            AndroidSystemNotificationShadeIntent.TriggerNativeTaskNotification -> {
                sendEffect(AndroidSystemNotificationShadeEffect.FireNativeTaskNotification)
                sendEffect(AndroidSystemNotificationShadeEffect.ShowToast("اعلان منشن تسک ارسال شد"))
            }
            AndroidSystemNotificationShadeIntent.TriggerNativeHrNotification -> {
                sendEffect(AndroidSystemNotificationShadeEffect.FireNativeHrNotification)
                sendEffect(AndroidSystemNotificationShadeEffect.ShowToast("اعلان فیش حقوقی ارسال شد"))
            }
            AndroidSystemNotificationShadeIntent.DismissAllNativeNotifications -> {
                sendEffect(AndroidSystemNotificationShadeEffect.ClearAllNativeNotifications)
                sendEffect(AndroidSystemNotificationShadeEffect.ShowToast("تمام اعلانات نوار سیستم پاک شدند"))
            }
        }
    }
}

