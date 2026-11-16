package amirparsa.shk.omnispace.core.designsystem.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import amirparsa.shk.omnispace.core.designsystem.OmniTheme

/**
 * Animated Shimmer modifier providing a modern, smooth sweep loading effect.
 */
fun Modifier.omniShimmer(
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(8.dp)
): Modifier = composed {
    if (!enabled) return@composed this

    val transition = rememberInfiniteTransition(label = "OmniShimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = -800f,
        targetValue = 1600f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "OmniShimmerTranslation"
    )

    val baseColor = OmniTheme.colors.card.copy(alpha = 0.85f)
    val highlightColor = OmniTheme.colors.elevated.copy(alpha = 0.95f)

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            baseColor,
            highlightColor,
            baseColor
        ),
        start = Offset(translateAnim, translateAnim * 0.4f),
        end = Offset(translateAnim + 500f, translateAnim * 0.4f + 300f)
    )

    this
        .clip(shape)
        .background(shimmerBrush)
}

/**
 * Simple rectangular shimmer placeholder.
 */
@Composable
fun OmniShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    Box(
        modifier = modifier.omniShimmer(enabled = true, shape = shape)
    )
}

/**
 * Shimmer card placeholder matching card layouts across OMNISpace.
 */
@Composable
fun OmniShimmerCard(
    modifier: Modifier = Modifier,
    height: Dp = 100.dp,
    shape: Shape = RoundedCornerShape(16.dp)
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .border(1.dp, OmniTheme.colors.borderSubtle, shape)
            .omniShimmer(enabled = true, shape = shape)
    )
}

/**
 * Shimmer task/item row placeholder.
 */
@Composable
fun OmniShimmerItemRow(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(14.dp)
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(OmniTheme.colors.card)
            .border(1.dp, OmniTheme.colors.borderSubtle, shape)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .omniShimmer(shape = CircleShape)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OmniShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(14.dp),
                    shape = RoundedCornerShape(4.dp)
                )
                OmniShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.35f)
                        .height(10.dp),
                    shape = RoundedCornerShape(4.dp)
                )
            }

            OmniShimmerBox(
                modifier = Modifier
                    .width(48.dp)
                    .height(20.dp),
                shape = RoundedCornerShape(6.dp)
            )
        }
    }
}

/**
 * Shimmer list showing multiple placeholder rows.
 */
@Composable
fun OmniShimmerList(
    count: Int = 3,
    modifier: Modifier = Modifier,
    spacing: Dp = 10.dp
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        repeat(count) {
            OmniShimmerItemRow()
        }
    }
}

