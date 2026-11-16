package amirparsa.shk.omnispace.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
fun OmniFilterChip(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    trailingBadge: String? = null
) {
    val bgColor = if (isSelected) OmniTheme.colors.primaryViolet else OmniTheme.colors.card
    val borderColor = if (isSelected) OmniTheme.colors.primaryViolet else OmniTheme.colors.borderSubtle
    val textColor = if (isSelected) OmniTheme.colors.onPrimary else OmniTheme.colors.textSecondary

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = textColor
            )
            if (!trailingBadge.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (isSelected) Color.White.copy(alpha = 0.25f)
                            else OmniTheme.colors.primaryViolet.copy(alpha = 0.15f)
                        )
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = trailingBadge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else OmniTheme.colors.primaryViolet
                    )
                }
            }
        }
    }
}

@Composable
fun OmniChipGroup(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier.horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEachIndexed { index, item ->
            OmniFilterChip(
                text = item,
                isSelected = index == selectedIndex,
                onClick = { onItemSelected(index) }
            )
        }
    }
}
