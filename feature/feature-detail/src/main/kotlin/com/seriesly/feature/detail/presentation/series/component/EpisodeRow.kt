package com.seriesly.feature.detail.presentation.series.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.seriesly.core.domain.model.Episode
import com.seriesly.core.ui.theme.Outline
import com.seriesly.core.ui.theme.Secondary
import com.seriesly.core.ui.theme.SurfaceContainerLow

@Composable
fun EpisodeRow(
    episode:   Episode,
    isWatched: Boolean,
    onToggle:  (Boolean) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var internalWatched by remember { mutableStateOf(isWatched) }
    LaunchedEffect(isWatched) { internalWatched = isWatched }

    val checkboxBg by animateColorAsState(
        targetValue   = if (internalWatched) Secondary else Color.Transparent,
        animationSpec = tween(200),
        label         = "checkboxBg"
    )
    val checkboxBorder by animateColorAsState(
        targetValue   = if (internalWatched) Secondary else Outline.copy(alpha = 0.5f),
        animationSpec = tween(200),
        label         = "checkboxBorder"
    )
    val checkGlowAlpha = remember { Animatable(0f) }
    LaunchedEffect(internalWatched) {
        if (internalWatched) {
            checkGlowAlpha.snapTo(1f)
            checkGlowAlpha.animateTo(0f, tween(800))
        } else {
            checkGlowAlpha.snapTo(0f)
        }
    }

    // Row background: watched = green gradient, unwatched = transparent
    val rowBg: Brush = if (internalWatched) {
        Brush.horizontalGradient(
            colors = listOf(Secondary.copy(alpha = 0.05f), Color.Transparent)
        )
    } else {
        Brush.horizontalGradient(colors = listOf(Color.Transparent, Color.Transparent))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBg)
            .clickable {
                val newState = !internalWatched
                internalWatched = newState
                if (newState) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onToggle(newState)
            }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Checkbox
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(checkboxBg)
                .then(
                    // "Ghost border" — felt not seen
                    if (!internalWatched) Modifier.background(
                        Color.Transparent,
                        RoundedCornerShape(8.dp)
                    ) else Modifier
                )
        ) {
            // Outer border via Box stroke simulation
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.linearGradient(listOf(checkboxBorder, checkboxBorder))
                    )
            )
            if (internalWatched) {
                Icon(
                    imageVector        = Icons.Filled.Check,
                    contentDescription = null,
                    tint               = SurfaceContainerLow,
                    modifier           = Modifier.size(16.dp)
                )
            }
        }

        // Text
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "E${episode.episodeNumber}${episode.title?.let { " · $it" } ?: ""}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight     = FontWeight.Bold,
                    textDecoration = if (internalWatched) TextDecoration.LineThrough else TextDecoration.None,
                    color          = if (internalWatched)
                                         MaterialTheme.colorScheme.onSurface.copy(alpha = 0.50f)
                                     else MaterialTheme.colorScheme.onSurface
                )
            )
            episode.airDate?.let { date ->
                Text(
                    text  = date.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (internalWatched) Outline.copy(alpha = 0.6f) else Outline
                )
            }
        }

        Icon(
            imageVector        = Icons.Outlined.Info,
            contentDescription = "Episode info",
            tint               = if (internalWatched) Outline.copy(alpha = 0.4f) else Outline,
            modifier           = Modifier.size(18.dp)
        )
    }
}

