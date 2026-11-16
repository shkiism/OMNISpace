package amirparsa.shk.omnispace.feature.notification.presentation.shade

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import amirparsa.shk.omnispace.core.designsystem.DarkBorderSubtle
import amirparsa.shk.omnispace.core.designsystem.DarkCard
import amirparsa.shk.omnispace.core.designsystem.DarkElevated
import amirparsa.shk.omnispace.core.designsystem.DarkPrimaryViolet
import amirparsa.shk.omnispace.core.designsystem.DarkStatusDanger
import amirparsa.shk.omnispace.core.designsystem.DarkStatusInfo
import amirparsa.shk.omnispace.core.designsystem.DarkStatusSuccess
import amirparsa.shk.omnispace.core.designsystem.DarkStatusWarning
import amirparsa.shk.omnispace.core.designsystem.DarkTextMuted
import amirparsa.shk.omnispace.core.designsystem.DarkTextSecondary
import amirparsa.shk.omnispace.core.notification.OmniFocusService
import amirparsa.shk.omnispace.core.notification.OmniNotificationManager

/**
 * Stateful Composable for Android System Notification Shade simulator following MVI architecture.
 */
@Composable
fun AndroidSystemNotificationShadeScreen(
    viewModel: AndroidSystemNotificationShadeViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onJoinVideoCall: () -> Unit = {},
    onOpenIssue: (String) -> Unit = {},
    onOpenHr: () -> Unit = {}
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                AndroidSystemNotificationShadeEffect.NavigateBack -> onBackClick()
                AndroidSystemNotificationShadeEffect.JoinVideoCall -> onJoinVideoCall()
                is AndroidSystemNotificationShadeEffect.OpenIssue -> onOpenIssue(effect.key)
                AndroidSystemNotificationShadeEffect.OpenHr -> onOpenHr()
                is AndroidSystemNotificationShadeEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                AndroidSystemNotificationShadeEffect.FireNativeCallNotification -> {
                    OmniNotificationManager.showUrgentCallNotification(
                        context = context,
                        callerName = state.callerName,
                        meetingTitle = state.callMeetingTitle
                    )
                }
                AndroidSystemNotificationShadeEffect.FireNativeFocusNotification -> {
                    OmniFocusService.start(
                        context = context,
                        durationMinutes = 25,
                        taskKey = state.activeTaskTitle
                    )
                }
                AndroidSystemNotificationShadeEffect.FireNativeTaskNotification -> {
                    OmniNotificationManager.showTaskMentionNotification(
                        context = context,
                        issueKey = state.mentionTaskKey,
                        issueTitle = state.mentionTaskTitle,
                        mentionUser = state.mentionUser,
                        commentSnippet = state.mentionCommentSnippet
                    )
                }
                AndroidSystemNotificationShadeEffect.FireNativeHrNotification -> {
                    OmniNotificationManager.showLeaveApprovedNotification(
                        context = context,
                        title = state.hrNotificationTitle,
                        message = state.hrNotificationMessage
                    )
                }
                AndroidSystemNotificationShadeEffect.ClearAllNativeNotifications -> {
                    OmniFocusService.stop(context)
                }
            }
        }
    }

    AndroidSystemNotificationShadeContent(
        state = state,
        onIntent = viewModel::onIntent
    )
}

/**
 * Pure Stateless Composable rendering Android System Notification Shade.
 */
@Composable
fun AndroidSystemNotificationShadeContent(
    state: AndroidSystemNotificationShadeUiState,
    onIntent: (AndroidSystemNotificationShadeIntent) -> Unit
) {
    val scrollState = rememberScrollState()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF07080A),
                            Color(0xFF0F1218),
                            Color(0xFF0A0C10)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Android Native Status Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 12.dp, start = 4.dp, end = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.currentTimeString,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SignalCellularAlt,
                            contentDescription = "Signal",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(15.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = "WiFi",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(15.dp)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${state.batteryPercentage}٪",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(end = 2.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.BatteryFull,
                                contentDescription = "Battery",
                                tint = DarkStatusSuccess,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Top Toolbar to return to app
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onIntent(AndroidSystemNotificationShadeIntent.BackClicked) },
                        modifier = Modifier
                            .size(36.dp)
                            .background(DarkElevated, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت به اپلیکیشن",
                            tint = DarkPrimaryViolet,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Surface(
                        color = DarkPrimaryViolet.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkPrimaryViolet.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(DarkStatusSuccess, CircleShape)
                            )
                            Text(
                                text = "شبیه‌ساز اعلانات سیستم‌عامل اندروید",
                                color = DarkPrimaryViolet,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(36.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Lockscreen Persian Digital Clock & Date
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    Text(
                        text = state.currentTimeString,
                        fontSize = 58.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = (-1).sp,
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            textDirection = TextDirection.Ltr
                        )
                    )
                    Text(
                        text = state.currentDateString,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = DarkElevated.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = state.weatherMeetingSummary,
                            color = DarkTextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Notification Shade Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = DarkPrimaryViolet,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "اعلانات فعال OmniSpace",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "خارج از برنامه (Lockscreen)",
                        color = DarkTextMuted,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 3. Card 1: Heads-up Incoming Video Call (LiveKit CallStyle)
                AnimatedVisibility(visible = !state.isCallDismissed) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .border(1.dp, DarkPrimaryViolet.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCard.copy(alpha = 0.95f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .background(DarkPrimaryViolet, RoundedCornerShape(6.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Videocam,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                    Text(
                                        text = "OmniSpace • تماس تصویری ورودی",
                                        color = DarkPrimaryViolet,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "هم‌اکنون",
                                    color = DarkTextMuted,
                                    fontSize = 11.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val callerInitials = state.callerName.trim().split(" ")
                                    .filter { it.isNotBlank() }
                                    .mapNotNull { it.firstOrNull()?.toString() }
                                    .take(2)
                                    .joinToString("")
                                    .ifBlank { "هم" }

                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .border(2.dp, DarkStatusSuccess.copy(alpha = pulseAlpha), CircleShape)
                                        .background(DarkElevated, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = callerInitials,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = state.callMeetingTitle,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${state.callerName} در حال برقراری تماس ویدیویی LiveKit...",
                                        color = DarkTextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { onIntent(AndroidSystemNotificationShadeIntent.JoinCallClicked) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkStatusSuccess),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Videocam,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "پیوستن به جلسه",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                OutlinedButton(
                                    onClick = { onIntent(AndroidSystemNotificationShadeIntent.DismissCallClicked) },
                                    modifier = Modifier.weight(0.8f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkStatusDanger),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkStatusDanger)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CallEnd,
                                        contentDescription = null,
                                        tint = DarkStatusDanger,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "رد تماس",
                                        color = DarkStatusDanger,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Card 2: Ongoing Focus Timer (Foreground Service Notification)
                AnimatedVisibility(visible = state.isFocusRunning) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .border(1.dp, DarkBorderSubtle, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCard)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                if (state.isFocusPaused) DarkStatusWarning else DarkPrimaryViolet,
                                                CircleShape
                                            )
                                    )
                                    Text(
                                        text = "OmniSpace • سرویس تمرکز و ثبت کارکرد (Ongoing)",
                                        color = DarkTextMuted,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Surface(
                                    color = DarkElevated,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (state.isFocusPaused) "متوقف شده" else "در حال اجرا",
                                        color = if (state.isFocusPaused) DarkStatusWarning else DarkPrimaryViolet,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "حالت تمرکز عمیق روی تسک ${state.activeTaskTitle}",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            val progressPercent = (state.focusProgress * 100).toInt()
                            val focusStatusText = if (state.isFocusPaused) "تایمر متوقف شده است" else "در حال اجرا • $progressPercent٪ سپری شده"
                            Text(
                                text = focusStatusText,
                                color = DarkTextSecondary,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            LinearProgressIndicator(
                                progress = { state.focusProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = DarkPrimaryViolet,
                                trackColor = DarkElevated
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { onIntent(AndroidSystemNotificationShadeIntent.StopFocusClicked) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkElevated),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Stop,
                                        contentDescription = null,
                                        tint = DarkStatusDanger,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "پایان و ثبت کارکرد",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Button(
                                    onClick = {
                                        if (state.isFocusPaused) {
                                            onIntent(AndroidSystemNotificationShadeIntent.ResumeFocusClicked)
                                        } else {
                                            onIntent(AndroidSystemNotificationShadeIntent.PauseFocusClicked)
                                        }
                                    },
                                    modifier = Modifier.weight(0.7f),
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkElevated),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        imageVector = if (state.isFocusPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                        contentDescription = null,
                                        tint = DarkStatusWarning,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (state.isFocusPaused) "ادامه" else "مکث",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                // 5. Card 3: Task Mention Notification (BigTextStyle)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .border(1.dp, DarkBorderSubtle, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(DarkStatusInfo, CircleShape)
                                )
                                Text(
                                    text = "OmniSpace • کارها و پروژه‌ها",
                                    color = DarkTextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "۱۲ دقیقه پیش",
                                color = DarkTextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "${state.mentionUser} شما را در تسک ${state.mentionTaskKey} منشن کرد:",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = DarkElevated.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle)
                        ) {
                            Text(
                                text = "«${state.mentionCommentSnippet}»",
                                color = DarkTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 19.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { onIntent(AndroidSystemNotificationShadeIntent.OpenIssueClicked(state.mentionTaskKey)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkElevated),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "مشاهده تسک و ثبت نظر ↗",
                                color = DarkPrimaryViolet,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // 6. Card 4: HR & Payroll Notification
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .border(1.dp, DarkBorderSubtle, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(DarkStatusSuccess, CircleShape)
                                )
                                Text(
                                    text = "OmniSpace • منابع انسانی و رشد",
                                    color = DarkTextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "دیروز",
                                color = DarkTextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = state.hrNotificationTitle,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = state.hrNotificationMessage,
                            color = DarkTextSecondary,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = { onIntent(AndroidSystemNotificationShadeIntent.OpenHrClicked) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle)
                        ) {
                            Text(
                                text = "مشاهده جزئیات در تب سازمان و من",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 7. Interactive Bottom Card: Dispatch Real Native Android Notifications
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .border(1.dp, DarkPrimaryViolet.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = DarkElevated
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                tint = DarkPrimaryViolet,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "تست اعلانات واقعی روی سیستم‌عامل اندروید",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "با زدن دکمه زیر، اعلانات فوق مستقیماً در نوار نوتیفیکیشن واقعی گوشی شما (Outside App) شلیک می‌شوند.",
                            color = DarkTextSecondary,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                onIntent(AndroidSystemNotificationShadeIntent.TriggerNativeCallNotification)
                                onIntent(AndroidSystemNotificationShadeIntent.TriggerNativeTaskNotification)
                                onIntent(AndroidSystemNotificationShadeIntent.TriggerNativeHrNotification)
                                onIntent(AndroidSystemNotificationShadeIntent.TriggerNativeFocusNotification)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkPrimaryViolet),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ارسال همه اعلانات به نوار اعلان گوشی",
                                color = Color.Black,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

