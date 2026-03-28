package com.seriesly.feature.detail.presentation.series

import com.seriesly.core.common.model.ContentType
import com.seriesly.core.common.ui.UiError
import com.seriesly.core.domain.model.Season
import com.seriesly.core.domain.model.Series
import com.seriesly.core.domain.model.SeriesProgress
import com.seriesly.core.domain.model.Watchlist

data class SeriesDetailUiState(
    val series: Series? = null,
    val isLoading: Boolean = true,
    val error: UiError? = null,
    val progress: SeriesProgress? = null,
    val episodeWatched: Map<Int, Boolean> = emptyMap(),
    val expandedSeasonIds: Set<Int> = emptySet(),
    val userRating: Float? = null,
    val showRatingPrompt: Boolean = false,
    val showWatchlistSheet: Boolean = false,
    val watchlists: List<Watchlist> = emptyList(),
    val inWatchlistIds: Set<Long> = emptySet(),
    val showSeriesCompletedCelebration: Boolean = false
)

sealed interface SeriesDetailIntent {
    object ShowWatchlistSheet : SeriesDetailIntent
    object DismissWatchlistSheet : SeriesDetailIntent
    data class ToggleWatchlist(val watchlistId: Long, val add: Boolean) : SeriesDetailIntent
    object ShowRatingSheet : SeriesDetailIntent
    object DismissRatingSheet : SeriesDetailIntent
    data class SaveRating(val rating: Float, val comment: String?) : SeriesDetailIntent
    data class EpisodeToggled(val episodeId: Int, val watched: Boolean) : SeriesDetailIntent
    data class SeasonMarkWatched(val season: Season) : SeriesDetailIntent
    object MarkAllWatched : SeriesDetailIntent
    data class ToggleSeason(val seasonId: Int) : SeriesDetailIntent
    object DismissSeriesCompletedCelebration : SeriesDetailIntent
}

sealed interface SeriesDetailEvent {
    object NavigateBack : SeriesDetailEvent
}
