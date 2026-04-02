package com.seriesly.core.ui.tokens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

object Motion {
    // ── Springs — element entry, state changes, scale feedback ────────────────
    val SpringBouncy = spring<Float>(dampingRatio = 0.45f, stiffness = 420f)
    val SpringSnappy = spring<Float>(dampingRatio = 0.6f,  stiffness = 380f)
    val SpringGentle = spring<Float>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 200f)

    // ── Tweens — fades, colour transitions ────────────────────────────────────
    val TweenFast   = tween<Float>(150,  easing = FastOutSlowInEasing)
    val TweenMedium = tween<Float>(300,  easing = FastOutSlowInEasing)
    val TweenSlow   = tween<Float>(500,  easing = FastOutSlowInEasing)
    val TweenLinear = tween<Float>(1000, easing = LinearEasing)

    // ── Raw durations (ms) ────────────────────────────────────────────────────
    const val Fast        = 150
    const val Medium      = 300
    const val Slow        = 500
    const val Celebration = 1200

    // ── Infinite — for ambient / looping effects ──────────────────────────────
    /**
     * Charging sweep for active series progress bars.
     * Animate an X offset from 0f → 1f on [LinearEasing] over 2 s.
     */
    val ChargingInfinite: InfiniteRepeatableSpec<Float> = infiniteRepeatable(
        animation  = tween(durationMillis = 2000, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
    )

    /**
     * Subtle float for empty / reward states: translateY 0 ↔ -4 dp over 3 s.
     */
    val FloatInfinite: InfiniteRepeatableSpec<Float> = infiniteRepeatable(
        animation  = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
    )
}
