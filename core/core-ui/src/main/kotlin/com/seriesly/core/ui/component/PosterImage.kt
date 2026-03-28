package com.seriesly.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.seriesly.core.ui.tokens.ContentSize

@Composable
fun PosterImage(
    url: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    width: Dp = ContentSize.posterWidth,
    height: Dp = ContentSize.posterHeight,
    withShadow: Boolean = false
) {
    val shadowMod = if (withShadow)
        modifier.shadow(elevation = 8.dp, shape = MaterialTheme.shapes.small)
    else
        modifier

    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(400)
            .build(),
        contentDescription = contentDescription,
        contentScale       = ContentScale.Crop,
        modifier           = shadowMod
            .size(width, height)
            .clip(MaterialTheme.shapes.small),
        loading = {
            ShimmerBox(width = width, height = height, shape = MaterialTheme.shapes.small)
        },
        error = { PosterPlaceholder(width, height) }
    )
}

@Composable
private fun PosterPlaceholder(width: Dp = 60.dp, height: Dp = 90.dp) {
    Box(
        modifier         = Modifier.size(width, height).background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector        = Icons.Outlined.Movie,
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}