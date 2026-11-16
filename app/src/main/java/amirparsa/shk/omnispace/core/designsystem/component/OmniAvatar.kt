package amirparsa.shk.omnispace.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import amirparsa.shk.omnispace.core.designsystem.OmniTheme
import amirparsa.shk.omnispace.core.designsystem.PrimaryViolet
import amirparsa.shk.omnispace.core.designsystem.StatusSuccess

@Composable
fun OmniAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    isOnline: Boolean = true,
    statusColor: Color? = null
) {
    val initials = name.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")
        .ifEmpty { "U" }

    val gradientBrush = Brush.linearGradient(
        listOf(
            PrimaryViolet,
            Color(0xFF6366F1),
            Color(0xFF3B82F6)
        )
    )

    val effectiveStatusColor = statusColor ?: if (isOnline) StatusSuccess else null

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Outer Gradient ring
        Box(
            modifier = Modifier
                .size(size)
                .background(gradientBrush, CircleShape)
                .padding(2.dp)
        ) {
            // Inner dark surface
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(OmniTheme.colors.card, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = Color.White,
                    fontSize = (size.value * 0.35f).sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Presence status dot
        if (effectiveStatusColor != null) {
            Box(
                modifier = Modifier
                    .size(size * 0.28f)
                    .align(Alignment.BottomEnd)
                    .offset(x = 1.dp, y = 1.dp)
                    .background(effectiveStatusColor, CircleShape)
                    .border(2.dp, OmniTheme.colors.canvas, CircleShape)
            )
        }
    }
}
