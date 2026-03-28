package com.seriesly.feature.detail.presentation.series.component

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.seriesly.core.domain.model.Episode
import com.seriesly.core.ui.theme.WatchedGold

@Composable
fun EpisodeRow(
    episode: Episode,
    isWatched: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var internalWatched by remember { mutableStateOf(isWatched) }

    LaunchedEffect(isWatched) { internalWatched = isWatched }

    val iconTint by animateColorAsState(
        targetValue   = if (internalWatched) WatchedGold else MaterialTheme.colorScheme.outline,
        animationSpec = tween(200),
        label         = "epTint"
    )
    val bgColor by animateColorAsState(
        targetValue   = if (internalWatched)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
                        else Color.Transparent,
        animationSpec = tween(300),
        label         = "epBg"
    )

    var ringAnimKey by remember { mutableIntStateOf(0) }
    val ringProgress = remember { Animatable(0f) }
    LaunchedEffect(ringAnimKey) {
        if (ringAnimKey > 0) {
            ringProgress.snapTo(0f)
            ringProgress.animateTo(1f, tween(450))
        }
    }

    ListItem(
        modifier = Modifier
            .padding(start = 16.dp)
            .background(bgColor)
            .clickable {
                val newState = !internalWatched
                internalWatched = newState
                if (newState) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    ringAnimKey++
                }
                onToggle(newState)
            },
        headlineContent = {
            Text(
                text  = "E${episode.episodeNumber}${episode.title?.let { " — $it" } ?: ""}",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        supportingContent = episode.airDate?.let { { Text(it, style = MaterialTheme.typography.labelSmall) } },
        trailingContent = {
            Box(
                contentAlignment = Alignment.Center,
                modifier         = Modifier.size(40.dp)
            ) {
                if (ringProgress.value > 0f) {
                    Canvas(Modifier.size(40.dp)) {
                        val radius = (size.minDimension / 2) * ringProgress.value * 1.5f
                        val alpha  = (1f - ringProgress.value).coerceIn(0f, 1f)
                        drawCircle(
                            color  = WatchedGold,
                            radius = radius,
                            style  = Stroke(width = 2.dp.toPx()),
                            alpha  = alpha,
                            center = Offset(size.width / 2f, size.height / 2f)
                        )
                    }
                }
                Crossfade(targetState = internalWatched, label = "epIcon") { watched ->
                    Icon(
                        imageVector        = if (watched) Icons.Filled.CheckCircle
                                             else Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = if (watched) "Mark unwatched" else "Mark watched",
                        tint               = iconTint,
                        modifier           = Modifier.size(24.dp)
                    )
                }
            }
        }
    )
}
