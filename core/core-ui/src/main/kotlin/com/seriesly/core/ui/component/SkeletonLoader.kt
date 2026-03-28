package com.seriesly.core.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seriesly.core.ui.tokens.ContentSize
import com.seriesly.core.ui.tokens.Spacing

@Composable
fun shimmerBrush(): Brush {
    val baseColor = MaterialTheme.colorScheme.surfaceVariant
    val shimmerColors = listOf(
        baseColor.copy(alpha = 0.6f),
        baseColor.copy(alpha = 0.15f),
        baseColor.copy(alpha = 0.6f),
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateX by transition.animateFloat(
        initialValue  = -400f,
        targetValue   = 400f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerX"
    )
    return Brush.linearGradient(
        colors = shimmerColors,
        start  = Offset(translateX, 0f),
        end    = Offset(translateX + 400f, 0f)
    )
}

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    width: Dp? = null,
    height: Dp? = null,
    shape: Shape = MaterialTheme.shapes.extraSmall
) {
    val brush = shimmerBrush()
    val sized = when {
        width != null && height != null -> modifier.size(width, height)
        height != null                  -> modifier.fillMaxWidth().height(height)
        else                            -> modifier.fillMaxWidth()
    }
    Box(sized.clip(shape).background(brush))
}

@Composable
fun SearchResultSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = Spacing.md, vertical = Spacing.smMd),
        horizontalArrangement = Arrangement.spacedBy(Spacing.smMd)
    ) {
        ShimmerBox(width = ContentSize.posterWidth, height = ContentSize.posterHeight, shape = MaterialTheme.shapes.small)
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            ShimmerBox(width = 180.dp, height = 16.dp)
            ShimmerBox(width = 100.dp, height = 12.dp)
            ShimmerBox(width = 60.dp,  height = 12.dp)
        }
    }
}

@Composable
fun SearchSkeletonList(count: Int = 6, modifier: Modifier = Modifier) {
    Column(modifier = modifier) { repeat(count) { SearchResultSkeleton() } }
}

@Composable
fun HeroSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier            = modifier.padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Box(Modifier.fillMaxWidth().height(240.dp).clip(MaterialTheme.shapes.medium).background(shimmerBrush()))
        ShimmerBox(width = 220.dp, height = 24.dp)
        ShimmerBox(width = 130.dp, height = 16.dp)
        ShimmerBox(modifier = Modifier.fillMaxWidth(0.9f), height = 14.dp)
        ShimmerBox(modifier = Modifier.fillMaxWidth(0.75f), height = 14.dp)
    }
}