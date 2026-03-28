package com.seriesly.feature.search.presentation

import com.seriesly.core.common.model.ContentType
import com.seriesly.core.common.ui.UiError
import com.seriesly.core.domain.model.ContentFilter
import com.seriesly.core.domain.model.ContentItem
import com.seriesly.core.domain.model.RatedItem
import com.seriesly.core.domain.model.Watchlist

data class SearchUiState(
    val query: String = "",
    val results: List<ContentItem> = emptyList(),
    val filter: ContentFilter = ContentFilter.ALL,
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val inProgressSeries: List<ContentItem> = emptyList(),
    val recentlyWatchedMovies: List<ContentItem> = emptyList(),
    val recentlyRated: List<RatedItem> = emptyList(),
    val watchlists: List<Watchlist> = emptyList()
)

sealed interface SearchIntent {
    data class QueryChanged(val query: String) : SearchIntent
    data class FilterSelected(val filter: ContentFilter) : SearchIntent
    data class ItemClicked(val tvdbId: Int, val contentType: ContentType) : SearchIntent
    object ClearQuery : SearchIntent
    object RetryClicked : SearchIntent
}

sealed interface SearchEvent {
    data class NavigateToDetail(val tvdbId: Int, val contentType: ContentType) : SearchEvent
}
