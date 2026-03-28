package com.seriesly.feature.detail.presentation.movie

import com.seriesly.core.common.model.ContentType
import com.seriesly.core.common.ui.UiError
import com.seriesly.core.domain.model.Movie
import com.seriesly.core.domain.model.Watchlist

data class MovieDetailUiState(
    val movie: Movie? = null,
    val isLoading: Boolean = true,
    val error: UiError? = null,
    val isWatched: Boolean = false,
    val userRating: Float? = null,
    val userComment: String? = null,
    val showWatchlistSheet: Boolean = false,
    val showRatingSheet: Boolean = false,
    val watchlists: List<Watchlist> = emptyList(),
    val inWatchlistIds: Set<Long> = emptySet()
)

sealed interface MovieDetailIntent {
    object ToggleWatched : MovieDetailIntent
    object ShowWatchlistSheet : MovieDetailIntent
    object DismissWatchlistSheet : MovieDetailIntent
    data class ToggleWatchlist(val watchlistId: Long, val add: Boolean) : MovieDetailIntent
    object ShowRatingSheet : MovieDetailIntent
    object DismissRatingSheet : MovieDetailIntent
    data class SaveRating(val rating: Float, val comment: String?) : MovieDetailIntent
}

sealed interface MovieDetailEvent {
    object NavigateBack : MovieDetailEvent
}
