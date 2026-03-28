package com.seriesly.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ── Font family ───────────────────────────────────────────────────────────────
// Using the system's rounded sans-serif stack (Roboto Flex / Noto Sans on Android).
// TODO: For the full Nunito experience, add Nunito TTF files to res/font/ and replace
//       FontFamily.SansSerif with:
//         FontFamily(
//             Font(R.font.nunito_regular,   FontWeight.Normal),
//             Font(R.font.nunito_medium,    FontWeight.Medium),
//             Font(R.font.nunito_semibold,  FontWeight.SemiBold),
//             Font(R.font.nunito_bold,      FontWeight.Bold),
//             Font(R.font.nunito_extrabold, FontWeight.ExtraBold)
//         )
val SerieslyFontFamily = FontFamily.SansSerif

val SerieslyTypography = Typography(
    // ── Display ──────────────────────────────────────────────────────────────
    displayLarge = TextStyle(
        fontFamily   = SerieslyFontFamily,
        fontWeight   = FontWeight.Bold,
        fontSize     = 57.sp,
        lineHeight   = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily   = SerieslyFontFamily,
        fontWeight   = FontWeight.Bold,
        fontSize     = 45.sp,
        lineHeight   = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily   = SerieslyFontFamily,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 36.sp,
        lineHeight   = 44.sp,
        letterSpacing = 0.sp
    ),
    // ── Headline ─────────────────────────────────────────────────────────────
    headlineLarge = TextStyle(
        fontFamily   = SerieslyFontFamily,
        fontWeight   = FontWeight.ExtraBold,
        fontSize     = 32.sp,
        lineHeight   = 40.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontFamily   = SerieslyFontFamily,
        fontWeight   = FontWeight.Bold,
        fontSize     = 28.sp,
        lineHeight   = 36.sp,
        letterSpacing = (-0.25).sp
    ),
    headlineSmall = TextStyle(
        fontFamily   = SerieslyFontFamily,
        fontWeight   = FontWeight.Bold,
        fontSize     = 24.sp,
        lineHeight   = 32.sp,
        letterSpacing = 0.sp
    ),
    // ── Title ─────────────────────────────────────────────────────────────────
    titleLarge = TextStyle(
        fontFamily   = SerieslyFontFamily,
        fontWeight   = FontWeight.Bold,
        fontSize     = 22.sp,
        lineHeight   = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily   = SerieslyFontFamily,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 16.sp,
        lineHeight   = 24.sp,
        letterSpacing = 0.1.sp
    ),
    titleSmall = TextStyle(
        fontFamily   = SerieslyFontFamily,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 14.sp,
        lineHeight   = 20.sp,
        letterSpacing = 0.1.sp
    ),
    // ── Body ──────────────────────────────────────────────────────────────────
    bodyLarge = TextStyle(
        fontFamily   = SerieslyFontFamily,
        fontWeight   = FontWeight.Normal,
        fontSize     = 16.sp,
        lineHeight   = 24.sp,
        letterSpacing = 0.15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily   = SerieslyFontFamily,
        fontWeight   = FontWeight.Normal,
        fontSize     = 14.sp,
        lineHeight   = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodySmall = TextStyle(
        fontFamily   = SerieslyFontFamily,
        fontWeight   = FontWeight.Normal,
        fontSize     = 12.sp,
        lineHeight   = 16.sp,
        letterSpacing = 0.2.sp
    ),
    // ── Label ─────────────────────────────────────────────────────────────────
    labelLarge = TextStyle(
        fontFamily   = SerieslyFontFamily,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 14.sp,
        lineHeight   = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily   = SerieslyFontFamily,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 12.sp,
        lineHeight   = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelSmall = TextStyle(
        fontFamily   = SerieslyFontFamily,
        fontWeight   = FontWeight.Medium,
        fontSize     = 11.sp,
        lineHeight   = 16.sp,
        letterSpacing = 0.4.sp
    )
)
