package com.seriesly.core.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.seriesly.core.ui.theme.ParticleGold
import com.seriesly.core.ui.theme.ParticlePink
import com.seriesly.core.ui.theme.ParticlePurple
import com.seriesly.core.ui.theme.ParticleTeal
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class Particle(
    val angle: Float,
    val speed: Float,
    val color: Color,
    val size: Float
)

@Composable
fun ParticleBurstOverlay(onComplete: () -> Unit) {
    val particleColors = listOf(ParticlePurple, ParticleTeal, ParticleGold, ParticlePink)
    val particles = remember {
        List(24) { i ->
            Particle(
                angle = Math.toRadians(i * 15.0).toFloat(),
                speed = Random.nextFloat() * 600f + 300f,
                color = particleColors[i % particleColors.size],
                size  = Random.nextFloat() * 8f + 4f
            )
        }
    }

    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue    = 1f,
            animationSpec  = tween(durationMillis = 1200, easing = LinearEasing)
        )
        onComplete()
    }

    Canvas(Modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val p  = progress.value
        particles.forEach { particle ->
            val dist  = particle.speed * p
            val x     = cx + cos(particle.angle) * dist
            val y     = cy + sin(particle.angle) * dist
            val alpha = (1f - p).coerceIn(0f, 1f)
            drawCircle(
                color  = particle.color.copy(alpha = alpha),
                radius = particle.size,
                center = Offset(x, y)
            )
        }
    }
}
