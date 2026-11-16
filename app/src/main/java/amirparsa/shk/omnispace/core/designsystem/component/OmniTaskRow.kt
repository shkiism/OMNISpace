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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import amirparsa.shk.omnispace.core.designsystem.OmniTheme

enum class OmniTaskPriority(val titleFa: String, val color: Color) {
    HIGH("بالا", Color(0xFFEF4444)),
    MEDIUM("متوسط", Color(0xFFF59E0B)),
    LOW("عادی", Color(0xFF38BDF8))
}

@Composable
fun OmniTaskRow(
    title: String,
    projectKey: String = "MD",
    isCompleted: Boolean = false,
    priority: OmniTaskPriority = OmniTaskPriority.MEDIUM,
    dueTime: String? = null,
    onToggle: (Boolean) -> Unit = {},
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left/Start: Checkbox & Title & Project Tag
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Interactive Checkbox
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isCompleted) OmniTheme.colors.primaryViolet else Color.Transparent)
                    .border(
                        1.5.dp,
                        if (isCompleted) OmniTheme.colors.primaryViolet else OmniTheme.colors.borderSubtle,
                        RoundedCornerShape(6.dp)
                    )
                    .clickable { onToggle(!isCompleted) },
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = OmniTheme.colors.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.Medium,
                    color = if (isCompleted) OmniTheme.colors.textMuted else OmniTheme.colors.textPrimary,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Project Key Badge
                    Text(
                        text = projectKey,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = OmniTheme.colors.primaryLight,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(OmniTheme.colors.primaryContainer.copy(alpha = 0.5f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                    if (dueTime != null) {
                        Text(
                            text = "· $dueTime",
                            fontSize = 10.sp,
                            color = OmniTheme.colors.textMuted
                        )
                    }
                }
            }
        }

        // Right/End: Priority Dot
        Box(
            modifier = Modifier
                .padding(start = 8.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(priority.color)
        )
    }
}

