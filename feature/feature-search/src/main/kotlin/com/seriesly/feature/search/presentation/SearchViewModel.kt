package com.seriesly.feature.search.presentation

import androidx.lifecycle.viewModelScope
import com.seriesly.core.common.base.BaseViewModel
import com.seriesly.core.common.result.Result
import com.seriesly.core.common.ui.toUiError
import com.seriesly.core.domain.model.ContentFilter
import com.seriesly.core.domain.model.ContentItem
import com.seriesly.core.domain.model.RatedItem
import com.seriesly.core.domain.model.Watchlist
import com.seriesly.core.domain.repository.ProgressRepository
import com.seriesly.core.domain.repository.SearchRepository
import com.seriesly.core.domain.repository.WatchlistRepository
import com.seriesly.core.security.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val progressRepository: ProgressRepository,
    private val watchlistRepository: WatchlistRepository,
    private val sessionManager: SessionManager
) : BaseViewModel<SearchUiState, SearchIntent, SearchEvent>(SearchUiState()) {

    private val userId      = sessionManager.getCurrentUserId()
    private val queryInput  = MutableStateFlow("")
    private val filterInput = MutableStateFlow(ContentFilter.ALL)

    init {
        viewModelScope.launch {
            combine(
                queryInput.debounce(400).filter { it.length >= 3 },
                filterInput
            ) { query, filter -> query to filter }
                .flatMapLatest { (query, filter) ->
                    searchRepository.search(query, filter)
                }
                .collect { result ->
                    when (result) {
                        is Result.Loading -> setState { copy(isLoading = true, error = null) }
                        is Result.Success -> setState { copy(results = result.data, isLoading = false, error = null) }
                        is Result.Error   -> setState { copy(isLoading = false, error = result.exception.toUiError()) }
                    }
                }
        }

        viewModelScope.launch {
            combine(
                progressRepository.observeInProgressSeries(userId, 12),
                progressRepository.observeRecentlyWatched(userId, 12),
                progressRepository.observeAllRatings(userId),
                watchlistRepository.observeWatchlists(userId)
            ) { inProgress: List<ContentItem>,
                movies: List<ContentItem>,
                ratings: List<RatedItem>,
                lists: List<Watchlist> ->
                setState {
                    copy(
                        inProgressSeries      = inProgress,
                        recentlyWatchedMovies = movies,
                        recentlyRated         = ratings.take(12),
                        watchlists            = lists
                    )
                }
            }.collect {}
        }
    }

    override fun onIntent(intent: SearchIntent) { when (intent) {
        is SearchIntent.QueryChanged -> {
            setState { copy(query = intent.query) }
            queryInput.value = intent.query
            if (intent.query.length < 3) {
                setState { copy(results = emptyList(), isLoading = false, error = null) }
            } else {
                setState { copy(isLoading = true, error = null) }
            }
        }
        is SearchIntent.FilterSelected -> {
            val hasQuery = uiState.value.query.length >= 3
            setState { copy(filter = intent.filter, results = emptyList(), isLoading = hasQuery) }
            filterInput.value = intent.filter
        }
        is SearchIntent.ItemClicked      -> sendEvent(SearchEvent.NavigateToDetail(intent.tvdbId, intent.contentType))
        is SearchIntent.WatchlistClicked -> sendEvent(SearchEvent.NavigateToWatchlist(intent.watchlistId))
        SearchIntent.ClearQuery     -> {
            setState { copy(query = "", results = emptyList(), error = null, isLoading = false) }
            queryInput.value = ""
        }
        SearchIntent.RetryClicked   -> viewModelScope.launch {
            val q = uiState.value.query
            val f = uiState.value.filter
            if (q.length >= 3) {
                searchRepository.search(q, f).collect { result ->
                    when (result) {
                        is Result.Loading -> setState { copy(isLoading = true, error = null) }
                        is Result.Success -> setState { copy(results = result.data, isLoading = false, error = null) }
                        is Result.Error   -> setState { copy(isLoading = false, error = result.exception.toUiError()) }
                    }
                }
            }
        }
    } }
}
