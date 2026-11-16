package amirparsa.shk.omnispace.feature.today.presentation

import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import amirparsa.shk.omnispace.core.designsystem.OmniSpaceTheme
import amirparsa.shk.omnispace.core.designsystem.OmniTheme
import amirparsa.shk.omnispace.core.designsystem.OmniThemeMode
import amirparsa.shk.omnispace.core.designsystem.component.OmniBadgeType
import amirparsa.shk.omnispace.core.designsystem.component.OmniFocusCard
import amirparsa.shk.omnispace.core.designsystem.component.OmniHeroCard
import amirparsa.shk.omnispace.core.designsystem.component.OmniShimmerBox
import amirparsa.shk.omnispace.core.designsystem.component.OmniShimmerCard
import amirparsa.shk.omnispace.core.designsystem.component.OmniShimmerList
import amirparsa.shk.omnispace.core.designsystem.component.omniShimmer
import amirparsa.shk.omnispace.core.designsystem.component.OmniStatusBadge
import amirparsa.shk.omnispace.core.designsystem.component.WorkType
import amirparsa.shk.omnispace.feature.today.domain.model.UpcomingMeetingModel
import amirparsa.shk.omnispace.feature.today.presentation.components.HabitCreationBottomSheet
import amirparsa.shk.omnispace.feature.today.presentation.components.MeetingAgendaBottomSheet
import amirparsa.shk.omnispace.feature.today.presentation.components.NotesArchiveBottomSheet

/**
 * Stateful entry composable for Today screen following MVI architecture.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    viewModel: TodayViewModel = viewModel(),
    onNavigateToWorkspace: () -> Unit = {},
    onTaskClick: (String) -> Unit = {},
    onJoinMeetingClick: () -> Unit = {},
    onOpenSystemNotificationShade: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                TodayEffect.NavigateToWorkspace -> onNavigateToWorkspace()
                is TodayEffect.OpenTask -> onTaskClick(effect.issueKey)
                TodayEffect.JoinMeeting -> onJoinMeetingClick()
                is TodayEffect.LaunchSystemCalendar -> {
                    val intent = Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.Events.TITLE, effect.meeting.title)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, effect.meeting.startTimeMillis)
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, effect.meeting.endTimeMillis)
                        .putExtra(CalendarContract.Events.DESCRIPTION, effect.meeting.description)
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, effect.meeting.location)
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "برنامه تقویم روی دستگاه یافت نشد", Toast.LENGTH_SHORT).show()
                    }
                }
                TodayEffect.OpenSystemNotificationShade -> onOpenSystemNotificationShade()
                is TodayEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    TodayContent(
        state = state,
        onIntent = viewModel::onIntent,
        onOpenSystemNotificationShade = onOpenSystemNotificationShade,
        modifier = modifier
    )
}

/**
 * Pure Stateless Composable rendering Today screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayContent(
    state: TodayUiState,
    onIntent: (TodayIntent) -> Unit,
    onOpenSystemNotificationShade: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val habitSheetState = rememberModalBottomSheetState()
    val notesSheetState = rememberModalBottomSheetState()
    val agendaSheetState = rememberModalBottomSheetState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OmniTheme.colors.canvas)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // SECTION 0: OFFLINE SYNC STATUS (پروتکل آفلاین و همگام‌سازی منطقه شخصی)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(OmniTheme.colors.card.copy(alpha = 0.6f))
                .border(1.dp, OmniTheme.colors.borderSubtle, RoundedCornerShape(12.dp))
                .clickable { onIntent(TodayIntent.RefreshToday) }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val iconRotation = if (state.isSyncing) {
                    val infiniteTransition = rememberInfiniteTransition(label = "syncRotation")
                    val angle by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "syncAngle"
                    )
                    angle
                } else 0f

                Icon(
                    imageVector = if (state.isSyncing) Icons.Default.Sync else Icons.Default.CloudDone,
                    contentDescription = "وضعیت همگام‌سازی",
                    tint = if (state.isSyncing) OmniTheme.colors.primaryLight else OmniTheme.colors.statusSuccess,
                    modifier = Modifier
                        .size(16.dp)
                        .graphicsLayer { rotationZ = iconRotation }
                )

                Text(
                    text = "منطقه شخصی • ${state.syncStatusText}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = OmniTheme.colors.textPrimary
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = state.lastSyncTime,
                    fontSize = 11.sp,
                    color = OmniTheme.colors.textMuted
                )
                OmniStatusBadge(
                    text = if (state.isSyncing) "در حال پردازش" else "بروز",
                    type = if (state.isSyncing) OmniBadgeType.PRIMARY else OmniBadgeType.SUCCESS
                )
            }
        }

        // SECTION 1: HERO ATTENDANCE CARD (Centered Large Clock)
        OmniHeroCard(
            isCheckedIn = state.isCheckedIn,
            checkInTimeStr = state.checkInTime,
            elapsedTimeStr = state.elapsedTime,
            locationName = if (state.selectedWorkType == WorkType.ON_SITE) "شعبه مرکزی" else "دورکاری",
            completionPercentage = state.completionPercentage,
            selectedWorkType = state.selectedWorkType,
            onWorkTypeChange = { onIntent(TodayIntent.ChangeWorkType(it)) },
            onCheckInClick = { onIntent(TodayIntent.CheckIn) },
            onCheckOutClick = { onIntent(TodayIntent.CheckOut) },
            onBreakClick = { onIntent(TodayIntent.TakeBreak) },
            isLoading = state.isLoading
        )

        // SECTION 1.5: FOCUS TIMER & POMODORO (سرویس تمرکز و پومودورو)
        OmniFocusCard(
            activeTaskTitle = state.tasks.firstOrNull()?.let { "${it.projectKey}: ${it.title}" },
            onStartFocus = { minutes ->
                onIntent(TodayIntent.StartFocus(minutes))
            },
            onStopAndLogWork = { loggedMinutes ->
                onIntent(TodayIntent.StopFocus(loggedMinutes))
            }
        )

        // SECTION 2: HABITS WIDGET («عادت‌های سازمانی امروز»)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        tint = Color(0xFFA78BFA),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "عادت‌های سازمانی امروز",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE2E8F0)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Completion Pill Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF2E1E5B))
                            .border(1.dp, Color(0xFF7C3AED).copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${state.completedHabitsCount} از ${state.habits.size} انجام شده",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFC4B5FD)
                        )
                    }

                    // Add Habit Button
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF7C3AED).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFF7C3AED).copy(alpha = 0.5f), CircleShape)
                            .clickable { onIntent(TodayIntent.OpenHabitCreation) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "تعریف عادت جدید",
                            tint = Color(0xFFC4B5FD),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Habits list or Shimmer or Empty state
            if (state.isLoading && state.habits.isEmpty()) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(36.dp)
                                .omniShimmer(shape = RoundedCornerShape(20.dp))
                        )
                    }
                }
            } else if (state.habits.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(OmniTheme.colors.card)
                        .border(1.dp, OmniTheme.colors.borderSubtle, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "عادتی برای امروز تعریف نشده است",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            } else {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.habits.forEach { habit ->
                        val isDone = habit.isDone
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isDone) Color(0xFF063726) else Color(0xFF161A24))
                                .border(
                                    1.dp,
                                    if (isDone) Color(0xFF10B981).copy(alpha = 0.6f) else Color(0xFF262C3C),
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    onIntent(TodayIntent.ToggleHabit(habit.id))
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (isDone) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF34D399),
                                    modifier = Modifier.size(15.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.CheckBoxOutlineBlank,
                                    contentDescription = null,
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Text(
                                text = habit.title,
                                fontSize = 12.sp,
                                fontWeight = if (isDone) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isDone) Color(0xFF34D399) else Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }

        // SECTION 3: FOCUS TASKS («کارهای سررسید امروز»)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(OmniTheme.colors.card)
                .border(1.dp, OmniTheme.colors.borderSubtle, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF2E1E5B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFFA78BFA),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "کارهای سررسید امروز",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE2E8F0)
                        )
                    }

                    // Count Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF221C44))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${state.completedTasksCount} / ${state.tasks.size}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA78BFA)
                        )
                    }
                }

                // Task Items or Shimmer or Empty state
                if (state.isLoading && state.tasks.isEmpty()) {
                    OmniShimmerList(count = 3)
                } else if (state.tasks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF141721))
                            .border(1.dp, Color(0xFF242938), RoundedCornerShape(12.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "تسک فعالی برای امروز وجود ندارد",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                } else {
                    state.tasks.forEach { task ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF141721))
                            .border(1.dp, Color(0xFF242938), RoundedCornerShape(12.dp))
                            .clickable { onIntent(TodayIntent.TaskClicked(task.projectKey)) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            // Avatar or Leading Icon
                            if (task.isDone) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF063726)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else if (task.avatarInitials != null) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF262D3E)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = task.avatarInitials,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFCBD5E1)
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF262D3E)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Group,
                                        contentDescription = null,
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            // Title & Project Subtitle
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = task.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (task.isDone) Color(0xFF64748B) else Color(0xFFE2E8F0),
                                        textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None
                                    )
                                    // Priority Indicator Dot
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Color(task.priorityColor))
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // Project Tag Chip
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF202638))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = task.projectKey,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF94A3B8)
                                        )
                                    }

                                    Text(
                                        text = task.subtitle,
                                        fontSize = 11.sp,
                                        color = if (task.isDone) Color(0xFF10B981) else Color(0xFF94A3B8)
                                    )

                                    if (task.dueTime != null) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFF352B18))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = task.dueTime,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFBBF24)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Checkbox
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (task.isDone) Color(0xFF063726) else Color(0xFF1C2130))
                                .border(
                                    1.dp,
                                    if (task.isDone) Color(0xFF10B981) else Color(0xFF384258),
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable {
                                    onIntent(TodayIntent.ToggleTask(task.id))
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (task.isDone) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF34D399),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
                }

                // Link to full tasks list
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onIntent(TodayIntent.NavigateToWorkspaceClicked) }
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = Color(0xFFA78BFA),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "مشاهده همه کارهای من",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFA78BFA)
                    )
                }
            }
        }

        // SECTION 4: UPCOMING MEETING CARD («جلسه بعدی»)
        if (state.upcomingMeeting != null) {
            val meeting = state.upcomingMeeting
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(OmniTheme.colors.card)
                    .border(1.dp, OmniTheme.colors.borderSubtle, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Header Row
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
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFA78BFA))
                            )
                            Text(
                                text = "جلسه بعدی",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFA78BFA)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF10B981).copy(alpha = 0.15f))
                                    .clickable { onIntent(TodayIntent.AddMeetingToCalendarClicked(meeting)) }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = "Add to Calendar",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "ثبت تقویم",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981)
                                    )
                                }
                            }

                            if (meeting.timeFormatted.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF222838))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = meeting.timeFormatted,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFCBD5E1)
                                    )
                                }
                            }
                        }
                    }

                    // Title & Subtitle
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = meeting.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (meeting.subtitle.isNotBlank()) {
                            Text(
                                text = meeting.subtitle,
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    // Meeting Meta: Duration + Platform
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (meeting.durationText.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = meeting.durationText,
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            Text(text = "•", color = Color(0xFF475569))
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideoCall,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = meeting.platformName,
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    // Bottom Row: Action Buttons + Overlapping Avatars
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF7C3AED))
                                    .clickable { onIntent(TodayIntent.JoinMeetingClicked) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VideoCall,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "پیوستن",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF1E2432))
                                    .border(1.dp, Color(0xFF323B4E), RoundedCornerShape(10.dp))
                                    .clickable { onIntent(TodayIntent.OpenMeetingAgendaClicked) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = Color(0xFFCBD5E1),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "آجندا",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFCBD5E1)
                                )
                            }
                        }

                        // Overlapping Avatar Stack
                        if (meeting.attendeeInitials.isNotEmpty()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy((-6).dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                meeting.attendeeInitials.forEach { tag ->
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF2B2050))
                                            .border(1.5.dp, Color(0xFF141721), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = tag,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFC4B5FD)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (state.isLoading) {
            OmniShimmerCard(height = 140.dp)
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(OmniTheme.colors.card)
                    .border(1.dp, OmniTheme.colors.borderSubtle, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VideoCall,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "جلسه‌ای برای امروز برنامه‌ریزی نشده است",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        // SECTION 5: QUICK NOTE CARD («یادداشت سریع امروز»)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(OmniTheme.colors.card)
                .border(1.dp, OmniTheme.colors.borderSubtle, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = null,
                            tint = Color(0xFFA78BFA),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "یادداشت سریع امروز",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE2E8F0)
                        )
                    }

                    // Cloud Auto-save badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "ذخیره خودکار ابری",
                            fontSize = 10.sp,
                            color = Color(0xFF34D399)
                        )
                    }
                }

                // Inset Text Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F121A))
                        .border(1.dp, Color(0xFF222838), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        BasicTextField(
                            value = state.quickNoteText,
                            onValueChange = { onIntent(TodayIntent.UpdateQuickNote(it)) },
                            textStyle = TextStyle(
                                fontSize = 13.sp,
                                color = Color(0xFFE2E8F0),
                                fontWeight = FontWeight.Normal
                            ),
                            cursorBrush = SolidColor(Color(0xFFA78BFA)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            decorationBox = { innerTextField ->
                                if (state.quickNoteText.isEmpty()) {
                                    Text(
                                        text = "یادداشتی برای امروز بنویسید...",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                                innerTextField()
                            }
                        )

                        // Bottom Action Icons inside Note
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left: Save Button
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1E2432))
                                    .border(1.dp, Color(0xFF323B4E), RoundedCornerShape(8.dp))
                                    .clickable { onIntent(TodayIntent.SaveQuickNote) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = null,
                                    tint = Color(0xFFCBD5E1),
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "ثبت",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFCBD5E1)
                                )
                            }

                            // Right: Mic & Attachment Icons
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Voice Note",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable { /* Voice Memo */ }
                                )
                                Icon(
                                    imageVector = Icons.Default.AttachFile,
                                    contentDescription = "Attachment",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable { /* Attach */ }
                                )
                            }
                        }
                    }
                }

                // Link to Full Notes Archive
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onIntent(TodayIntent.OpenNotesArchive) }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "آرشیو و مدیریت همه یادداشت‌های خصوصی ↗",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA78BFA)
                    )
                }
            }
        }

        // Bottom clearance for Floating Action Button
        Spacer(modifier = Modifier.height(72.dp))
    }

    // Modal Bottom Sheets for Today Extensions
    if (state.isHabitCreationOpen) {
        HabitCreationBottomSheet(
            onDismissRequest = { onIntent(TodayIntent.CloseHabitCreation) },
            sheetState = habitSheetState,
            onHabitCreated = { title, _, _ ->
                onIntent(TodayIntent.AddHabit(title))
            }
        )
    }

    if (state.isNotesArchiveOpen) {
        NotesArchiveBottomSheet(
            onDismissRequest = { onIntent(TodayIntent.CloseNotesArchive) },
            sheetState = notesSheetState,
            notes = state.notes,
            onSaveNote = { id, title, content ->
                onIntent(TodayIntent.SaveNote(id, title, content))
            },
            onTogglePin = { id ->
                onIntent(TodayIntent.TogglePinNote(id))
            },
            onDeleteNote = { id ->
                onIntent(TodayIntent.DeleteNote(id))
            }
        )
    }

    if (state.isMeetingAgendaOpen) {
        MeetingAgendaBottomSheet(
            meeting = state.upcomingMeeting ?: UpcomingMeetingModel(),
            onDismissRequest = { onIntent(TodayIntent.CloseMeetingAgenda) },
            sheetState = agendaSheetState,
            onJoinMeeting = { onIntent(TodayIntent.JoinMeetingClicked) },
            onAddToCalendar = { meeting ->
                onIntent(TodayIntent.AddMeetingToCalendarClicked(meeting))
            }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0C0E)
@Composable
private fun TodayScreenPreview() {
    OmniSpaceTheme(themeMode = OmniThemeMode.DARK) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            TodayContent(
                state = TodayUiState(),
                onIntent = {}
            )
        }
    }
}

