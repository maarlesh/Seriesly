package com.seriesly.core.domain.repository

import com.seriesly.core.common.model.ContentType
import com.seriesly.core.common.result.Result

interface SyncRepository {
    suspend fun pushPendingWatchlists()
    suspend fun pushPendingRatings()
    suspend fun pushPendingProgress()
    suspend fun pullAll(): Result<Unit>
    suspend fun pushWatchlistDeletion(watchlistId: Long)
    suspend fun pushWatchlistItemDeletion(itemId: Long)
    suspend fun pushRatingDeletion(tvdbId: Int, type: ContentType)
}
