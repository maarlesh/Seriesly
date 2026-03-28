package com.seriesly.core.ui.tokens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

object Motion {
    // ── Springs ─────────────────────────────────────────────────────────────
    /** High-energy bounce — for icon pops, FABs, achievement moments */
    val SpringBouncy  = spring<Float>(dampingRatio = 0.45f, stiffness = 420f)
    /** Snappy with slight overshoot — general UI responses */
    val SpringSnappy  = spring<Float>(dampingRatio = 0.6f,  stiffness = 380f)
    /** Gentle settle — progress bars, colours, smooth reveals */
    val SpringGentle  = spring<Float>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 200f)

    // ── Tweens ───────────────────────────────────────────────────────────────
    val TweenFast     = tween<Float>(150,  easing = FastOutSlowInEasing)
    val TweenMedium   = tween<Float>(300,  easing = FastOutSlowInEasing)
    val TweenSlow     = tween<Float>(500,  easing = FastOutSlowInEasing)
    val TweenLinear   = tween<Float>(1000, easing = LinearEasing)

    // ── Raw durations (ms) ────────────────────────────────────────────────
    const val Fast         = 150
    const val Medium       = 300
    const val Slow         = 500
    const val Celebration  = 1200
}
