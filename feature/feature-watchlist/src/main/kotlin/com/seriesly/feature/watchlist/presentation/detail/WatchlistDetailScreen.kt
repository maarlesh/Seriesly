package com.seriesly.feature.watchlist.presentation.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seriesly.core.common.model.ContentType
import com.seriesly.core.domain.model.ContentFilter
import com.seriesly.core.domain.model.ContentItem
import com.seriesly.core.ui.component.ContentTypeBadge
import com.seriesly.core.ui.component.EmptyState
import com.seriesly.core.ui.component.PosterImage

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.watchlist?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        val tabIndex = if (uiState.selectedTab == ContentFilter.MOVIES) 0 else 1
        val currentItems = if (uiState.selectedTab == ContentFilter.MOVIES) uiState.movies else uiState.series

        androidx.compose.foundation.layout.Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = tabIndex) {
                Tab(
                    selected = tabIndex == 0,
                    onClick  = { viewModel.onIntent(WatchlistDetailIntent.TabSelected(ContentFilter.MOVIES)) },
                    text     = { Text("Movies (${uiState.movies.size})") }
                )
                Tab(
                    selected = tabIndex == 1,
                    onClick  = { viewModel.onIntent(WatchlistDetailIntent.TabSelected(ContentFilter.SERIES)) },
                    text     = { Text("Series (${uiState.series.size})") }
                )
            }

            if (currentItems.isEmpty() && !uiState.isLoading) {
                val label = if (uiState.selectedTab == ContentFilter.MOVIES) "movies" else "series"
                EmptyState(
                    icon     = Icons.Outlined.Bookmarks,
                    title    = "No $label added",
                    message  = "Add $label from their detail screens",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn {
                    items(currentItems, key = { "${it.tvdbId}-${it.contentType}" }) { item ->
                        WatchlistItemRow(
                            item     = item,
                            onClick  = { viewModel.onIntent(WatchlistDetailIntent.ItemClicked(item.tvdbId, item.contentType)) },
                            onRemove = { viewModel.onIntent(WatchlistDetailIntent.RemoveItem(item.tvdbId, item.contentType)) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun WatchlistItemRow(
    item: ContentItem,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    ListItem(
        headlineContent = { Text(item.title) },
        supportingContent = {
            item.year?.let { Text("$it") }
        },
        leadingContent = {
            PosterImage(
                url                = item.posterUrl,
                contentDescription = item.title,
                modifier           = Modifier.size(50.dp, 75.dp)
            )
        },
        trailingContent = {
            Row(
                verticalAlignment    = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ContentTypeBadge(item.contentType)
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove from watchlist")
                }
            }
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}
