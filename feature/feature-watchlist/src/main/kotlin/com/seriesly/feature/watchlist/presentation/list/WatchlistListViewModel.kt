package com.seriesly.feature.watchlist.presentation.list

import androidx.lifecycle.viewModelScope
import com.seriesly.core.common.base.BaseViewModel
import com.seriesly.core.common.result.Result
import com.seriesly.core.domain.model.Watchlist
import com.seriesly.core.security.session.SessionManager
import com.seriesly.feature.watchlist.domain.usecase.CreateWatchlistUseCase
import com.seriesly.feature.watchlist.domain.usecase.DeleteWatchlistUseCase
import com.seriesly.feature.watchlist.domain.usecase.ObserveWatchlistsUseCase
import com.seriesly.feature.watchlist.domain.usecase.ReorderWatchlistsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistListViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val observeWatchlists: ObserveWatchlistsUseCase,
    private val createWatchlist: CreateWatchlistUseCase,
    private val deleteWatchlist: DeleteWatchlistUseCase,
    private val reorder: ReorderWatchlistsUseCase
) : BaseViewModel<WatchlistListUiState, WatchlistListIntent, WatchlistListEvent>(WatchlistListUiState()) {

    private val userId = sessionManager.getCurrentUserId()

    init {
        viewModelScope.launch {
            observeWatchlists(userId).collect { lists ->
                setState { copy(watchlists = lists, isLoading = false) }
            }
        }
    }

    override fun onIntent(intent: WatchlistListIntent) { when (intent) {
        WatchlistListIntent.CreateClicked          -> sendEvent(WatchlistListEvent.ShowCreateDialog)
        is WatchlistListIntent.CreateConfirmed     -> create(intent.name)
        is WatchlistListIntent.DeleteClicked       -> delete(intent.watchlist)
        is WatchlistListIntent.WatchlistSelected   -> sendEvent(WatchlistListEvent.NavigateToDetail(intent.watchlistId))
        is WatchlistListIntent.ReorderDone         -> viewModelScope.launch { reorder(intent.orderedIds) }
    } }

    private fun create(name: String) = viewModelScope.launch {
        when (val r = createWatchlist(userId, name)) {
            is Result.Success -> sendEvent(WatchlistListEvent.ShowSnackbar("\"$name\" created"))
            is Result.Error   -> sendEvent(WatchlistListEvent.ShowSnackbar(r.exception.message ?: "Error"))
            else              -> {}
        }
    }

    private fun delete(watchlist: Watchlist) = viewModelScope.launch {
        when (val r = deleteWatchlist(watchlist)) {
            is Result.Success -> sendEvent(WatchlistListEvent.ShowSnackbar("\"${watchlist.name}\" deleted"))
            is Result.Error   -> sendEvent(WatchlistListEvent.ShowSnackbar(r.exception.message ?: "Cannot delete"))
            else              -> {}
        }
    }
}
