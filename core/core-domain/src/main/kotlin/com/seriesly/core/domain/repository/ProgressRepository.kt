package com.seriesly.core.domain.repository

import com.seriesly.core.common.model.ContentType
import com.seriesly.core.common.result.Result
import com.seriesly.core.domain.model.ContentItem
import com.seriesly.core.domain.model.RatedItem
import com.seriesly.core.domain.model.UserRating
import com.seriesly.core.domain.model.WatchHistoryEntry
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    fun observeMovieWatched(userId: Long, tvdbId: Int): Flow<Boolean>
    fun observeRating(userId: Long, tvdbId: Int, type: ContentType): Flow<UserRating?>
    fun observeAllRatings(userId: Long): Flow<List<RatedItem>>
    fun observeRecentlyWatched(userId: Long, limit: Int = 12): Flow<List<ContentItem>>
    fun observeInProgressSeries(userId: Long, limit: Int = 12): Flow<List<ContentItem>>
    fun observeWatchHistory(userId: Long, limit: Int = 200): Flow<List<WatchHistoryEntry>>
    fun observeWatchedMovieTvdbIds(userId: Long): Flow<Set<Int>>
    suspend fun markMovieWatched(userId: Long, tvdbId: Int, watched: Boolean): Result<Unit>
    suspend fun upsertRating(userId: Long, tvdbId: Int, type: ContentType, rating: Float, comment: String?, title: String, posterUrl: String?): Result<Unit>
    suspend fun deleteRating(userId: Long, tvdbId: Int, type: ContentType): Result<Unit>
}
