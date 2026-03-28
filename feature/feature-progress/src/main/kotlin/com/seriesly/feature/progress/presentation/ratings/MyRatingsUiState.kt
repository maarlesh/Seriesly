package com.seriesly.feature.progress.presentation.ratings

import com.seriesly.core.common.model.ContentType
import com.seriesly.core.domain.model.RatedItem

data class MyRatingsUiState(
    val ratings: List<RatedItem> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface MyRatingsIntent {
    data class ItemClicked(val tvdbId: Int, val contentType: ContentType) : MyRatingsIntent
}

sealed interface MyRatingsEvent {
    data class NavigateToDetail(val tvdbId: Int, val contentType: ContentType) : MyRatingsEvent
}
