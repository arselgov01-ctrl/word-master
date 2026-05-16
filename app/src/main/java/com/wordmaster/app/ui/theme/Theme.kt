package com.wordmaster.app.ui.theme

import android.app.Activity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ButtonBlue,
    onPrimary = TextWhite,
    primaryContainer = SecondaryDark,
    onPrimaryContainer = TextWhite,
    secondary = ButtonPurple,
    onSecondary = TextWhite,
    secondaryContainer = AccentPurple,
    onSecondaryContainer = TextWhite,
    tertiary = ButtonTeal,
    onTertiary = TextWhite,
    background = BackgroundDark,
    onBackground = TextWhite,
    surface = BackgroundCard,
    onSurface = TextWhite,
    surfaceVariant = BackgroundCardLight,
    onSurfaceVariant = TextGray,
    error = WrongRed,
    onError = TextWhite,
    outline = TextMuted
)

@Composable
fun WordMasterTheme(content: @Composable () -> Unit) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BackgroundDark.toArgb()
            window.navigationBarColor = BackgroundDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
