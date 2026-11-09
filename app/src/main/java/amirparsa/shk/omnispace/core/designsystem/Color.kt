package amirparsa.shk.omnispace.core.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// --- Seed & Accent Colors ---
val PrimaryViolet = Color(0xFF9D7BF6)
val PrimaryHover = Color(0xFF8C67F4)
val PrimaryContainer = Color(0xFF330081)
val OnPrimary = Color(0xFF0B0C0E)

// --- Surfaces & Canvas ---
val CanvasBackground = Color(0xFF0B0C0E)
val SurfaceCard = Color(0xFF13161C)
val SurfaceElevated = Color(0xFF1A1E26)
val SurfaceContainerLowest = Color(0xFF0C0E11)
val SurfaceContainerHigh = Color(0xFF282A2D)

// --- Borders & Outlines ---
val BorderSubtle = Color(0xFF1F242E)
val BorderFocus = Color(0xFF9D7BF6)

// --- Semantic Status Colors ---
val StatusSuccess = Color(0xFF10B981)
val StatusWarning = Color(0xFFF59E0B)
val StatusDanger = Color(0xFFEF4444)

// --- Text Colors ---
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

@Immutable
data class OmniColors(
    val primaryViolet: Color = PrimaryViolet,
    val primaryHover: Color = PrimaryHover,
    val onPrimary: Color = OnPrimary,
    val canvas: Color = CanvasBackground,
    val card: Color = SurfaceCard,
    val elevated: Color = SurfaceElevated,
    val borderSubtle: Color = BorderSubtle,
    val borderFocus: Color = BorderFocus,
    val statusSuccess: Color = StatusSuccess,
    val statusWarning: Color = StatusWarning,
    val statusDanger: Color = StatusDanger,
    val textPrimary: Color = TextPrimary,
    val textSecondary: Color = TextSecondary,
    val textMuted: Color = TextMuted,
)

val LocalOmniColors = staticCompositionLocalOf { OmniColors() }

