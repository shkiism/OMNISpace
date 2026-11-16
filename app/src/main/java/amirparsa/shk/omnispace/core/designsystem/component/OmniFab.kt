package amirparsa.shk.omnispace.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import amirparsa.shk.omnispace.core.designsystem.OmniTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OmniFab(
    onNewTaskClick: () -> Unit = {},
    onNewLeaveClick: () -> Unit = {},
    onNewMeetingClick: () -> Unit = {},
    onNewNoteClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showSheet by remember { mutableStateOf(false) }

    FloatingActionButton(
        onClick = { showSheet = true },
        shape = CircleShape,
        containerColor = OmniTheme.colors.primaryViolet,
        contentColor = OmniTheme.colors.onPrimary,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
        modifier = modifier.size(56.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Quick Action",
            modifier = Modifier.size(28.dp)
        )
    }

    if (showSheet) {
        OmniBottomSheet(
            onDismissRequest = { showSheet = false }
        ) {
            Text(
                text = "عملیات سریع",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = OmniTheme.colors.textPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickActionItem(
                    title = "ایجاد تسک جدید",
                    subtitle = "افزودن به کارهای امروز یا پروژه",
                    icon = Icons.Default.AddTask,
                    onClick = {
                        showSheet = false
                        onNewTaskClick()
                    }
                )

                QuickActionItem(
                    title = "ثبت مرخصی یا تردد",
                    subtitle = "درخواست مرخصی استحقاقی یا ساعتی",
                    icon = Icons.Default.BeachAccess,
                    onClick = {
                        showSheet = false
                        onNewLeaveClick()
                    }
                )

                QuickActionItem(
                    title = "شروع جلسه تصویری",
                    subtitle = "ایجاد اتاق ویدیویی LiveKit فوری",
                    icon = Icons.Default.VideoCall,
                    onClick = {
                        showSheet = false
                        onNewMeetingClick()
                    }
                )

                QuickActionItem(
                    title = "یادداشت جدید",
                    subtitle = "پیش‌نویس شخصی و فوری",
                    icon = Icons.Default.EditNote,
                    onClick = {
                        showSheet = false
                        onNewNoteClick()
                    }
                )
            }
        }
    }
}

@Composable
private fun QuickActionItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(OmniTheme.colors.card)
            .border(1.dp, OmniTheme.colors.borderSubtle, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(OmniTheme.colors.primaryContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = OmniTheme.colors.primaryLight,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = OmniTheme.colors.textPrimary
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = OmniTheme.colors.textMuted
            )
        }
    }
}
