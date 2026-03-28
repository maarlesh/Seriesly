package com.seriesly.core.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seriesly.core.ui.tokens.TouchTarget

/**
 * Half-star precision rating input with spring-animated star fills.
 * Tapping the left half of a star → half star. Right half → full star.
 *
 * @param currentRating Value in range [0, starCount] with 0.5 increments.
 * @param onRatingChanged Called with the new rating when a star is tapped.
 */
@Composable
fun StarRatingInput(
    currentRating: Float,
    onRatingChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    starCount: Int = 5,
    starSize: Dp = TouchTarget.min
) {
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = modifier.semantics {
            contentDescription = "Rating: $currentRating out of $starCount stars"
        },
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(starCount) { index ->
            val fullThreshold = (index + 1).toFloat()
            val halfThreshold = index + 0.5f
            val isFilled      = currentRating >= halfThreshold

            val scale by animateFloatAsState(
                targetValue   = if (isFilled) 1.2f else 1f,
                animationSpec = spring(dampingRatio = 0.4f, stiffness = 500f),
                label         = "starScale$index"
            )
            val tint by animateColorAsState(
                targetValue   = if (isFilled) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline,
                animationSpec = tween(150),
                label         = "starTint$index"
            )

            Box(
                modifier = Modifier
                    .size(starSize)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val newRating = if (offset.x < size.width / 2f) halfThreshold else fullThreshold
                            if (newRating == fullThreshold && index == starCount - 1) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            onRatingChanged(newRating)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                when {
                    currentRating >= fullThreshold -> Icon(
                        imageVector        = Icons.Filled.Star,
                        contentDescription = null,
                        tint               = tint,
                        modifier           = Modifier.scale(scale)
                    )
                    currentRating >= halfThreshold -> Icon(
                        imageVector        = Icons.AutoMirrored.Filled.StarHalf,
                        contentDescription = null,
                        tint               = tint,
                        modifier           = Modifier.scale(scale)
                    )
                    else -> Icon(
                        imageVector        = Icons.Outlined.StarOutline,
                        contentDescription = null,
                        tint               = tint,
                        modifier           = Modifier.scale(scale)
                    )
                }
            }
        }
    }
}

/** Read-only star display (no tap handling). */
@Composable
fun StarRatingDisplay(
    rating: Float,
    modifier: Modifier = Modifier,
    starCount: Int = 5,
    starSize: Dp = 20.dp
) {
    Row(
        modifier = modifier.semantics {
            contentDescription = "Rated $rating out of $starCount stars"
        },
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(starCount) { index ->
            val fullThreshold = (index + 1).toFloat()
            val halfThreshold = index + 0.5f
            val iconSize = Modifier.size(starSize)
            when {
                rating >= fullThreshold -> Icon(Icons.Filled.Star, null, iconSize, tint = MaterialTheme.colorScheme.primary)
                rating >= halfThreshold -> Icon(Icons.AutoMirrored.Filled.StarHalf, null, iconSize, tint = MaterialTheme.colorScheme.primary)
                else -> Icon(Icons.Outlined.StarOutline, null, iconSize, tint = MaterialTheme.colorScheme.outline)
            }
        }
    }
}
