package com.seriesly.feature.watchlist.presentation.detail

import com.seriesly.core.common.model.ContentType
import com.seriesly.core.domain.model.ContentFilter
import com.seriesly.core.domain.model.ContentItem
import com.seriesly.core.domain.model.Watchlist

data class WatchlistDetailUiState(
    val watchlist: Watchlist? = null,
    val movies: List<ContentItem> = emptyList(),
    val series: List<ContentItem> = emptyList(),
    val selectedTab: ContentFilter = ContentFilter.MOVIES,
    val isLoading: Boolean = true
)

sealed interface WatchlistDetailIntent {
    data class TabSelected(val filter: ContentFilter) : WatchlistDetailIntent
    data class RemoveItem(val tvdbId: Int, val type: ContentType) : WatchlistDetailIntent
    data class ItemClicked(val tvdbId: Int, val type: ContentType) : WatchlistDetailIntent
    data class MarkMovieWatched(val tvdbId: Int) : WatchlistDetailIntent
}

sealed interface WatchlistDetailEvent {
    data class NavigateToDetail(val tvdbId: Int, val contentType: ContentType) : WatchlistDetailEvent
}
