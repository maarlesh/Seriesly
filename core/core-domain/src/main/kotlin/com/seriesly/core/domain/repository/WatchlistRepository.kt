package com.seriesly.core.domain.repository

import com.seriesly.core.common.model.ContentType
import com.seriesly.core.common.result.Result
import com.seriesly.core.domain.model.ContentFilter
import com.seriesly.core.domain.model.Watchlist
import com.seriesly.core.domain.model.ContentItem
import com.seriesly.core.domain.model.WatchlistItem
import kotlinx.coroutines.flow.Flow

interface WatchlistRepository {
    fun observeWatchlists(userId: Long): Flow<List<Watchlist>>
    fun observeItems(watchlistId: Long, type: ContentFilter): Flow<List<WatchlistItem>>
    fun observeItemsAsContent(watchlistId: Long, filter: ContentFilter): Flow<List<ContentItem>>
    suspend fun create(userId: Long, name: String): Result<Long>
    suspend fun rename(watchlistId: Long, name: String, userId: Long): Result<Unit>
    suspend fun delete(watchlistId: Long): Result<Unit>
    suspend fun addItem(watchlistId: Long, tvdbId: Int, type: ContentType, title: String, posterUrl: String?, year: Int?): Result<Unit>
    suspend fun removeItem(watchlistId: Long, tvdbId: Int, type: ContentType): Result<Unit>
    suspend fun reorder(orderedIds: List<Long>): Result<Unit>
    suspend fun getWatchlistsContaining(userId: Long, tvdbId: Int, type: ContentType): List<Long>
    suspend fun nameExistsForUser(userId: Long, name: String): Boolean
    suspend fun countForUser(userId: Long): Int
}
