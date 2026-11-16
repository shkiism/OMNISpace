package amirparsa.shk.omnispace.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import amirparsa.shk.omnispace.core.designsystem.OmniTheme

enum class OmniBadgeType {
    SUCCESS,
    WARNING,
    DANGER,
    INFO,
    PRIMARY
}

@Composable
fun OmniStatusBadge(
    text: String,
    type: OmniBadgeType = OmniBadgeType.PRIMARY,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (type) {
        OmniBadgeType.SUCCESS -> Pair(OmniTheme.colors.statusSuccess.copy(alpha = 0.14f), OmniTheme.colors.statusSuccess)
        OmniBadgeType.WARNING -> Pair(OmniTheme.colors.statusWarning.copy(alpha = 0.14f), OmniTheme.colors.statusWarning)
        OmniBadgeType.DANGER -> Pair(OmniTheme.colors.statusDanger.copy(alpha = 0.14f), OmniTheme.colors.statusDanger)
        OmniBadgeType.INFO -> Pair(OmniTheme.colors.statusInfo.copy(alpha = 0.14f), OmniTheme.colors.statusInfo)
        OmniBadgeType.PRIMARY -> Pair(OmniTheme.colors.primaryContainer.copy(alpha = 0.4f), OmniTheme.colors.primaryLight)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

