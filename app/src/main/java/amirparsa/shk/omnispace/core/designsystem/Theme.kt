package amirparsa.shk.omnispace.core.designsystem

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryViolet,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    background = CanvasBackground,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    outlineVariant = BorderFocus,
    error = StatusDanger,
    onError = TextPrimary,
)

object OmniTheme {
    val colors: OmniColors
        @Composable
        @ReadOnlyComposable
        get() = LocalOmniColors.current

    val typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography
}

@Composable
fun OmniSpaceTheme(
    darkTheme: Boolean = true, // Dark by default for Obsidian Violet executive look
    content: @Composable () -> Unit
) {
    val omniColors = OmniColors()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = CanvasBackground.toArgb()
            window.navigationBarColor = CanvasBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    CompositionLocalProvider(LocalOmniColors provides omniColors) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            typography = OmniTypography,
            content = content
        )
    }
}

