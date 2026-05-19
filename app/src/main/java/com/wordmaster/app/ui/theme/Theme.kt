package com.wordmaster.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.wordmaster.app.settings.ThemeMode

/**
 * Semantic color tokens used across all screens. Provided via [LocalAppColors] from
 * [WordMasterTheme] so light/dark switching works without touching individual screens.
 */
data class AppColors(
    val background: Color,
    val surfaceCard: Color,
    val surfaceCardElevated: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val cardGradientStart: Color,
    val cardGradientEnd: Color,
    val onGradient: Color,
    val answerLetterBg: Color,
    val divider: Color
)

private val DarkAppColors = AppColors(
    background = DarkBackground,
    surfaceCard = DarkSurfaceCard,
    surfaceCardElevated = DarkSurfaceCardElevated,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    textMuted = DarkTextMuted,
    cardGradientStart = DarkCardGradientStart,
    cardGradientEnd = DarkCardGradientEnd,
    onGradient = DarkTextPrimary,
    answerLetterBg = DarkAnswerLetterBg,
    divider = DarkTextMuted.copy(alpha = 0.3f)
)

private val LightAppColors = AppColors(
    background = Color(0xFFF5F7FB),
    surfaceCard = Color(0xFFFFFFFF),
    surfaceCardElevated = Color(0xFFEEF1F6),
    textPrimary = Color(0xFF111418),
    textSecondary = Color(0xFF52606D),
    textMuted = Color(0xFF9AA1AA),
    cardGradientStart = Color(0xFF3F69C6),
    cardGradientEnd = Color(0xFF6E47B0),
    onGradient = Color(0xFFFFFFFF),
    answerLetterBg = Color(0xFFDCE7FF),
    divider = Color(0xFFCAD0DA)
)

val LocalAppColors = staticCompositionLocalOf { DarkAppColors }

object WordMasterAppTheme {
    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current
}

private val DarkColorScheme = darkColorScheme(
    primary = ButtonBlue,
    onPrimary = DarkTextPrimary,
    primaryContainer = DarkAnswerLetterBg,
    onPrimaryContainer = DarkTextPrimary,
    secondary = ButtonPurple,
    onSecondary = DarkTextPrimary,
    secondaryContainer = AccentPurple,
    onSecondaryContainer = DarkTextPrimary,
    tertiary = ButtonTeal,
    onTertiary = DarkTextPrimary,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurfaceCard,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceCardElevated,
    onSurfaceVariant = DarkTextSecondary,
    error = WrongRed,
    onError = DarkTextPrimary,
    outline = DarkTextMuted
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
    val appColors = if (useDark) DarkAppColors else LightAppColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val statusColor = appColors.background.toArgb()
            window.statusBarColor = statusColor
            window.navigationBarColor = statusColor
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDark
        }
    }
    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
