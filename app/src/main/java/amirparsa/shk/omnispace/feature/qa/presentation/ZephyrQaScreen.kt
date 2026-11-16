package amirparsa.shk.omnispace.feature.qa.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import amirparsa.shk.omnispace.core.designsystem.OmniSpaceTheme
import amirparsa.shk.omnispace.core.designsystem.OmniTheme
import amirparsa.shk.omnispace.core.designsystem.OmniThemeMode
import amirparsa.shk.omnispace.core.designsystem.component.OmniShimmerList
import amirparsa.shk.omnispace.feature.qa.domain.model.TestStatus
import amirparsa.shk.omnispace.feature.qa.domain.model.ZephyrTestCase

/**
 * Stateful entry composable for Zephyr QA screen following MVI architecture.
 */
@Composable
fun ZephyrQaScreen(
    cycleName: String = "اسپرینت ۲۴ - نسخه موبایل v2.4",
    onBackClick: () -> Unit = {},
    onCreateBugClick: (ZephyrTestCase) -> Unit = {},
    viewModel: ZephyrQaViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ZephyrQaEffect.NavigateBack -> onBackClick()
                is ZephyrQaEffect.OpenCreateBug -> onCreateBugClick(effect.testCase)
            }
        }
    }

    ZephyrQaContent(
        state = state.copy(cycleName = cycleName),
        onIntent = viewModel::onIntent,
        modifier = modifier
    )
}

/**
 * Stateless UI content composable for Zephyr QA screen.
 */
@Composable
fun ZephyrQaContent(
    state: ZephyrQaUiState,
    onIntent: (ZephyrQaIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(OmniTheme.colors.canvas)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
            ) {
                // 1. TOP APP BAR
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(OmniTheme.colors.card)
                        .border(0.5.dp, OmniTheme.colors.borderSubtle)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = { onIntent(ZephyrQaIntent.BackClicked) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "بازگشت",
                                tint = OmniTheme.colors.textPrimary
                            )
                        }

                        Column {
                            Text(
                                text = "مدیریت آزمون (Zephyr QA)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = OmniTheme.colors.textPrimary
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = state.cycleName,
                                    fontSize = 11.sp,
                                    color = Color(0xFF9D7BF6)
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color(0xFF9D7BF6),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E2433))
                            .clickable { onIntent(ZephyrQaIntent.RefreshClicked) }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "بروزرسانی",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
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
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF3B1E2B))
                            .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = state.error,
                            fontSize = 11.sp,
                            color = Color(0xFFFCA5A5)
                        )
                    }
                }

                // 2. SUMMARY METRICS CARD
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF13161F))
                        .border(1.dp, Color(0xFF232838), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "پیشرفت اجرای چرخه‌ی تست",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "${state.passPercentage}٪ موفق",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }

                        // Multi-segment progress bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF262C3C))
                        ) {
                            if (state.passCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .weight(state.passCount.toFloat())
                                        .fillMaxSize()
                                        .background(Color(0xFF10B981))
                                )
                            }
                            if (state.failCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .weight(state.failCount.toFloat())
                                        .fillMaxSize()
                                        .background(Color(0xFFEF4444))
                                )
                            }
                            if (state.blockedCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .weight(state.blockedCount.toFloat())
                                        .fillMaxSize()
                                        .background(Color(0xFFF59E0B))
                                )
                            }
                            if (state.unexecutedCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .weight(state.unexecutedCount.toFloat())
                                        .fillMaxSize()
                                        .background(Color(0xFF38BDF8))
                                )
                            }
                        }

                        // Stat tiles row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatTile(label = "کل تست‌ها", value = "${state.totalCount}", color = Color.White)
                            StatTile(label = "پاس شده", value = "${state.passCount}", color = Color(0xFF10B981))
                            StatTile(label = "شکست‌خورده", value = "${state.failCount}", color = Color(0xFFEF4444))
                            StatTile(label = "مسدود/باقی‌مانده", value = "${state.blockedCount + state.unexecutedCount}", color = Color(0xFFF59E0B))
                        }
                    }
                }

                // 3. FILTER TABS
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QaFilter.entries.forEach { filter ->
                        val isSelected = filter == state.selectedFilter
                        val count = when (filter) {
                            QaFilter.ALL -> state.totalCount
                            QaFilter.FAIL -> state.failCount
                            QaFilter.UNEXECUTED -> state.unexecutedCount + state.blockedCount
                            QaFilter.PASS -> state.passCount
                        }

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) Color(0xFF7C3AED) else Color(0xFF161A24))
                                .border(
                                    1.dp,
                                    if (isSelected) Color(0xFF7C3AED) else Color(0xFF262C3C),
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { onIntent(ZephyrQaIntent.SelectFilter(filter)) }
                                .padding(horizontal = 14.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = filter.titleFa,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF94A3B8)
                            )
                            Text(
                                text = count.toString(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color(0xFFEDE9FE) else Color(0xFF64748B)
                            )
                        }
                    }
                }

                // 4. TEST CASES LIST
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (state.filteredCases.isEmpty()) {
                        item {
                            if (state.isLoading) {
                                OmniShimmerList(count = 4)
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = state.error ?: "هیچ مورد آزمونی برای نمایش یافت نشد",
                                        fontSize = 12.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }

                    items(state.filteredCases, key = { it.id }) { tc ->
                        TestCaseCard(
                            testCase = tc,
                            onPassClick = { onIntent(ZephyrQaIntent.UpdateTestStatus(tc, TestStatus.PASS)) },
                            onFailClick = { onIntent(ZephyrQaIntent.UpdateTestStatus(tc, TestStatus.FAIL)) },
                            onBlockClick = { onIntent(ZephyrQaIntent.UpdateTestStatus(tc, TestStatus.BLOCKED)) },
                            onCreateBugClick = { onIntent(ZephyrQaIntent.CreateBugClicked(tc)) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }

            // UNDO SNACKBAR (Toast banner at bottom)
            AnimatedVisibility(
                visible = state.showUndoSnackbar,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E1B2E))
                        .border(1.dp, Color(0xFF7C3AED), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "وضعیت تست با موفقیت به‌روزرسانی شد.",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF7C3AED))
                                .clickable { onIntent(ZephyrQaIntent.UndoStatusUpdate) }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "بازگشت (Undo)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatTile(label: String, value: String, color: Color) {
    Column {
        Text(text = label, fontSize = 10.sp, color = Color(0xFF94A3B8))
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun TestCaseCard(
    testCase: ZephyrTestCase,
    onPassClick: () -> Unit,
    onFailClick: () -> Unit,
    onBlockClick: () -> Unit,
    onCreateBugClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF161A24))
            .border(1.dp, Color(0xFF262C3C), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header: ID badge + Module tag + Status pill
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
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF7C3AED).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFF7C3AED).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = testCase.id,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC4B5FD)
                        )
                    }

                    Text(
                        text = "[${testCase.module}]",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(testCase.status.bgColor)
                        .border(1.dp, testCase.status.color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = testCase.status.titleFa,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = testCase.status.color
                    )
                }
            }

            // Title
            Text(
                text = testCase.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Steps summary
            Text(
                text = testCase.stepsSummary,
                fontSize = 11.sp,
                lineHeight = 17.sp,
                color = Color(0xFF94A3B8)
            )

            // Error callout (if failed)
            if (testCase.errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEF4444).copy(alpha = 0.12f))
                        .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = testCase.errorMessage,
                            fontSize = 11.sp,
                            color = Color(0xFFFCA5A5)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFEF4444))
                                    .clickable { onCreateBugClick() }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BugReport,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "ثبت خودکار باگ در جسیرا",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Footer: Execution meta + 1-Tap Quick Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (testCase.executedBy != null) {
                    Text(
                        text = "اجرا: ${testCase.executedBy} • ${testCase.executionTime}",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                } else {
                    Text(
                        text = "هنوز اجرا نشده است",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                }

                // Quick 1-tap execution buttons
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Pass
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.5f), CircleShape)
                            .clickable { onPassClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Pass",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Fail
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f), CircleShape)
                            .clickable { onFailClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fail",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Block
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF59E0B).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f), CircleShape)
                            .clickable { onBlockClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = "Block",
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Zephyr QA RTL", showBackground = true)
@Composable
fun ZephyrQaPreview() {
    OmniSpaceTheme(themeMode = OmniThemeMode.DARK) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ZephyrQaContent(
                state = ZephyrQaUiState(),
                onIntent = {}
            )
        }
    }
}

