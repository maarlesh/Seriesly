package com.seriesly.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.seriesly.core.ui.R

private val fontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage   = "com.google.android.gms",
    certificates      = R.array.com_google_android_gms_fonts_certs
)

// Display & Headlines — "Our Voice" (editorial authority)
private val plusJakartaSans = GoogleFont("Plus Jakarta Sans")
val HeadlineFontFamily: FontFamily = FontFamily(
    Font(googleFont = plusJakartaSans, fontProvider = fontProvider, weight = FontWeight.Normal),
    Font(googleFont = plusJakartaSans, fontProvider = fontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = plusJakartaSans, fontProvider = fontProvider, weight = FontWeight.Bold),
    Font(googleFont = plusJakartaSans, fontProvider = fontProvider, weight = FontWeight.ExtraBold),
    Font(googleFont = plusJakartaSans, fontProvider = fontProvider, weight = FontWeight.Black),
)

// Body & Labels — "Our Information" (legible and functional)
private val interFont = GoogleFont("Inter")
val BodyFontFamily: FontFamily = FontFamily(
    Font(googleFont = interFont, fontProvider = fontProvider, weight = FontWeight.Normal),
    Font(googleFont = interFont, fontProvider = fontProvider, weight = FontWeight.Medium),
    Font(googleFont = interFont, fontProvider = fontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = interFont, fontProvider = fontProvider, weight = FontWeight.Bold),
)

val SerieslyTypography = Typography(
    // ── Display — movie/series hero titles ────────────────────────────────────
    displayLarge = TextStyle(
        fontFamily    = HeadlineFontFamily,
        fontWeight    = FontWeight.Black,
        fontSize      = 56.sp,
        lineHeight    = 60.sp,
        letterSpacing = (-1.12).sp   // -2 % tighten for premium feel
    ),
    displayMedium = TextStyle(
        fontFamily    = HeadlineFontFamily,
        fontWeight    = FontWeight.ExtraBold,
        fontSize      = 45.sp,
        lineHeight    = 50.sp,
        letterSpacing = (-0.9).sp
    ),
    displaySmall = TextStyle(
        fontFamily    = HeadlineFontFamily,
        fontWeight    = FontWeight.ExtraBold,
        fontSize      = 36.sp,
        lineHeight    = 42.sp,
        letterSpacing = (-0.72).sp
    ),
    // ── Headline — section titles, watchlist editorial header ─────────────────
    headlineLarge = TextStyle(
        fontFamily    = HeadlineFontFamily,
        fontWeight    = FontWeight.ExtraBold,
        fontSize      = 32.sp,
        lineHeight    = 38.sp,
        letterSpacing = (-0.64).sp
    ),
    headlineMedium = TextStyle(
        fontFamily    = HeadlineFontFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 28.sp,
        lineHeight    = 34.sp,
        letterSpacing = (-0.56).sp
    ),
    headlineSmall = TextStyle(
        fontFamily    = HeadlineFontFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 24.sp,
        lineHeight    = 30.sp,
        letterSpacing = (-0.48).sp
    ),
    // ── Title — card titles, sub-headings ─────────────────────────────────────
    titleLarge = TextStyle(
        fontFamily    = HeadlineFontFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 22.sp,
        lineHeight    = 28.sp,
        letterSpacing = (-0.22).sp
    ),
    titleMedium = TextStyle(
        fontFamily    = HeadlineFontFamily,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 16.sp,
        lineHeight    = 22.sp,
        letterSpacing = (-0.16).sp
    ),
    titleSmall = TextStyle(
        fontFamily    = HeadlineFontFamily,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 14.sp,
        lineHeight    = 20.sp,
        letterSpacing = 0.sp
    ),
    // ── Body — descriptions, metadata ─────────────────────────────────────────
    bodyLarge = TextStyle(
        fontFamily    = BodyFontFamily,
        fontWeight    = FontWeight.Normal,
        fontSize      = 16.sp,
        lineHeight    = 24.sp,
        letterSpacing = 0.15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily    = BodyFontFamily,
        fontWeight    = FontWeight.Normal,
        fontSize      = 14.sp,
        lineHeight    = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodySmall = TextStyle(
        fontFamily    = BodyFontFamily,
        fontWeight    = FontWeight.Normal,
        fontSize      = 12.sp,
        lineHeight    = 16.sp,
        letterSpacing = 0.2.sp
    ),
    // ── Label — timestamps / micro-copy (always UPPERCASE at use site) ─────────
    labelLarge = TextStyle(
        fontFamily    = BodyFontFamily,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 14.sp,
        lineHeight    = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily    = BodyFontFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 12.sp,
        lineHeight    = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily    = BodyFontFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 11.sp,
        lineHeight    = 16.sp,
        letterSpacing = 0.8.sp   // premium "technical" micro-copy feel
    )
)
