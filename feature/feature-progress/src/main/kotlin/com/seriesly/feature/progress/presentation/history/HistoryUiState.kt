package com.seriesly.feature.progress.presentation.history

import com.seriesly.core.common.model.ContentType
import com.seriesly.core.domain.model.WatchHistoryEntry

data class HistoryUiState(
    val entries: List<WatchHistoryEntry> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface HistoryIntent {
    data class ItemClicked(val tvdbId: Int, val contentType: ContentType) : HistoryIntent
}

sealed interface HistoryEvent {
    data class NavigateToDetail(val tvdbId: Int, val contentType: ContentType) : HistoryEvent
}
