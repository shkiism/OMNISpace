package amirparsa.shk.omnispace.feature.notification.presentation.inbox

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import amirparsa.shk.omnispace.feature.notification.domain.model.NotificationCategory
import amirparsa.shk.omnispace.feature.notification.domain.model.NotificationItem

/**
 * Stateful entry composable for Notification Inbox following MVI architecture.
 */
@Composable
fun NotificationInboxScreen(
    viewModel: NotificationInboxViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onOpenIssue: (String) -> Unit = {},
    onJoinMeeting: () -> Unit = {},
    onViewPayslip: () -> Unit = {},
    onOpenZephyrQa: () -> Unit = {},
    onOpenSystemNotificationShade: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                NotificationInboxEffect.NavigateBack -> onBackClick()
                is NotificationInboxEffect.OpenIssue -> onOpenIssue(effect.key)
                NotificationInboxEffect.JoinMeeting -> onJoinMeeting()
                NotificationInboxEffect.ViewPayslip -> onViewPayslip()
                NotificationInboxEffect.OpenZephyrQa -> onOpenZephyrQa()
                NotificationInboxEffect.OpenSystemNotificationShade -> onOpenSystemNotificationShade()
                is NotificationInboxEffect.ShowToast -> { /* Show Toast if needed */ }
            }
        }
    }

    NotificationInboxContent(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier
    )
}

/**
 * Pure stateless Composable rendering the Notification Inbox.
 */
@Composable
fun NotificationInboxContent(
    state: NotificationInboxUiState,
    onIntent: (NotificationInboxIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OmniTheme.colors.canvas)
    ) {
        // TOP APP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(OmniTheme.colors.card)
                .border(0.5.dp, OmniTheme.colors.borderSubtle)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { onIntent(NotificationInboxIntent.BackClicked) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "بازگشت",
                        tint = OmniTheme.colors.textPrimary
                    )
                }
                Text(
                    text = "مرکز اعلانات",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OmniTheme.colors.textPrimary
                )
                if (state.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEF4444).copy(alpha = 0.15f))
                            .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${state.unreadCount} جدید",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                    }
                }
            }

            if (state.unreadCount > 0) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onIntent(NotificationInboxIntent.MarkAllAsRead) }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = null,
                        tint = Color(0xFF9D7BF6),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "خواندن همه",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF9D7BF6)
                    )
                }
            }
        }

        // NATIVE ANDROID NOTIFICATIONS BANNER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF7C3AED).copy(alpha = 0.12f))
                .border(1.dp, Color(0xFF7C3AED).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                .clickable { onIntent(NotificationInboxIntent.OpenSystemNotificationShadeClicked) }
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
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
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF7C3AED).copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = Color(0xFFC4B5FD),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "اعلانات بومی سیستم‌عامل (خارج از برنامه)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "مشاهده شبیه‌ساز صفحه قفل و پنل اعلانات اندروید (Lockscreen Shade)",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
                Text(
                    text = "مشاهده ↗",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC4B5FD)
                )
            }
        }

        // FILTER CHIPS ROW
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NotificationCategory.entries.forEach { category ->
                val isSelected = category == state.selectedCategory
                val count = if (category == NotificationCategory.ALL) {
                    state.notifications.size
                } else {
                    state.notifications.count { it.category == category }
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
                        .clickable { onIntent(NotificationInboxIntent.SelectCategory(category)) }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = category.titleFa,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else Color(0xFF94A3B8)
                    )
                    if (count > 0) {
                        Text(
                            text = count.toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color(0xFFEDE9FE) else Color(0xFF64748B)
                        )
                    }
                }
            }
        }

        // NOTIFICATIONS LIST
        if (state.filteredList.isEmpty()) {
            if (state.isLoading) {
                OmniShimmerList(
                    count = 4,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = Color(0xFF475569),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "هیچ اعلانی در این دسته وجود ندارد",
                            fontSize = 14.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.filteredList, key = { it.id }) { item ->
                    NotificationCard(
                        item = item,
                        onClick = { onIntent(NotificationInboxIntent.NotificationClicked(item)) },
                        onActionClick = { onIntent(NotificationInboxIntent.ActionClicked(item)) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    item: NotificationItem,
    onClick: () -> Unit,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (item.isUnread) Color(0xFF161B26) else Color(0xFF11141C))
            .border(
                1.dp,
                if (item.isUnread) Color(0xFF9D7BF6).copy(alpha = 0.35f) else Color(0xFF1E2433),
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(item.iconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = item.iconTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = item.title,
                            fontSize = 13.sp,
                            fontWeight = if (item.isUnread) FontWeight.Bold else FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = item.timeAgo,
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                if (item.isUnread) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF06B6D4))
                    )
                }
            }

            Text(
                text = item.description,
                fontSize = 12.sp,
                lineHeight = 19.sp,
                color = if (item.isUnread) Color(0xFFE2E8F0) else Color(0xFF94A3B8)
            )

            if (item.actionText != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF7C3AED).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFF7C3AED).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .clickable { onActionClick() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = item.actionText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC4B5FD)
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Notification Inbox RTL", showBackground = true)
@Composable
fun NotificationInboxPreview() {
    OmniSpaceTheme(themeMode = OmniThemeMode.DARK) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            NotificationInboxContent(
                state = NotificationInboxUiState(),
                onIntent = {}
            )
        }
    }
}

