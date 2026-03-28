package com.seriesly.feature.search.presentation.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seriesly.core.common.model.ContentType
import com.seriesly.core.domain.model.ContentItem
import com.seriesly.core.domain.model.RatedItem
import com.seriesly.core.domain.model.Watchlist
import com.seriesly.core.ui.component.PosterImage
import kotlinx.coroutines.delay

@Composable
fun DiscoveryRow(
    title: String,
    items: List<ContentItem>,
    onItemClick: (Int, ContentType) -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return
    Column(modifier) {
        Text(
            text     = title,
            style    = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
        )
        LazyRow(
            contentPadding      = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(items, key = { _, item -> "${item.tvdbId}_${item.contentType}" }) { index, item ->
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(item.tvdbId) {
                    delay(index * 50L)
                    visible = true
                }
                val alpha by animateFloatAsState(
                    targetValue   = if (visible) 1f else 0f,
                    animationSpec = tween(250),
                    label         = "disc_alpha_$index"
                )
                Box(Modifier.graphicsLayer { this.alpha = alpha }) {
                    DiscoveryPosterCard(item = item, onClick = { onItemClick(item.tvdbId, item.contentType) })
                }
            }
        }
    }
}

@Composable
fun RatingsRow(
    title: String,
    items: List<RatedItem>,
    onItemClick: (Int, ContentType) -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return
    Column(modifier) {
        Text(
            text     = title,
            style    = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
        )
        LazyRow(
            contentPadding        = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(items, key = { _, item -> "${item.tvdbId}_${item.contentType}" }) { index, item ->
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(item.tvdbId) {
                    delay(index * 50L)
                    visible = true
                }
                val alpha by animateFloatAsState(
                    targetValue   = if (visible) 1f else 0f,
                    animationSpec = tween(250),
                    label         = "rating_alpha_$index"
                )
                Box(Modifier.graphicsLayer { this.alpha = alpha }) {
                    RatingPosterCard(item = item, onClick = { onItemClick(item.tvdbId, item.contentType) })
                }
            }
        }
    }
}

@Composable
fun WatchlistsSection(
    title: String,
    watchlists: List<Watchlist>,
    modifier: Modifier = Modifier
) {
    if (watchlists.isEmpty()) return
    Column(modifier) {
        Text(
            text     = title,
            style    = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp)
        )
        watchlists.forEach { watchlist ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape    = MaterialTheme.shapes.large
            ) {
                Row(
                    modifier          = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(watchlist.name, style = MaterialTheme.typography.bodyLarge)
                        val count = watchlist.movieCount + watchlist.seriesCount
                        Text(
                            text  = "$count ${if (count == 1) "item" else "items"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun DiscoveryPosterCard(item: ContentItem, onClick: () -> Unit) {
    Column(
        modifier            = Modifier
            .width(96.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.Start
    ) {
        PosterImage(
            url                = item.posterUrl,
            contentDescription = item.title,
            modifier           = Modifier.clip(MaterialTheme.shapes.medium),
            width              = 96.dp,
            height             = 144.dp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text     = item.title,
            style    = MaterialTheme.typography.labelSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun RatingPosterCard(item: RatedItem, onClick: () -> Unit) {
    Column(
        modifier            = Modifier
            .width(96.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.Start
    ) {
        PosterImage(
            url                = item.posterUrl,
            contentDescription = item.title,
            modifier           = Modifier.clip(MaterialTheme.shapes.medium),
            width              = 96.dp,
            height             = 144.dp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text  = "\u2605 ${"%.1f".format(item.rating)}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text     = item.title,
            style    = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
