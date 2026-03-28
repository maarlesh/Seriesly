package com.seriesly.feature.watchlist.presentation.list

import com.seriesly.core.common.ui.UiError
import com.seriesly.core.domain.model.Watchlist

data class WatchlistListUiState(
    val watchlists: List<Watchlist> = emptyList(),
    val isLoading: Boolean = true,
    val error: UiError? = null
)

sealed interface WatchlistListIntent {
    object CreateClicked : WatchlistListIntent
    data class CreateConfirmed(val name: String) : WatchlistListIntent
    data class DeleteClicked(val watchlist: Watchlist) : WatchlistListIntent
    data class WatchlistSelected(val watchlistId: Long) : WatchlistListIntent
    data class ReorderDone(val orderedIds: List<Long>) : WatchlistListIntent
}

sealed interface WatchlistListEvent {
    data class NavigateToDetail(val watchlistId: Long) : WatchlistListEvent
    data class ShowSnackbar(val message: String) : WatchlistListEvent
    object ShowCreateDialog : WatchlistListEvent
}
