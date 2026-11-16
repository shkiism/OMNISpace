package amirparsa.shk.omnispace.core.designsystem

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// =========================================================================
// 1. Obsidian Violet Executive (Standard Dark - Default)
// =========================================================================
val DarkPrimaryViolet = Color(0xFF9D7BF6)
val DarkPrimaryHover = Color(0xFF8C67F4)
val DarkPrimaryContainer = Color(0xFF4E3896)
val DarkPrimaryLight = Color(0xFFCCBEFF)
val DarkOnPrimary = Color(0xFF0B0C0E)

val DarkCanvas = Color(0xFF0B0C0E)
val DarkCard = Color(0xFF13161C)
val DarkElevated = Color(0xFF1A1E26)
val DarkSurfaceContainerLowest = Color(0xFF0C0E11)
val DarkSurfaceContainerHigh = Color(0xFF282A2D)

val DarkBorderSubtle = Color(0xFF1F242E)
val DarkBorderFocus = Color(0xFF9D7BF6)

val DarkStatusSuccess = Color(0xFF10B981)
val DarkStatusWarning = Color(0xFFF59E0B)
val DarkStatusDanger = Color(0xFFEF4444)
val DarkStatusInfo = Color(0xFF38BDF8)

val DarkTextPrimary = Color(0xFFFFFFFF)
val DarkTextSecondary = Color(0xFF94A3B8)
val DarkTextMuted = Color(0xFF64748B)

val DarkAvatarGradient = listOf(Color(0xFF9D7BF6), Color(0xFF6366F1), Color(0xFF3B82F6))

// =========================================================================
// 2. Pitch Black OLED (Deep Dark)
// =========================================================================
val DeepDarkPrimaryViolet = Color(0xFFA78BFA)
val DeepDarkPrimaryHover = Color(0xFF8B5CF6)
val DeepDarkPrimaryContainer = Color(0xFF1E1738)
val DeepDarkPrimaryLight = Color(0xFFCEBDFF)
val DeepDarkOnPrimary = Color(0xFF000000)

val DeepDarkCanvas = Color(0xFF000000)
val DeepDarkCard = Color(0xFF0B0C10)
val DeepDarkElevated = Color(0xFF12141A)
val DeepDarkSurfaceContainerLowest = Color(0xFF000000)
val DeepDarkSurfaceContainerHigh = Color(0xFF161922)

val DeepDarkBorderSubtle = Color(0xFF1A1D24)
val DeepDarkBorderFocus = Color(0xFF8B5CF6)

val DeepDarkStatusSuccess = Color(0xFF10B981)
val DeepDarkStatusWarning = Color(0xFFF59E0B)
val DeepDarkStatusDanger = Color(0xFFEF4444)
val DeepDarkStatusInfo = Color(0xFF38BDF8)

val DeepDarkTextPrimary = Color(0xFFF9FAFB)
val DeepDarkTextSecondary = Color(0xFF808898)
val DeepDarkTextMuted = Color(0xFF6B7280)

val DeepDarkAvatarGradient = listOf(Color(0xFFA78BFA), Color(0xFF8B5CF6), Color(0xFF6366F1))

// =========================================================================
// 3. Executive Daylight (Light Palette)
// =========================================================================
val LightPrimaryViolet = Color(0xFF6D28D9)
val LightPrimaryHover = Color(0xFF7C4DFF)
val LightPrimaryContainer = Color(0xFFEDE9FE)
val LightPrimaryLight = Color(0xFF7C4DFF)
val LightOnPrimary = Color(0xFFFFFFFF)

val LightCanvas = Color(0xFFF8F9FC)
val LightCard = Color(0xFFFFFFFF)
val LightElevated = Color(0xFFF1F5F9)
val LightSurfaceContainerLowest = Color(0xFFFFFFFF)
val LightSurfaceContainerHigh = Color(0xFFE2E8F0)

val LightBorderSubtle = Color(0xFFE2E6EF)
val LightBorderFocus = Color(0xFF6D28D9)

val LightStatusSuccess = Color(0xFF059669)
val LightStatusWarning = Color(0xFFD97706)
val LightStatusDanger = Color(0xFFDC2626)
val LightStatusInfo = Color(0xFF0284C7)

val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF64748B)
val LightTextMuted = Color(0xFF94A3B8)

val LightAvatarGradient = listOf(Color(0xFF6D28D9), Color(0xFF7C4DFF), Color(0xFF4F46E5))

// --- Backward Compatibility Aliases (Default Obsidian) ---
val PrimaryViolet = DarkPrimaryViolet
val PrimaryHover = DarkPrimaryHover
val PrimaryContainer = DarkPrimaryContainer
val PrimaryLight = DarkPrimaryLight
val OnPrimary = DarkOnPrimary
val CanvasBackground = DarkCanvas
val SurfaceCard = DarkCard
val SurfaceElevated = DarkElevated
val SurfaceContainerLowest = DarkSurfaceContainerLowest
val SurfaceContainerHigh = DarkSurfaceContainerHigh
val BorderSubtle = DarkBorderSubtle
val BorderFocus = DarkBorderFocus
val StatusSuccess = DarkStatusSuccess
val StatusWarning = DarkStatusWarning
val StatusDanger = DarkStatusDanger
val StatusInfo = DarkStatusInfo
val TextPrimary = DarkTextPrimary
val TextSecondary = DarkTextSecondary
val TextMuted = DarkTextMuted

enum class OmniThemeMode(val titleFa: String, val titleEn: String) {
    DARK("دارک (Obsidian)", "Dark (Obsidian)"),
    DEEP_DARK("دیپ دارک (OLED)", "Deep Dark (OLED)"),
    LIGHT("روشن (Daylight)", "Light (Daylight)")
}

@Immutable
data class OmniColors(
    val primaryViolet: Color = DarkPrimaryViolet,
    val primaryHover: Color = DarkPrimaryHover,
    val primaryContainer: Color = DarkPrimaryContainer,
    val primaryLight: Color = DarkPrimaryLight,
    val onPrimary: Color = DarkOnPrimary,
    val canvas: Color = DarkCanvas,
    val card: Color = DarkCard,
    val elevated: Color = DarkElevated,
    val surfaceContainerLowest: Color = DarkSurfaceContainerLowest,
    val surfaceContainerHigh: Color = DarkSurfaceContainerHigh,
    val borderSubtle: Color = DarkBorderSubtle,
    val borderFocus: Color = DarkBorderFocus,
    val statusSuccess: Color = DarkStatusSuccess,
    val statusWarning: Color = DarkStatusWarning,
    val statusDanger: Color = DarkStatusDanger,
    val statusInfo: Color = DarkStatusInfo,
    val textPrimary: Color = DarkTextPrimary,
    val textSecondary: Color = DarkTextSecondary,
    val textMuted: Color = DarkTextMuted,
    val avatarGradient: List<Color> = DarkAvatarGradient,
    val isDark: Boolean = true
)

fun getOmniColors(mode: OmniThemeMode): OmniColors {
    return when (mode) {
        OmniThemeMode.DARK -> OmniColors(
            primaryViolet = DarkPrimaryViolet,
            primaryHover = DarkPrimaryHover,
            primaryContainer = DarkPrimaryContainer,
            primaryLight = DarkPrimaryLight,
            onPrimary = DarkOnPrimary,
            canvas = DarkCanvas,
            card = DarkCard,
            elevated = DarkElevated,
            surfaceContainerLowest = DarkSurfaceContainerLowest,
            surfaceContainerHigh = DarkSurfaceContainerHigh,
            borderSubtle = DarkBorderSubtle,
            borderFocus = DarkBorderFocus,
            statusSuccess = DarkStatusSuccess,
            statusWarning = DarkStatusWarning,
            statusDanger = DarkStatusDanger,
            statusInfo = DarkStatusInfo,
            textPrimary = DarkTextPrimary,
            textSecondary = DarkTextSecondary,
            textMuted = DarkTextMuted,
            avatarGradient = DarkAvatarGradient,
            isDark = true
        )
        OmniThemeMode.DEEP_DARK -> OmniColors(
            primaryViolet = DeepDarkPrimaryViolet,
            primaryHover = DeepDarkPrimaryHover,
            primaryContainer = DeepDarkPrimaryContainer,
            primaryLight = DeepDarkPrimaryLight,
            onPrimary = DeepDarkOnPrimary,
            canvas = DeepDarkCanvas,
            card = DeepDarkCard,
            elevated = DeepDarkElevated,
            surfaceContainerLowest = DeepDarkSurfaceContainerLowest,
            surfaceContainerHigh = DeepDarkSurfaceContainerHigh,
            borderSubtle = DeepDarkBorderSubtle,
            borderFocus = DeepDarkBorderFocus,
            statusSuccess = DeepDarkStatusSuccess,
            statusWarning = DeepDarkStatusWarning,
            statusDanger = DeepDarkStatusDanger,
            statusInfo = DeepDarkStatusInfo,
            textPrimary = DeepDarkTextPrimary,
            textSecondary = DeepDarkTextSecondary,
            textMuted = DeepDarkTextMuted,
            avatarGradient = DeepDarkAvatarGradient,
            isDark = true
        )
        OmniThemeMode.LIGHT -> OmniColors(
            primaryViolet = LightPrimaryViolet,
            primaryHover = LightPrimaryHover,
            primaryContainer = LightPrimaryContainer,
            primaryLight = LightPrimaryLight,
            onPrimary = LightOnPrimary,
            canvas = LightCanvas,
            card = LightCard,
            elevated = LightElevated,
            surfaceContainerLowest = LightSurfaceContainerLowest,
            surfaceContainerHigh = LightSurfaceContainerHigh,
            borderSubtle = LightBorderSubtle,
            borderFocus = LightBorderFocus,
            statusSuccess = LightStatusSuccess,
            statusWarning = LightStatusWarning,
            statusDanger = LightStatusDanger,
            statusInfo = LightStatusInfo,
            textPrimary = LightTextPrimary,
            textSecondary = LightTextSecondary,
            textMuted = LightTextMuted,
            avatarGradient = LightAvatarGradient,
            isDark = false
        )
    }
}

val LocalOmniColors = staticCompositionLocalOf { getOmniColors(OmniThemeMode.DARK) }

object OmniThemeManager {
    private const val PREFS_NAME = "omni_theme_prefs"
    private const val KEY_THEME = "selected_theme_mode"

    var themeMode: OmniThemeMode by mutableStateOf(OmniThemeMode.DARK)
        private set

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedMode = prefs.getString(KEY_THEME, OmniThemeMode.DARK.name)
        themeMode = try {
            OmniThemeMode.valueOf(savedMode ?: OmniThemeMode.DARK.name)
        } catch (_: Exception) {
            OmniThemeMode.DARK
        }
    }

    fun setTheme(context: Context, mode: OmniThemeMode) {
        themeMode = mode
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_THEME, mode.name).apply()
    }
}
