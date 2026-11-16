package amirparsa.shk.omnispace.core.designsystem

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

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
    themeMode: OmniThemeMode = OmniThemeManager.themeMode,
    content: @Composable () -> Unit
) {
    val omniColors = getOmniColors(themeMode)

    val materialColorScheme = if (omniColors.isDark) {
        darkColorScheme(
            primary = omniColors.primaryViolet,
            onPrimary = omniColors.onPrimary,
            primaryContainer = omniColors.primaryContainer,
            background = omniColors.canvas,
            onBackground = omniColors.textPrimary,
            surface = omniColors.card,
            onSurface = omniColors.textPrimary,
            surfaceVariant = omniColors.elevated,
            onSurfaceVariant = omniColors.textSecondary,
            outline = omniColors.borderSubtle,
            outlineVariant = omniColors.borderFocus,
            error = omniColors.statusDanger,
            onError = omniColors.textPrimary
        )
    } else {
        lightColorScheme(
            primary = omniColors.primaryViolet,
            onPrimary = omniColors.onPrimary,
            primaryContainer = omniColors.primaryContainer,
            background = omniColors.canvas,
            onBackground = omniColors.textPrimary,
            surface = omniColors.card,
            onSurface = omniColors.textPrimary,
            surfaceVariant = omniColors.elevated,
            onSurfaceVariant = omniColors.textSecondary,
            outline = omniColors.borderSubtle,
            outlineVariant = omniColors.borderFocus,
            error = omniColors.statusDanger,
            onError = omniColors.textPrimary
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = omniColors.canvas.toArgb()
            window.navigationBarColor = omniColors.card.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !omniColors.isDark
            controller.isAppearanceLightNavigationBars = !omniColors.isDark
        }
    }

    CompositionLocalProvider(LocalOmniColors provides omniColors) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            typography = OmniTypography,
            content = content
        )
    }
}
