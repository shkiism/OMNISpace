package amirparsa.shk.omnispace.feature.issue.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import amirparsa.shk.omnispace.core.designsystem.component.OmniShimmerCard
import amirparsa.shk.omnispace.core.designsystem.component.OmniShimmerList
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import amirparsa.shk.omnispace.core.designsystem.OmniSpaceTheme
import amirparsa.shk.omnispace.core.designsystem.OmniTheme
import amirparsa.shk.omnispace.core.designsystem.OmniThemeMode
import amirparsa.shk.omnispace.feature.issue.domain.model.IssueStatusType
import amirparsa.shk.omnispace.feature.work.data.remote.dto.IssueTransitionDto

/**
 * Stateful entry composable for Issue Detail following MVI architecture.
 */
@Composable
fun IssueDetailScreen(
    issueKey: String = "",
    viewModel: IssueDetailViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onLogWorkClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(issueKey) {
        viewModel.onIntent(IssueDetailIntent.LoadIssue(issueKey))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                IssueDetailEffect.NavigateBack -> onBackClick()
                is IssueDetailEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    IssueDetailContent(
        state = state.copy(issueKey = issueKey),
        onIntent = viewModel::onIntent,
        onLogWorkClick = {
            onLogWorkClick()
            viewModel.onIntent(IssueDetailIntent.OpenLogWorkDialog)
        },
        modifier = modifier
    )
}

/**
 * Pure Stateless Composable rendering Issue Detail screen.
 */
@Composable
fun IssueDetailContent(
    state: IssueDetailUiState,
    onIntent: (IssueDetailIntent) -> Unit,
    onLogWorkClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var showStatusMenu by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OmniTheme.colors.canvas)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 80.dp)
        ) {
            // 1. TOP APP BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF191E2A))
                            .border(1.dp, Color(0xFF2A3246), CircleShape)
                            .clickable { onIntent(IssueDetailIntent.BackClicked) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Key Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF222838))
                            .border(1.dp, Color(0xFF333B4F), RoundedCornerShape(8.dp))
                            .clickable { onIntent(IssueDetailIntent.CopyKeyClicked) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = state.issueKey,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFCBD5E1)
                        )
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Key",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                // Status Dropdown & Options
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(state.currentStatus.color.copy(alpha = 0.15f))
                                .border(1.dp, state.currentStatus.color.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .clickable { showStatusMenu = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(state.currentStatus.color)
                            )
                            Text(
                                text = state.currentStatus.labelFa,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = state.currentStatus.color
                            )
                            if (state.isTransitioning) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    strokeWidth = 1.5.dp,
                                    color = state.currentStatus.color
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "انتخاب وضعیت",
                                    tint = state.currentStatus.color,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showStatusMenu,
                            onDismissRequest = { showStatusMenu = false },
                            modifier = Modifier.background(Color(0xFF191E2A))
                        ) {
                            if (state.availableTransitions.isNotEmpty()) {
                                state.availableTransitions.forEach { transition ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = transition.displayName,
                                                fontSize = 12.sp,
                                                color = Color.White
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.SwapHoriz,
                                                contentDescription = null,
                                                tint = Color(0xFFA78BFA),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        },
                                        onClick = {
                                            showStatusMenu = false
                                            onIntent(
                                                IssueDetailIntent.ExecuteTransition(
                                                    transition.id,
                                                    transition.displayName
                                                )
                                            )
                                        }
                                    )
                                }
                            } else {
                                IssueStatusType.entries.filter { it != state.currentStatus }.forEach { status ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = status.labelFa,
                                                fontSize = 12.sp,
                                                color = Color.White
                                            )
                                        },
                                        leadingIcon = {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(status.color)
                                            )
                                        },
                                        onClick = {
                                            showStatusMenu = false
                                            onIntent(IssueDetailIntent.ChangeStatus(status))
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (state.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp),
                    color = Color(0xFF7C3AED),
                    trackColor = Color(0xFF1E2432)
                )
            }

            if (state.error != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF3B1E2B))
                        .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = state.error,
                        fontSize = 12.sp,
                        color = Color(0xFFFCA5A5)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 2. ISSUE TITLE
                Text(
                    text = state.title.ifBlank { if (state.isLoading) "در حال بارگذاری اطلاعات تسک..." else "تسک بدون عنوان" },
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 26.sp
                )

                // Description
                if (state.isLoading) {
                    OmniShimmerCard(height = 60.dp)
                } else if (state.description.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF131622))
                            .border(1.dp, Color(0xFF222838), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = state.description,
                            fontSize = 12.sp,
                            color = Color(0xFFCBD5E1),
                            lineHeight = 20.sp
                        )
                    }
                }

                // 3. METADATA MATRIX CARD
                if (state.isLoading) {
                    OmniShimmerCard(height = 100.dp)
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF131622))
                            .border(1.dp, Color(0xFF222838), RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Text(text = "مسئول (Assignee)", fontSize = 10.sp, color = Color(0xFF64748B))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF7C3AED)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = state.assigneeInitials, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                        Text(text = state.assigneeName, fontSize = 12.sp, color = Color(0xFFE2E8F0), fontWeight = FontWeight.Medium)
                                    }
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Text(text = "اولویت (Priority)", fontSize = 10.sp, color = Color(0xFF64748B))
                                    val isHigh = state.priorityName.contains("فوری") || state.priorityName.contains("بحرانی") || state.priorityName.contains("High") || state.priorityName.contains("Blocker")
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isHigh) Color(0xFF3B151C) else Color(0xFF1E2838))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(5.dp)
                                                .clip(CircleShape)
                                                .background(if (isHigh) Color(0xFFEF4444) else Color(0xFF38BDF8))
                                        )
                                        Text(
                                            text = state.priorityName,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isHigh) Color(0xFFFDA4AF) else Color(0xFF7DD3FC)
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = Color(0xFF1D2230), thickness = 1.dp)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(text = "اسپرینت (Sprint)", fontSize = 10.sp, color = Color(0xFF64748B))
                                    Text(text = state.sprintName, fontSize = 11.sp, color = Color(0xFFCBD5E1), fontWeight = FontWeight.Medium)
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(text = "امتیاز تخمین (Story Points)", fontSize = 10.sp, color = Color(0xFF64748B))
                                    Text(text = state.estimateStoryPoints, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA78BFA))
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(text = "سررسید (Due)", fontSize = 10.sp, color = Color(0xFF64748B))
                                    Text(text = state.dueDateText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24))
                                }
                            }
                        }
                    }
                }

                // 4. WORK LOGGING & TIME TRACKING CARD
                val loggedHoursFormatted = String.format(java.util.Locale.US, "%.1f", state.timeLoggedHours).removeSuffix(".0")
                val estHoursFormatted = String.format(java.util.Locale.US, "%.1f", state.totalEstimatedHours).removeSuffix(".0")
                val progressFraction = if (state.totalEstimatedHours > 0) {
                    (state.timeLoggedHours / state.totalEstimatedHours).toFloat().coerceIn(0f, 1f)
                } else 0f
                val progressPercent = (progressFraction * 100).toInt()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF131622))
                        .border(1.dp, Color(0xFF222838), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
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
                                    imageVector = Icons.Default.HourglassBottom,
                                    contentDescription = null,
                                    tint = Color(0xFFA78BFA),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "پیگیری زمان کارکرد (Time Tracking)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Text(
                                text = "$loggedHoursFormatted ساعت از $estHoursFormatted ساعت برآورد ($progressPercent٪)",
                                fontSize = 11.sp,
                                color = Color(0xFFA78BFA)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF222838))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progressFraction.coerceAtLeast(0.01f))
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0xFF7C3AED))
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E2432))
                                .border(1.dp, Color(0xFF333B4F), RoundedCornerShape(8.dp))
                                .clickable {
                                    onLogWorkClick()
                                    onIntent(IssueDetailIntent.OpenLogWorkDialog)
                                },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = Color(0xFFCBD5E1),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ثبت کارکرد دستی (Log Work)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }
                }

                // 5. ACCEPTANCE CRITERIA CHECKLIST
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF131622))
                        .border(1.dp, Color(0xFF222838), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "چک‌لیست و معیارهای پذیرش (Acceptance Criteria)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (state.checklistItems.isNotEmpty()) {
                                Text(
                                    text = "${state.completedChecklistCount}/${state.checklistItems.size}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFA78BFA)
                                )
                            }
                        }

                        if (state.isLoading) {
                            OmniShimmerCard(height = 24.dp)
                        } else if (state.checklistItems.isEmpty()) {
                            Text(
                                text = "معیار پذیرش یا آیتم چک‌لیستی برای این تسک ثبت نشده است.",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        } else {
                            state.checklistItems.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onIntent(IssueDetailIntent.ToggleChecklistItem(item.id))
                                        }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (item.isDone) Color(0xFF063726) else Color(0xFF1C2130))
                                            .border(
                                                1.dp,
                                                if (item.isDone) Color(0xFF10B981) else Color(0xFF384258),
                                                RoundedCornerShape(4.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (item.isDone) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color(0xFF34D399),
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = item.title,
                                        fontSize = 12.sp,
                                        color = if (item.isDone) Color(0xFF64748B) else Color(0xFFE2E8F0),
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // 6. ACTIVITY & COMMENTS TABS
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF11141E))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IssueActivityTab.entries.forEach { tab ->
                        val isSelected = tab == state.selectedTab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFF2E1E5B) else Color.Transparent)
                                .clickable { onIntent(IssueDetailIntent.SelectTab(tab)) },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val countText = if (tab == IssueActivityTab.COMMENTS) {
                                    "(${state.comments.size})"
                                } else if (tab.count != null) {
                                    "(${tab.count})"
                                } else null

                                Text(
                                    text = tab.labelFa,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color(0xFFC4B5FD) else Color(0xFF94A3B8)
                                )
                                if (countText != null) {
                                    Text(
                                        text = countText,
                                        fontSize = 10.sp,
                                        color = if (isSelected) Color(0xFFC4B5FD) else Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }
                }

                // Tab Content
                when (state.selectedTab) {
                    IssueActivityTab.COMMENTS -> {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (state.comments.isEmpty()) {
                                if (state.isLoading) {
                                    OmniShimmerList(count = 2)
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF141724))
                                            .border(1.dp, Color(0xFF222838), RoundedCornerShape(12.dp))
                                            .padding(24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "هنوز دیدگاهی برای این تسک ثبت نشده است.",
                                            fontSize = 12.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }
                            } else {
                                state.comments.forEach { comment ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF141724))
                                            .border(1.dp, Color(0xFF222838), RoundedCornerShape(12.dp))
                                            .padding(12.dp)
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
                                                            .size(26.dp)
                                                            .clip(CircleShape)
                                                            .background(Color(0xFF2B2050)),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(text = comment.authorInitials, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC4B5FD))
                                                    }
                                                    Text(text = comment.authorName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                }

                                                Text(text = comment.time, fontSize = 10.sp, color = Color(0xFF64748B))
                                            }

                                            Text(
                                                text = comment.text,
                                                fontSize = 12.sp,
                                                color = Color(0xFFCBD5E1),
                                                lineHeight = 18.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    IssueActivityTab.HISTORY -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF141724))
                                .border(1.dp, Color(0xFF222838), RoundedCornerShape(10.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "تاریخچه‌ای برای این تسک ثبت نشده است.",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    IssueActivityTab.ATTACHMENTS -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF141724))
                                .border(1.dp, Color(0xFF222838), RoundedCornerShape(10.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "فایلی برای این تسک پیوست نشده است.",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    IssueActivityTab.QA_TESTS -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF141724))
                                .border(1.dp, Color(0xFF222838), RoundedCornerShape(10.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "مورد آزمون یا نتیجه تست QA متصل یافت نشد.",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            }
        }

        // 7. STICKY BOTTOM COMMENT BAR
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0xFF0C0E14))
                .border(1.dp, Color(0xFF1E2432))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFF171B26))
                        .border(1.dp, Color(0xFF283042), RoundedCornerShape(22.dp))
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Comment,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (state.commentInputText.isEmpty()) {
                            Text(
                                text = "دیدگاهی بنویسید یا همکاران را منشن کنید...",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                        BasicTextField(
                            value = state.commentInputText,
                            onValueChange = { onIntent(IssueDetailIntent.UpdateCommentInput(it)) },
                            textStyle = TextStyle(
                                color = Color.White,
                                fontSize = 12.sp
                            ),
                            cursorBrush = SolidColor(Color(0xFF7C3AED)),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            if (state.commentInputText.isNotBlank() && !state.isSubmittingComment) {
                                Color(0xFF7C3AED)
                            } else {
                                Color(0xFF2E2050)
                            }
                        )
                        .clickable(enabled = state.commentInputText.isNotBlank() && !state.isSubmittingComment) {
                            onIntent(IssueDetailIntent.SubmitComment)
                        }
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (state.isSubmittingComment) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "ارسال نظر",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "ارسال نظر",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // 8. LOG WORK DIALOG
        if (state.isLogWorkDialogOpen) {
            LogWorkDialog(
                issueKey = state.issueKey,
                isLogging = state.isLoggingWork,
                onDismiss = { onIntent(IssueDetailIntent.DismissLogWorkDialog) },
                onSubmit = { minutes, comment ->
                    onIntent(IssueDetailIntent.SubmitLogWork(minutes, comment))
                }
            )
        }
    }
}

@Composable
private fun LogWorkDialog(
    issueKey: String,
    isLogging: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (minutes: Int, comment: String?) -> Unit
) {
    var minutesText by remember { mutableStateOf("60") }
    var commentText by remember { mutableStateOf("") }
    val presets = listOf(15, 30, 45, 60, 120, 240)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF171B26)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF283042))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ثبت کارکرد جدید (Log Work)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "برای تسک $issueKey",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFFA78BFA),
                        modifier = Modifier.size(22.dp)
                    )
                }

                HorizontalDivider(color = Color(0xFF222838), thickness = 1.dp)

                // Quick presets
                Text(
                    text = "انتخاب سریع مدت زمان:",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1),
                    fontWeight = FontWeight.Medium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presets.take(3).forEach { m ->
                        val isSelected = minutesText == m.toString()
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { minutesText = m.toString() },
                            color = if (isSelected) Color(0xFF7C3AED) else Color(0xFF222838),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFFA78BFA) else Color(0xFF333B4F)
                            )
                        ) {
                            Text(
                                text = "$m دقیقه",
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presets.drop(3).forEach { m ->
                        val isSelected = minutesText == m.toString()
                        val label = if (m >= 60) "${m / 60} ساعت" else "$m دقیقه"
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { minutesText = m.toString() },
                            color = if (isSelected) Color(0xFF7C3AED) else Color(0xFF222838),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFFA78BFA) else Color(0xFF333B4F)
                            )
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }

                // Custom Minutes Input
                OutlinedTextField(
                    value = minutesText,
                    onValueChange = { minutesText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("مدت زمان صرف‌شده (دقیقه)", fontSize = 11.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF7C3AED),
                        unfocusedBorderColor = Color(0xFF333B4F),
                        focusedLabelColor = Color(0xFFA78BFA),
                        unfocusedLabelColor = Color(0xFF94A3B8)
                    )
                )

                // Optional Comment Input
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    label = { Text("توضیحات کارکرد (اختیاری)", fontSize = 11.sp) },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF7C3AED),
                        unfocusedBorderColor = Color(0xFF333B4F),
                        focusedLabelColor = Color(0xFFA78BFA),
                        unfocusedLabelColor = Color(0xFF94A3B8)
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        enabled = !isLogging
                    ) {
                        Text("انصراف", color = Color(0xFF94A3B8), fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            val mins = minutesText.toIntOrNull() ?: 0
                            onSubmit(mins, commentText.ifBlank { null })
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isLogging && (minutesText.toIntOrNull() ?: 0) > 0,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isLogging) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text("ثبت کارکرد", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Issue Detail Screen Dark Mode", showBackground = true, backgroundColor = 0xFF0B0C0E)
@Composable
private fun IssueDetailScreenPreview() {
    OmniSpaceTheme(themeMode = OmniThemeMode.DARK) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            IssueDetailContent(
                state = IssueDetailUiState(),
                onIntent = {}
            )
        }
    }
}

