package com.seriesly.feature.watchlist.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.seriesly.core.common.base.BaseViewModel
import com.seriesly.core.domain.model.ContentFilter
import com.seriesly.core.domain.repository.ProgressRepository
import com.seriesly.core.domain.repository.WatchlistRepository
import com.seriesly.core.security.session.SessionManager
import com.seriesly.feature.watchlist.domain.usecase.RemoveFromWatchlistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sessionManager: SessionManager,
    private val watchlistRepository: WatchlistRepository,
    private val progressRepository: ProgressRepository,
    private val removeFromWatchlist: RemoveFromWatchlistUseCase
) : BaseViewModel<WatchlistDetailUiState, WatchlistDetailIntent, WatchlistDetailEvent>(WatchlistDetailUiState()) {

    private val watchlistId: Long = savedStateHandle.get<Long>("watchlistId") ?: -1L
    private val userId = sessionManager.getCurrentUserId()

    init {
        viewModelScope.launch {
            watchlistRepository.observeWatchlists(userId).collect { lists ->
                val wl = lists.find { it.watchlistId == watchlistId }
                setState { copy(watchlist = wl) }
            }
        }
        viewModelScope.launch {
            combine(
                watchlistRepository.observeItemsAsContent(watchlistId, ContentFilter.MOVIES),
                progressRepository.observeWatchedMovieTvdbIds(userId)
            ) { items, watchedIds ->
                items.map { it.copy(isWatched = it.tvdbId in watchedIds) }
            }.collect { items ->
                setState { copy(movies = items, isLoading = false) }
            }
        }
        viewModelScope.launch {
            watchlistRepository.observeItemsAsContent(watchlistId, ContentFilter.SERIES).collect { items ->
                setState { copy(series = items, isLoading = false) }
            }
        }
    }

    override fun onIntent(intent: WatchlistDetailIntent) { when (intent) {
        is WatchlistDetailIntent.TabSelected     -> setState { copy(selectedTab = intent.filter) }
        is WatchlistDetailIntent.RemoveItem      -> viewModelScope.launch {
            removeFromWatchlist(watchlistId, intent.tvdbId, intent.type)
        }
        is WatchlistDetailIntent.ItemClicked     -> sendEvent(WatchlistDetailEvent.NavigateToDetail(intent.tvdbId, intent.type))
        is WatchlistDetailIntent.MarkMovieWatched -> viewModelScope.launch {
            progressRepository.markMovieWatched(userId, intent.tvdbId, watched = true)
        }
    } }
}
