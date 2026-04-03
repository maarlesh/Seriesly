package com.seriesly.core.ui.tokens

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.seriesly.core.ui.theme.Background
import com.seriesly.core.ui.theme.Secondary
import com.seriesly.core.ui.theme.SecondaryContainer
import com.seriesly.core.ui.theme.SoulGradientEnd
import com.seriesly.core.ui.theme.SoulGradientStart

object Brushes {
    /**
     * Primary "Soul" gradient (135°) — use for CTAs, icon containers, FABs.
     * Periwinkle → vivid blue.
     */
    val SoulGradient: Brush
        get() = Brush.linearGradient(
            colors = listOf(SoulGradientStart, SoulGradientEnd),
            start  = Offset.Zero,
            end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )

    /**
     * Charging gradient for active series progress bars.
     * Sweeping mint → deep green → mint, animated via offset.
     */
    val ChargingGradient: Brush
        get() = Brush.horizontalGradient(
            colors = listOf(Secondary, SecondaryContainer, Secondary)
        )

    /**
     * Hero image bottom scrim — fades cinematic backdrop into the app background.
     */
    val HeroScrim: Brush
        get() = Brush.verticalGradient(
            colorStops = arrayOf(
                0.0f to Color.Transparent,
                0.4f to Background.copy(alpha = 0.4f),
                1.0f to Background
            )
        )

    /**
     * Subtle top-to-bottom glass sheen for collectible cards.
     */
    val GlassSheen: Brush
        get() = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.03f),
                Color.Transparent
            )
        )
}
