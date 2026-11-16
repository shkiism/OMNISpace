package amirparsa.shk.omnispace.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationDismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("notification_id", -1)
        if (notificationId != -1) {
            OmniNotificationManager.cancelNotification(context, notificationId)
        }
    }
}

