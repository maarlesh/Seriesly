package com.seriesly.core.ui.theme

import androidx.compose.ui.graphics.Color

// ── Backgrounds ───────────────────────────────────────────────────────────────
val Background              = Color(0xFF050A14)
val Surface                 = Color(0xFF0B1326)
val SurfaceDim              = Color(0xFF0B1326)
val SurfaceContainerLowest  = Color(0xFF060E20)
val SurfaceContainerLow     = Color(0xFF131B2E)
val SurfaceContainer        = Color(0xFF171F33)
val SurfaceContainerHigh    = Color(0xFF222A3D)
val SurfaceContainerHighest = Color(0xFF2D3449)
val SurfaceBright           = Color(0xFF31394D)
val SurfaceVariant          = Color(0xFF2D3449)

// ── Primary — "Seriesly Blue" ─────────────────────────────────────────────────
val Primary                 = Color(0xFFADC6FF)  // periwinkle — text/icons on dark
val PrimaryContainer        = Color(0xFF4D8EFF)  // vivid blue — CTA gradient end
val OnPrimary               = Color(0xFF002E6A)
val OnPrimaryContainer      = Color(0xFF00285D)
val PrimaryFixed            = Color(0xFFD8E2FF)
val PrimaryFixedDim         = Color(0xFFADC6FF)
val InversePrimary          = Color(0xFF005AC2)

// ── Secondary — "Seriesly Green" ─────────────────────────────────────────────
val Secondary               = Color(0xFF4EDEA3)  // mint green — series / progress
val SecondaryContainer      = Color(0xFF00A572)
val OnSecondary             = Color(0xFF003824)
val OnSecondaryContainer    = Color(0xFF00311F)
val SecondaryFixed          = Color(0xFF6FFBBE)
val SecondaryFixedDim       = Color(0xFF4EDEA3)

// ── Tertiary — "Seriesly Amber" ───────────────────────────────────────────────
val Tertiary                = Color(0xFFFFB95F)  // warm amber — ratings, highlights
val TertiaryContainer       = Color(0xFFCA8100)
val OnTertiary              = Color(0xFF472A00)
val OnTertiaryContainer     = Color(0xFF3E2400)
val TertiaryFixed           = Color(0xFFFFDDB8)
val TertiaryFixedDim        = Color(0xFFFFB95F)

// ── Surface text ──────────────────────────────────────────────────────────────
val OnBackground            = Color(0xFFDAE2FD)
val OnSurface               = Color(0xFFDAE2FD)
val OnSurfaceVariant        = Color(0xFFC2C6D6)
val InverseSurface          = Color(0xFFDAE2FD)
val InverseOnSurface        = Color(0xFF283044)

// ── Borders ───────────────────────────────────────────────────────────────────
val Outline                 = Color(0xFF8C909F)
val OutlineVariant          = Color(0xFF424754)

// ── Error ─────────────────────────────────────────────────────────────────────
val Error                   = Color(0xFFFFB4AB)
val ErrorContainer          = Color(0xFF93000A)
val OnError                 = Color(0xFF690005)
val OnErrorContainer        = Color(0xFFFFDAD6)

// ── Special / semantic ────────────────────────────────────────────────────────
val SurfaceTint             = Color(0xFFADC6FF)

/** Warm gold kept for legacy particle bursts — prefer Tertiary in new code */
val WatchedGold             = Color(0xFFFFD166)
val WatchedGoldSubtle       = Color(0x26FFD166)

/** Series green glow — 50 % alpha for shadow/BoxShadow equivalents */
val SeriesGreenGlow         = Color(0x804EDEA3)

/** Celebration particle palette */
val ParticleBlue   = Primary
val ParticleGreen  = Secondary
val ParticleAmber  = Tertiary
val ParticlePink   = Color(0xFFFF6B9D)

// ── Soul gradient stops — use with Brush.linearGradient ───────────────────────
val SoulGradientStart = Primary          // #ADC6FF
val SoulGradientEnd   = PrimaryContainer // #4D8EFF
