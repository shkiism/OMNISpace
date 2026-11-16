package amirparsa.shk.omnispace.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import amirparsa.shk.omnispace.MainActivity
import amirparsa.shk.omnispace.R

object OmniNotificationManager {

    const val CHANNEL_LIVEKIT_CALLS = "omni_livekit_calls"
    const val CHANNEL_FOCUS_TIMER = "omni_focus_timer"
    const val CHANNEL_TASKS_MENTIONS = "omni_tasks_mentions"
    const val CHANNEL_HR_APPROVALS = "omni_hr_approvals"

    const val NOTIFICATION_ID_CALL = 2001
    const val NOTIFICATION_ID_TASK = 2002
    const val NOTIFICATION_ID_FOCUS_DONE = 2003
    const val NOTIFICATION_ID_HR = 2004

    const val EXTRA_DESTINATION = "extra_destination"
    const val DEST_VIDEO_CALL = "dest_video_call"
    const val DEST_ISSUE_DETAIL = "dest_issue_detail"
    const val DEST_HR_GROWTH = "dest_hr_growth"
    const val DEST_NOTIFICATIONS = "dest_notifications"

    const val EXTRA_ISSUE_KEY = "extra_issue_key"
    const val EXTRA_ROOM_ID = "extra_room_id"

    const val ACTION_DISMISS_NOTIFICATION = "amirparsa.shk.omnispace.ACTION_DISMISS"

    fun initChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // 1. LiveKit Urgent Video Calls (Heads-up banner)
            val callChannel = NotificationChannel(
                CHANNEL_LIVEKIT_CALLS,
                "جلسات و تماس‌های فوری (LiveKit Calls)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "نمایش نوتیفیکیشن‌های سرصفحه (Heads-up) هنگام تماس تصویری یا دعوت به جلسه"
                enableLights(true)
                lightColor = Color.MAGENTA
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            // 2. Ongoing Focus Timer (Foreground Service)
            val focusChannel = NotificationChannel(
                CHANNEL_FOCUS_TIMER,
                "تایمر تمرکز و کارکرد (Focus Timer)",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "نمایش زنده شمارنده زمان کارکرد در پنل اعلان‌ها و صفحه قفل"
                enableLights(false)
                enableVibration(false)
                setShowBadge(false)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            // 3. Task Mentions
            val tasksChannel = NotificationChannel(
                CHANNEL_TASKS_MENTIONS,
                "منشن‌ها و وظایف سازمانی (Tasks & Mentions)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "نوتیفیکیشن منشن شدن شما در پروژه‌ها و تسک‌های سازمانی"
                enableLights(true)
                lightColor = Color.CYAN
                enableVibration(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE
            }

            // 4. HR & Payroll
            val hrChannel = NotificationChannel(
                CHANNEL_HR_APPROVALS,
                "امور پرسنلی و حقوق (HR & Payroll)",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "اعلانات تایید مرخصی، فیش حقوقی و گزارشات اداری"
                lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE
            }

            notificationManager.createNotificationChannels(
                listOf(callChannel, focusChannel, tasksChannel, hrChannel)
            )
        }
    }

    /**
     * Show high-priority heads-up notification for LiveKit incoming video call when outside app.
     */
    fun showUrgentCallNotification(
        context: Context,
        meetingTitle: String = "جلسه ویدیویی",
        callerName: String = "تماس دریافتی",
        roomId: String = "room-meeting"
    ) {
        val joinIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_DESTINATION, DEST_VIDEO_CALL)
            putExtra(EXTRA_ROOM_ID, roomId)
        }
        val joinPendingIntent = PendingIntent.getActivity(
            context,
            101,
            joinIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Dismiss intent
        val dismissIntent = Intent(context, NotificationDismissReceiver::class.java).apply {
            putExtra("notification_id", NOTIFICATION_ID_CALL)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            102,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_LIVEKIT_CALLS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("جلسه ویدیویی فوری: $meetingTitle")
            .setContentText("$callerName در حال برقراری تماس ویدیویی است...")
            .setSubText("LiveKit")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setAutoCancel(true)
            .setContentIntent(joinPendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "پیوستن به جلسه", joinPendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "رد تماس", dismissPendingIntent)
            .setColor(Color.parseColor("#9D7BF6"))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_CALL, notification)
        } catch (_: SecurityException) {
            // Permission not yet granted
        }
    }

    /**
     * Show Task Mention notification with deep-link directly into IssueDetailScreen.
     */
    fun showTaskMentionNotification(
        context: Context,
        issueKey: String = "تسک سازمانی",
        issueTitle: String = "وظیفه جدید",
        mentionUser: String = "همکار",
        commentSnippet: String = "شما در یک وظیفه منشن شده‌اید.",
        projectName: String = "پروژه سازمانی"
    ) {
        val detailIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_DESTINATION, DEST_ISSUE_DETAIL)
            putExtra(EXTRA_ISSUE_KEY, issueKey)
        }
        val detailPendingIntent = PendingIntent.getActivity(
            context,
            201,
            detailIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_TASKS_MENTIONS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("منشن جدید در $issueKey: $issueTitle")
            .setContentText("$mentionUser: «$commentSnippet»")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle("منشن در تسک $issueKey")
                    .setSummaryText(projectName)
                    .bigText("$mentionUser شما را منشن کرد:\n$commentSnippet")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setContentIntent(detailPendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "مشاهده تسک", detailPendingIntent)
            .setColor(Color.parseColor("#7C3AED"))
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_TASK, notification)
        } catch (_: SecurityException) {
            // Permission not yet granted
        }
    }

    /**
     * Show Leave Approval notification.
     */
    fun showLeaveApprovedNotification(
        context: Context,
        title: String = "درخواست مرخصی تایید شد",
        message: String = "درخواست مرخصی ثبت شده شما در سامانه اداری تایید گردید."
    ) {
        val hrIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_DESTINATION, DEST_HR_GROWTH)
        }
        val hrPendingIntent = PendingIntent.getActivity(
            context,
            301,
            hrIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_HR_APPROVALS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(hrPendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "مشاهده سوابق مرخصی", hrPendingIntent)
            .setColor(Color.parseColor("#10B981"))
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_HR, notification)
        } catch (_: SecurityException) {
            // Permission not yet granted
        }
    }

    /**
     * Notification shown when focus timer completes.
     */
    fun showFocusCompletedNotification(
        context: Context,
        taskTitle: String = "تمرکز آزاد",
        minutesLogged: Int = 25
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            401,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_TASKS_MENTIONS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("جلسه تمرکز با موفقیت به پایان رسید 🎉")
            .setContentText("$minutesLogged دقیقه کارکرد برای تسک $taskTitle ثبت و همگام‌سازی شد.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setColor(Color.parseColor("#10B981"))
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_FOCUS_DONE, notification)
        } catch (_: SecurityException) {
            // Permission not yet granted
        }
    }

    fun cancelNotification(context: Context, id: Int) {
        NotificationManagerCompat.from(context).cancel(id)
    }
}

