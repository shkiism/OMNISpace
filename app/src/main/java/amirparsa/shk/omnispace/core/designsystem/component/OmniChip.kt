package amirparsa.shk.omnispace.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import amirparsa.shk.omnispace.core.designsystem.OmniTheme

enum class IssueStatus(val labelFa: String, val labelEn: String) {
    BACKLOG("بک‌لاگ", "Backlog"),
    TODO("برای انجام", "To Do"),
    IN_PROGRESS("در حال انجام", "In Progress"),
    QA("بررسی کیفیت", "QA Testing"),
    DONE("انجام شده", "Done")
}

enum class IssuePriority(val labelFa: String, val labelEn: String) {
    HIGH("بالا", "High"),
    MEDIUM("متوسط", "Medium"),
    LOW("پایین", "Low")
}

@Composable
fun StatusChip(
    status: IssueStatus,
    modifier: Modifier = Modifier,
    usePersian: Boolean = true
) {
    val (bgColor, borderColor, textColor, hasCheck) = when (status) {
        IssueStatus.BACKLOG -> Quad(
            OmniTheme.colors.elevated,
            OmniTheme.colors.borderSubtle,
            OmniTheme.colors.textSecondary,
            false
        )
        IssueStatus.TODO -> Quad(
            OmniTheme.colors.elevated,
            OmniTheme.colors.borderSubtle,
            OmniTheme.colors.textPrimary,
            false
        )
        IssueStatus.IN_PROGRESS -> Quad(
            OmniTheme.colors.primaryViolet.copy(alpha = 0.15f),
            OmniTheme.colors.primaryViolet.copy(alpha = 0.4f),
            OmniTheme.colors.primaryViolet,
            false
        )
        IssueStatus.QA -> Quad(
            OmniTheme.colors.statusWarning.copy(alpha = 0.15f),
            OmniTheme.colors.statusWarning.copy(alpha = 0.4f),
            OmniTheme.colors.statusWarning,
            false
        )
        IssueStatus.DONE -> Quad(
            OmniTheme.colors.statusSuccess.copy(alpha = 0.15f),
            OmniTheme.colors.statusSuccess.copy(alpha = 0.4f),
            OmniTheme.colors.statusSuccess,
            true
        )
    }

    Row(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(9999.dp))
            .border(1.dp, borderColor, RoundedCornerShape(9999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (hasCheck) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        } else {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(textColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }

        Text(
            text = if (usePersian) status.labelFa else status.labelEn,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PriorityChip(
    priority: IssuePriority,
    modifier: Modifier = Modifier,
    usePersian: Boolean = true
) {
    val (bgColor, borderColor, textColor, icon) = when (priority) {
        IssuePriority.HIGH -> Quad(
            OmniTheme.colors.statusDanger.copy(alpha = 0.15f),
            OmniTheme.colors.statusDanger.copy(alpha = 0.3f),
            OmniTheme.colors.statusDanger,
            Icons.Default.KeyboardArrowUp
        )
        IssuePriority.MEDIUM -> Quad(
            OmniTheme.colors.statusWarning.copy(alpha = 0.15f),
            OmniTheme.colors.statusWarning.copy(alpha = 0.3f),
            OmniTheme.colors.statusWarning,
            Icons.Default.Remove
        )
        IssuePriority.LOW -> Quad(
            OmniTheme.colors.elevated,
            OmniTheme.colors.borderSubtle,
            OmniTheme.colors.textSecondary,
            Icons.Default.KeyboardArrowDown
        )
    }

    Row(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(9999.dp))
            .border(1.dp, borderColor, RoundedCornerShape(9999.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = if (usePersian) priority.labelFa else priority.labelEn,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private data class Quad<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

