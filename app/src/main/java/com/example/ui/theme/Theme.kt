package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ElegantLavender,
    onPrimary = ElegantLavenderOn,
    primaryContainer = SosaSurfaceInset,
    onPrimaryContainer = ElegantLavender,
    secondary = ElegantIceBlue,
    onSecondary = OnPrimaryDark,
    secondaryContainer = SosaSurfaceCard,
    onSecondaryContainer = ElegantIceBlue,
    tertiary = ElegantLiveGreen,
    background = SosaBackground,
    onBackground = SosaTextPrimary,
    surface = SosaSurfaceDark,
    onSurface = SosaTextPrimary,
    surfaceVariant = SosaSurfaceCard,
    onSurfaceVariant = SosaTextSecondary,
    outline = SosaBorder,
    outlineVariant = SosaBorderSubtle
)

@Composable
fun SosaXAITheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun SosaXAiTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    SosaXAITheme(darkTheme = darkTheme, content = content)
}
