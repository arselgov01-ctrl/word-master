package com.wordmaster.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// ---------------------------------------------------------------------------
// Static accent palette (used unchanged in both light and dark themes).
// ---------------------------------------------------------------------------

val PrimaryDark = Color(0xFF1A1A2E)
val PrimaryMid = Color(0xFF16213E)
val AccentBlue = Color(0xFF533483)
val AccentPurple = Color(0xFF7B2D8E)

// Яркие акценты
val CorrectGreen = Color(0xFF00C853)
val CorrectGreenLight = Color(0xFF69F0AE)
val WrongRed = Color(0xFFFF1744)
val WrongRedLight = Color(0xFFFF8A80)
val GoldYellow = Color(0xFFFFD600)
val GoldYellowLight = Color(0xFFFFF59D)

// Кнопки
val ButtonBlue = Color(0xFF2979FF)
val ButtonPurple = Color(0xFF7C4DFF)
val ButtonTeal = Color(0xFF00BFA5)

// Статус прогресса
val ProgressBronze = Color(0xFFCD7F32)
val ProgressSilver = Color(0xFFC0C0C0)
val ProgressGold = Color(0xFFFFD700)
val ProgressPlatinum = Color(0xFFE5E4E2)

// ---------------------------------------------------------------------------
// Raw dark-mode palette values. These are package-internal and are only
// referenced when constructing [DarkAppColors] / [DarkColorScheme] in
// Theme.kt. UI code references the theme-aware getters below.
// ---------------------------------------------------------------------------

internal val DarkBackground = Color(0xFF0D1117)
internal val DarkSurfaceCard = Color(0xFF161B22)
internal val DarkSurfaceCardElevated = Color(0xFF21262D)
internal val DarkTextPrimary = Color(0xFFF0F6FC)
internal val DarkTextSecondary = Color(0xFF8B949E)
internal val DarkTextMuted = Color(0xFF484F58)
internal val DarkAnswerLetterBg = Color(0xFF0F3460)
internal val DarkCardGradientStart = Color(0xFF1E3A5F)
internal val DarkCardGradientEnd = Color(0xFF2D1B69)

// ---------------------------------------------------------------------------
// Backwards-compatible names exposed to UI code as theme-aware getters.
// They resolve from [LocalAppColors], so toggling theme automatically
// flips the entire app — no screen changes required.
//
// Each getter MUST be used inside a @Composable scope.
// ---------------------------------------------------------------------------

val BackgroundDark: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.background

val BackgroundCard: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.surfaceCard

val BackgroundCardLight: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.surfaceCardElevated

val TextWhite: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.textPrimary

val TextGray: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.textSecondary

val TextMuted: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.textMuted

val CardGradientStart: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.cardGradientStart

val CardGradientEnd: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.cardGradientEnd

val SecondaryDark: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.answerLetterBg

