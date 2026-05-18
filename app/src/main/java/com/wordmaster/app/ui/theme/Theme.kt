package com.wordmaster.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.wordmaster.app.settings.ThemeMode

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

// Светлая схема Material 3. Кастомные палитры (BackgroundDark и пр.)
// внутри приложения остаются прежними — они используются как акцентные тона
// карточек/градиентов и одинаково хорошо смотрятся в обоих режимах.
// Светлая тема меняет фон/поверхности на светлые и тёмный текст.
private val LightColorScheme = lightColorScheme(
    primary = ButtonBlue,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDCE7FF),
    onPrimaryContainer = Color(0xFF001A41),
    secondary = ButtonPurple,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEADDFF),
    onSecondaryContainer = Color(0xFF20005C),
    tertiary = ButtonTeal,
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFF7F8FB),
    onBackground = Color(0xFF111418),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF111418),
    surfaceVariant = Color(0xFFE7E9EF),
    onSurfaceVariant = Color(0xFF44474E),
    error = WrongRed,
    onError = Color(0xFFFFFFFF),
    outline = Color(0xFFB0B5BD)
)

@Composable
fun WordMasterTheme(
    themeMode: ThemeMode = ThemeMode.System,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val useDark = when (themeMode) {
        ThemeMode.System -> systemInDark
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    }
    val colorScheme = if (useDark) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val statusColor = if (useDark) BackgroundDark.toArgb() else colorScheme.background.toArgb()
            window.statusBarColor = statusColor
            window.navigationBarColor = statusColor
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDark
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
