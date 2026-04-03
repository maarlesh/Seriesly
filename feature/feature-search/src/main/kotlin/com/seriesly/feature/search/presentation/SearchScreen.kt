package com.seriesly.feature.search.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.seriesly.core.ui.theme.Background
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seriesly.core.common.model.ContentType
import com.seriesly.core.ui.component.EmptyState
import com.seriesly.core.ui.component.ErrorState
import com.seriesly.core.ui.component.SearchSkeletonList
import com.seriesly.feature.search.presentation.component.ContentFilterTabs
import com.seriesly.feature.search.presentation.component.DiscoveryRow
import com.seriesly.feature.search.presentation.component.RatingsRow
import com.seriesly.feature.search.presentation.component.SearchBar
import com.seriesly.feature.search.presentation.component.SearchResultCard
import com.seriesly.feature.search.presentation.component.WatchlistsSection
import kotlinx.coroutines.delay

@Composable
fun SearchScreen(
    onNavigateToDetail: (Int, ContentType) -> Unit,
    onNavigateToWatchlist: (Long) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SearchEvent.NavigateToDetail   -> onNavigateToDetail(event.tvdbId, event.contentType)
                is SearchEvent.NavigateToWatchlist -> onNavigateToWatchlist(event.watchlistId)
            }
        }
    }

    Column(Modifier.fillMaxSize().background(Background)) {
        SearchBar(
            query    = uiState.query,
            onChange = { viewModel.onIntent(SearchIntent.QueryChanged(it)) },
            onClear  = { viewModel.onIntent(SearchIntent.ClearQuery) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        ContentFilterTabs(
            selected   = uiState.filter,
            onSelected = { viewModel.onIntent(SearchIntent.FilterSelected(it)) }
        )

        Box(Modifier.weight(1f)) {
            when {
                uiState.isLoading && uiState.results.isEmpty() ->
                    SearchSkeletonList(modifier = Modifier.fillMaxSize())

                uiState.error != null && uiState.results.isEmpty() ->
                    ErrorState(
                        error    = uiState.error!!,
                        onRetry  = { viewModel.onIntent(SearchIntent.RetryClicked) },
                        modifier = Modifier.fillMaxSize()
                    )

                uiState.query.length >= 3 && uiState.results.isEmpty() && !uiState.isLoading ->
                    EmptyState(
                        icon     = Icons.Outlined.SearchOff,
                        title    = "No results",
                        message  = "Nothing found for \"${uiState.query}\"",
                        modifier = Modifier.fillMaxSize()
                    )

                uiState.query.length < 3 -> {
                    val hasContent = uiState.inProgressSeries.isNotEmpty() ||
                                     uiState.recentlyWatchedMovies.isNotEmpty() ||
                                     uiState.recentlyRated.isNotEmpty() ||
                                     uiState.watchlists.isNotEmpty()

                    if (hasContent) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item {
                                DiscoveryRow(
                                    title       = "Continue Watching",
                                    items       = uiState.inProgressSeries,
                                    onItemClick = { tvdbId, type -> viewModel.onIntent(SearchIntent.ItemClicked(tvdbId, type)) }
                                )
                            }
                            item {
                                DiscoveryRow(
                                    title       = "Recently Watched",
                                    items       = uiState.recentlyWatchedMovies,
                                    onItemClick = { tvdbId, type -> viewModel.onIntent(SearchIntent.ItemClicked(tvdbId, type)) }
                                )
                            }
                            item {
                                RatingsRow(
                                    title       = "Your Ratings",
                                    items       = uiState.recentlyRated,
                                    onItemClick = { tvdbId, type -> viewModel.onIntent(SearchIntent.ItemClicked(tvdbId, type)) }
                                )
                            }
                            item {
                                WatchlistsSection(
                                    title            = "Your Watchlists",
                                    watchlists       = uiState.watchlists,
                                    onWatchlistClick = { viewModel.onIntent(SearchIntent.WatchlistClicked(it)) }
                                )
                            }
                            item { Spacer(Modifier.height(16.dp)) }
                        }
                    } else {
                        EmptyState(
                            icon     = Icons.Outlined.Search,
                            title    = "What will you watch next?",
                            message  = "Type at least 3 characters to search for movies and series",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                else -> {
                    val results = uiState.results
                    LazyColumn {
                        itemsIndexed(results, key = { _, item -> item.tvdbId }) { index, item ->
                            var visible by remember(item.tvdbId) { mutableStateOf(false) }
                            LaunchedEffect(item.tvdbId) {
                                delay(index * 40L)
                                visible = true
                            }
                            val alpha by animateFloatAsState(
                                targetValue   = if (visible) 1f else 0f,
                                animationSpec = tween(200),
                                label         = "alpha_${item.tvdbId}"
                            )
                            Box(Modifier.graphicsLayer { this.alpha = alpha }) {
                                SearchResultCard(
                                    item    = item,
                                    onClick = { viewModel.onIntent(SearchIntent.ItemClicked(item.tvdbId, item.contentType)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
