package com.seriesly.feature.watchlist.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.seriesly.core.common.model.ContentType
import com.seriesly.core.domain.model.ContentFilter
import com.seriesly.core.domain.model.ContentItem
import com.seriesly.core.ui.component.EmptyState
import com.seriesly.core.ui.theme.*
import com.seriesly.core.ui.tokens.Brushes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistDetailScreen(
    onNavigateToContent: (Int, ContentType) -> Unit,
    onBack: () -> Unit,
    viewModel: WatchlistDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is WatchlistDetailEvent.NavigateToDetail ->
                    onNavigateToContent(event.tvdbId, event.contentType)
            }
        }
    }

    val tabIndex   = if (uiState.selectedTab == ContentFilter.MOVIES) 0 else 1
    val currentItems = if (uiState.selectedTab == ContentFilter.MOVIES) uiState.movies else uiState.series
    val isMoviesTab  = uiState.selectedTab == ContentFilter.MOVIES

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Glass top app bar area
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Background.copy(alpha = 0.85f))
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint               = Primary
                        )
                    }
                    Text(
                        text     = uiState.watchlist?.name?.uppercase() ?: "WATCHLIST",
                        style    = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontStyle  = FontStyle.Italic,
                            letterSpacing = 2.sp
                        ),
                        color    = Primary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Editorial header
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector        = Icons.Filled.Star,
                            contentDescription = null,
                            tint               = Secondary,
                            modifier           = Modifier.size(14.dp)
                        )
                        Text(
                            text  = "THE VAULT",
                            style = MaterialTheme.typography.labelSmall,
                            color = Outline
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text  = uiState.watchlist?.name ?: "To Watch Later",
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = OnSurface
                    )
                    Text(
                        text     = "Your premium collection of future cinematic journeys.",
                        style    = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                        color    = OnSurfaceVariant.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Segmented control tabs
            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceContainerLow)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    listOf(
                        0 to "Movies (${uiState.movies.size})",
                        1 to "Series (${uiState.series.size})"
                    ).forEach { (idx, label) ->
                        val isActive = tabIndex == idx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isActive) SurfaceContainerHigh else Color.Transparent
                                )
                                .clickable {
                                    viewModel.onIntent(
                                        WatchlistDetailIntent.TabSelected(
                                            if (idx == 0) ContentFilter.MOVIES else ContentFilter.SERIES
                                        )
                                    )
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text  = label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.SemiBold
                                ),
                                color = if (isActive) OnSurface else Outline
                            )
                        }
                    }
                }
            }

            if (currentItems.isEmpty() && !uiState.isLoading) {
                item {
                    EmptyState(
                        icon    = Icons.Outlined.Bookmarks,
                        title   = "No ${if (isMoviesTab) "movies" else "series"} yet",
                        message = "Add ${if (isMoviesTab) "movies" else "series"} from their detail screens",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp)
                    )
                }
            } else {
                items(currentItems, key = { "${it.tvdbId}-${it.contentType}" }) { item ->
                    CollectibleCard(
                        item     = item,
                        onClick  = { viewModel.onIntent(WatchlistDetailIntent.ItemClicked(item.tvdbId, item.contentType)) },
                        onRemove = { viewModel.onIntent(WatchlistDetailIntent.RemoveItem(item.tvdbId, item.contentType)) }
                    )
                }
            }
        }
    }
}

// ── Collectible Card ─────────────────────────────────────────────────────────

@Composable
private fun CollectibleCard(
    item:     ContentItem,
    onClick:  () -> Unit,
    onRemove: () -> Unit
) {
    val isMovie   = item.contentType == ContentType.MOVIE
    val accentBrush = if (isMovie) {
        Brush.linearGradient(listOf(Primary.copy(alpha = 0.05f), Color.Transparent))
    } else {
        Brush.linearGradient(listOf(Secondary.copy(alpha = 0.05f), Color.Transparent))
    }

    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainer)
            .background(accentBrush)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Poster
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceContainerHighest)
                ) {
                    AsyncImage(
                        model              = item.posterUrl,
                        contentDescription = item.title,
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier.fillMaxSize()
                    )
                    // Rating badge — tvdbRating or userRating
                    val displayRating = item.userRating ?: item.tvdbRating
                    displayRating?.let { rating ->
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Background.copy(alpha = 0.80f))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint        = Tertiary,
                                modifier    = Modifier.size(12.dp)
                            )
                            Text(
                                text  = "%.1f".format(rating),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                color = OnSurface
                            )
                        }
                    }
                }

                // Metadata
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Type badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (isMovie) Color.White.copy(alpha = 0.05f)
                                else Secondary.copy(alpha = 0.10f)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text  = if (isMovie) "MOVIE" else "ACTIVE SERIES",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                            color = if (isMovie) Outline else Secondary
                        )
                    }

                    Text(
                        text  = item.title,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                        color = OnSurface
                    )

                    item.year?.let { year ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text(
                                text  = year.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = Outline
                            )
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(if (isMovie) Primary.copy(alpha = 0.4f) else Secondary.copy(alpha = 0.4f))
                            )
                            Text(
                                text  = if (isMovie) "MOVIE" else "SERIES",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isMovie) Primary else Secondary
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Action buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Secondary)
                                .clickable(onClick = onClick)
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector        = if (isMovie) Icons.Filled.VerifiedUser else Icons.Filled.PlayArrow,
                                    contentDescription = null,
                                    tint               = OnSecondaryContainer,
                                    modifier           = Modifier.size(16.dp)
                                )
                                Text(
                                    text  = if (isMovie) "MARK WATCHED" else "RESUME",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                    color = OnSecondaryContainer
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceContainerHigh)
                                .clickable(onClick = onRemove),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector        = Icons.Filled.Delete,
                                contentDescription = "Remove",
                                tint               = Error,
                                modifier           = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
