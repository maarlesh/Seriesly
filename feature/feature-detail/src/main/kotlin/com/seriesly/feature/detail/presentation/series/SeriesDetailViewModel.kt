package com.seriesly.feature.detail.presentation.series

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.seriesly.core.common.base.BaseViewModel
import com.seriesly.core.common.model.ContentType
import com.seriesly.core.common.result.Result
import com.seriesly.core.common.ui.toUiError
import com.seriesly.core.domain.model.SeriesWatchStatus
import com.seriesly.core.domain.repository.ContentDetailRepository
import com.seriesly.core.domain.repository.ProgressRepository
import com.seriesly.core.domain.repository.SeriesProgressRepository
import com.seriesly.core.domain.repository.WatchlistRepository
import com.seriesly.core.security.session.SessionManager
import com.seriesly.feature.detail.domain.usecase.GetSeriesDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeriesDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getSeriesDetail: GetSeriesDetailUseCase,
    private val detailRepository: ContentDetailRepository,
    private val seriesProgressRepository: SeriesProgressRepository,
    private val progressRepository: ProgressRepository,
    private val watchlistRepository: WatchlistRepository,
    private val sessionManager: SessionManager
) : BaseViewModel<SeriesDetailUiState, SeriesDetailIntent, SeriesDetailEvent>(SeriesDetailUiState()) {

    private val tvdbId: Int = savedStateHandle.get<Int>("tvdbId") ?: -1
    private val userId = sessionManager.getCurrentUserId()

    init {
        viewModelScope.launch { loadSeries() }
        viewModelScope.launch { observeProgress() }
        viewModelScope.launch { observeEpisodeWatched() }
        viewModelScope.launch { observeRating() }
        viewModelScope.launch { observeWatchlists() }
    }

    private suspend fun loadSeries() {
        when (val r = getSeriesDetail(tvdbId)) {
            is Result.Error -> setState { copy(isLoading = false, error = r.exception.toUiError()) }
            else            -> {}
        }
        detailRepository.observeSeries(tvdbId).collect { series ->
            if (series != null) setState { copy(series = series, isLoading = false) }
        }
    }

    private suspend fun observeProgress() {
        seriesProgressRepository.observeSeriesProgress(userId, tvdbId).collect { progress ->
            val wasWatched  = uiState.value.progress?.status == SeriesWatchStatus.WATCHED
            val isNowWatched = progress.status == SeriesWatchStatus.WATCHED
            setState { copy(progress = progress) }
            if (isNowWatched && !wasWatched) {
                setState { copy(showSeriesCompletedCelebration = true) }
                if (uiState.value.userRating == null) {
                    delay(800)
                    setState { copy(showRatingPrompt = true) }
                }
            }
        }
    }

    private suspend fun observeEpisodeWatched() {
        seriesProgressRepository.observeEpisodeWatched(userId, tvdbId).collect { watchedMap ->
            setState { copy(episodeWatched = watchedMap) }
        }
    }

    private suspend fun observeRating() {
        progressRepository.observeRating(userId, tvdbId, ContentType.SERIES).collect { rating ->
            setState { copy(userRating = rating?.rating) }
        }
    }

    private suspend fun observeWatchlists() {
        watchlistRepository.observeWatchlists(userId).collect { lists ->
            val inIds = watchlistRepository.getWatchlistsContaining(userId, tvdbId, ContentType.SERIES).toSet()
            setState { copy(watchlists = lists, inWatchlistIds = inIds) }
        }
    }

    override fun onIntent(intent: SeriesDetailIntent) {
        when (intent) {
            SeriesDetailIntent.ShowWatchlistSheet               -> setState { copy(showWatchlistSheet = true) }
            SeriesDetailIntent.DismissWatchlistSheet            -> setState { copy(showWatchlistSheet = false) }
            is SeriesDetailIntent.ToggleWatchlist               -> toggleWatchlist(intent.watchlistId, intent.add)
            SeriesDetailIntent.ShowRatingSheet                  -> setState { copy(showRatingPrompt = true) }
            SeriesDetailIntent.DismissRatingSheet               -> setState { copy(showRatingPrompt = false) }
            is SeriesDetailIntent.SaveRating                    -> saveRating(intent.rating, intent.comment)
            is SeriesDetailIntent.EpisodeToggled                -> markEpisode(intent.episodeId, intent.watched)
            is SeriesDetailIntent.SeasonMarkWatched             -> markSeason(intent.season)
            SeriesDetailIntent.MarkAllWatched                   -> markAllWatched()
            is SeriesDetailIntent.ToggleSeason                  -> toggleSeason(intent.seasonId)
            SeriesDetailIntent.DismissSeriesCompletedCelebration -> setState { copy(showSeriesCompletedCelebration = false) }
        }
    }

    private fun toggleSeason(seasonId: Int) {
        val current = uiState.value.expandedSeasonIds
        setState { copy(expandedSeasonIds = if (seasonId in current) current - seasonId else current + seasonId) }
    }

    private fun markEpisode(episodeId: Int, watched: Boolean) = viewModelScope.launch {
        seriesProgressRepository.markEpisodeWatched(userId, tvdbId, episodeId, watched)
    }

    private fun markSeason(season: com.seriesly.core.domain.model.Season) = viewModelScope.launch {
        val episodeIds = season.episodes.map { it.episodeId }
        seriesProgressRepository.markSeasonEpisodesWatched(userId, tvdbId, episodeIds, watched = true)
    }

    private fun markAllWatched() = viewModelScope.launch {
        seriesProgressRepository.markAllAiredWatched(userId, tvdbId, watched = true)
    }

    private fun toggleWatchlist(watchlistId: Long, add: Boolean) = viewModelScope.launch {
        if (add) {
            val series = uiState.value.series
            val year = series?.firstAired?.take(4)?.toIntOrNull()
            watchlistRepository.addItem(watchlistId, tvdbId, ContentType.SERIES, series?.title ?: "", series?.posterUrl, year)
        } else {
            watchlistRepository.removeItem(watchlistId, tvdbId, ContentType.SERIES)
        }
        val inIds = watchlistRepository.getWatchlistsContaining(userId, tvdbId, ContentType.SERIES).toSet()
        setState { copy(inWatchlistIds = inIds) }
    }

    private fun saveRating(rating: Float, comment: String?) = viewModelScope.launch {
        val series = uiState.value.series ?: return@launch
        progressRepository.upsertRating(userId, tvdbId, ContentType.SERIES, rating, comment, series.title, series.posterUrl)
        setState { copy(showRatingPrompt = false) }
    }
}
