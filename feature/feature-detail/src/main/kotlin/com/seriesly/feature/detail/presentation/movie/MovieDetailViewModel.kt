package com.seriesly.feature.detail.presentation.movie

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.seriesly.core.common.base.BaseViewModel
import com.seriesly.core.common.model.ContentType
import com.seriesly.core.common.result.Result
import com.seriesly.core.common.ui.toUiError
import com.seriesly.core.domain.repository.ContentDetailRepository
import com.seriesly.core.domain.repository.ProgressRepository
import com.seriesly.core.domain.repository.WatchlistRepository
import com.seriesly.core.security.session.SessionManager
import com.seriesly.feature.detail.domain.usecase.GetMovieDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieDetail: GetMovieDetailUseCase,
    private val detailRepository: ContentDetailRepository,
    private val progressRepository: ProgressRepository,
    private val watchlistRepository: WatchlistRepository,
    private val sessionManager: SessionManager
) : BaseViewModel<MovieDetailUiState, MovieDetailIntent, MovieDetailEvent>(MovieDetailUiState()) {

    private val tvdbId: Int = savedStateHandle.get<Int>("tvdbId") ?: -1
    private val userId = sessionManager.getCurrentUserId()

    init {
        viewModelScope.launch { loadMovie() }
        viewModelScope.launch { observeWatched() }
        viewModelScope.launch { observeRating() }
        viewModelScope.launch { observeWatchlists() }
    }

    private suspend fun loadMovie() {
        when (val r = getMovieDetail(tvdbId)) {
            is Result.Error -> setState { copy(isLoading = false, error = r.exception.toUiError()) }
            else            -> { /* data arrives via observeMovie below */ }
        }
        detailRepository.observeMovie(tvdbId).collect { movie ->
            if (movie != null) setState { copy(movie = movie, isLoading = false) }
        }
    }

    private suspend fun observeWatched() {
        progressRepository.observeMovieWatched(userId, tvdbId).collect { watched ->
            setState { copy(isWatched = watched) }
        }
    }

    private suspend fun observeRating() {
        progressRepository.observeRating(userId, tvdbId, ContentType.MOVIE).collect { rating ->
            setState { copy(userRating = rating?.rating, userComment = rating?.comment) }
        }
    }

    private suspend fun observeWatchlists() {
        watchlistRepository.observeWatchlists(userId).collect { lists ->
            val inIds = watchlistRepository.getWatchlistsContaining(userId, tvdbId, ContentType.MOVIE).toSet()
            setState { copy(watchlists = lists, inWatchlistIds = inIds) }
        }
    }

    override fun onIntent(intent: MovieDetailIntent) {
        when (intent) {
            MovieDetailIntent.ToggleWatched         -> toggleWatched()
            MovieDetailIntent.ShowWatchlistSheet     -> setState { copy(showWatchlistSheet = true) }
            MovieDetailIntent.DismissWatchlistSheet  -> setState { copy(showWatchlistSheet = false) }
            is MovieDetailIntent.ToggleWatchlist     -> toggleWatchlist(intent.watchlistId, intent.add)
            MovieDetailIntent.ShowRatingSheet        -> setState { copy(showRatingSheet = true) }
            MovieDetailIntent.DismissRatingSheet     -> setState { copy(showRatingSheet = false) }
            is MovieDetailIntent.SaveRating          -> saveRating(intent.rating, intent.comment)
        }
    }

    private fun toggleWatched() = viewModelScope.launch {
        progressRepository.markMovieWatched(userId, tvdbId, !uiState.value.isWatched)
    }

    private fun toggleWatchlist(watchlistId: Long, add: Boolean) = viewModelScope.launch {
        if (add) watchlistRepository.addItem(watchlistId, tvdbId, ContentType.MOVIE)
        else     watchlistRepository.removeItem(watchlistId, tvdbId, ContentType.MOVIE)
        val inIds = watchlistRepository.getWatchlistsContaining(userId, tvdbId, ContentType.MOVIE).toSet()
        setState { copy(inWatchlistIds = inIds) }
    }

    private fun saveRating(rating: Float, comment: String?) = viewModelScope.launch {
        progressRepository.upsertRating(userId, tvdbId, ContentType.MOVIE, rating, comment)
        setState { copy(showRatingSheet = false) }
    }
}
