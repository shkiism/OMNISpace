package amirparsa.shk.omnispace.core.notification

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import amirparsa.shk.omnispace.MainActivity
import amirparsa.shk.omnispace.R

class OmniFocusService : Service() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private var totalSeconds = 25 * 60
    private var remainingSeconds = 25 * 60
    private var isPaused = false
    private var taskKey = ""

    private var tickerJob: Job? = null

    companion object {
        const val NOTIFICATION_ID = 1001

        const val ACTION_START = "amirparsa.shk.omnispace.action.START_FOCUS"
        const val ACTION_PAUSE = "amirparsa.shk.omnispace.action.PAUSE_FOCUS"
        const val ACTION_RESUME = "amirparsa.shk.omnispace.action.RESUME_FOCUS"
        const val ACTION_STOP = "amirparsa.shk.omnispace.action.STOP_FOCUS"

        const val EXTRA_DURATION_MINUTES = "extra_duration_minutes"
        const val EXTRA_TASK_KEY = "extra_task_key"

        fun start(context: Context, durationMinutes: Int = 25, taskKey: String = "") {
            val intent = Intent(context, OmniFocusService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_DURATION_MINUTES, durationMinutes)
                putExtra(EXTRA_TASK_KEY, taskKey)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, OmniFocusService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        OmniNotificationManager.initChannels(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val duration = intent.getIntExtra(EXTRA_DURATION_MINUTES, 25)
                taskKey = intent.getStringExtra(EXTRA_TASK_KEY).orEmpty()
                totalSeconds = duration * 60
                remainingSeconds = totalSeconds
                isPaused = false

                startForegroundServiceNotification()
                startTicker()
            }

            ACTION_PAUSE -> {
                isPaused = true
                updateNotification()
            }

            ACTION_RESUME -> {
                isPaused = false
                updateNotification()
            }

            ACTION_STOP -> {
                stopTicker()
                val minutesSpent = ((totalSeconds - remainingSeconds) / 60).coerceAtLeast(1)
                OmniNotificationManager.showFocusCompletedNotification(
                    this,
                    taskTitle = taskKey.ifBlank { "تمرکز آزاد" },
                    minutesLogged = minutesSpent
                )
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun startForegroundServiceNotification() {
        val notification = buildOngoingNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = serviceScope.launch {
            while (isActive && remainingSeconds > 0) {
                delay(1000)
                if (!isPaused) {
                    remainingSeconds--
                    updateNotification()
                }
            }

            if (remainingSeconds <= 0) {
                OmniNotificationManager.showFocusCompletedNotification(
                    this@OmniFocusService,
                    taskTitle = taskKey.ifBlank { "تمرکز آزاد" },
                    minutesLogged = totalSeconds / 60
                )
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
    }

    private fun stopTicker() {
        tickerJob?.cancel()
    }

    private fun updateNotification() {
        val notification = buildOngoingNotification()
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun buildOngoingNotification(): Notification {
        val minutes = remainingSeconds / 60
        val seconds = remainingSeconds % 60
        val formattedTime = String.format("%02d:%02d", minutes, seconds)
        val elapsed = totalSeconds - remainingSeconds
        val percent = if (totalSeconds > 0) (elapsed * 100 / totalSeconds) else 0

        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openPendingIntent = PendingIntent.getActivity(
            this,
            501,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, OmniFocusService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            502,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pauseResumeIntent = Intent(this, OmniFocusService::class.java).apply {
            action = if (isPaused) ACTION_RESUME else ACTION_PAUSE
        }
        val pauseResumePendingIntent = PendingIntent.getService(
            this,
            503,
            pauseResumeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val statusText = if (isPaused) "متوقف شده (Pause)" else "زمان باقی‌مانده: $formattedTime"

        val title = if (taskKey.isNotBlank()) "حالت تمرکز سازمانی: تسک $taskKey" else "حالت تمرکز سازمانی"

        return NotificationCompat.Builder(this, OmniNotificationManager.CHANNEL_FOCUS_TIMER)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText("$statusText • $percent٪ سپری شد")
            .setSubText("سرویس فوکوس پیش‌زمینه")
            .setProgress(totalSeconds, elapsed, false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setContentIntent(openPendingIntent)
            .addAction(
                R.drawable.ic_launcher_foreground,
                if (isPaused) "ادامه تمرکز" else "توقف موقت",
                pauseResumePendingIntent
            )
            .addAction(
                R.drawable.ic_launcher_foreground,
                "پایان و ثبت کارکرد",
                stopPendingIntent
            )
            .setColor(Color.parseColor("#9D7BF6"))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
