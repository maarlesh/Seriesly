package com.seriesly.feature.progress.data

import com.seriesly.core.common.model.ContentType
import com.seriesly.core.common.result.AppException
import com.seriesly.core.common.result.Result
import com.seriesly.core.database.dao.MovieDao
import com.seriesly.core.database.dao.RatingDao
import com.seriesly.core.database.dao.SearchCacheDao
import com.seriesly.core.database.dao.SeriesDao
import com.seriesly.core.database.dao.WatchProgressDao
import com.seriesly.core.database.dao.InProgressSeriesEntry
import com.seriesly.core.database.entity.RatingEntity
import com.seriesly.core.database.entity.WatchProgressEntity
import com.seriesly.core.domain.model.ContentItem
import com.seriesly.core.domain.model.RatedItem
import com.seriesly.core.domain.model.UserRating
import com.seriesly.core.domain.model.WatchHistoryEntry
import com.seriesly.core.domain.repository.ProgressRepository
import com.seriesly.core.domain.repository.SyncRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressRepositoryImpl @Inject constructor(
    private val watchProgressDao: WatchProgressDao,
    private val ratingDao: RatingDao,
    private val searchCacheDao: SearchCacheDao,
    private val movieDao: MovieDao,
    private val seriesDao: SeriesDao,
    private val syncRepository: SyncRepository,
) : ProgressRepository {

    override fun observeMovieWatched(userId: Long, tvdbId: Int): Flow<Boolean> =
        watchProgressDao.observeMovieProgress(userId, tvdbId)
            .map { it?.watched == true }
            .flowOn(Dispatchers.IO)

    override fun observeRating(userId: Long, tvdbId: Int, type: ContentType): Flow<UserRating?> =
        ratingDao.observeRating(userId, tvdbId, type)
            .map { it?.toDomain() }
            .flowOn(Dispatchers.IO)

    override fun observeAllRatings(userId: Long): Flow<List<RatedItem>> =
        ratingDao.observeAllRatings(userId)
            .map { entities ->
                entities.map { entity ->
                    RatedItem(
                        tvdbId      = entity.tvdbId,
                        title       = entity.title.ifBlank { "Unknown" },
                        contentType = entity.contentType,
                        posterUrl   = entity.posterUrl,
                        rating      = entity.rating,
                        comment     = entity.comment,
                        ratedAt     = entity.updatedAt
                    )
                }
            }
            .flowOn(Dispatchers.IO)

    override fun observeRecentlyWatched(userId: Long, limit: Int): Flow<List<ContentItem>> =
        watchProgressDao.getRecentlyWatched(userId, limit)
            .map { entries ->
                entries.mapNotNull { entry ->
                    val cached = searchCacheDao.getByTvdbId(entry.tvdbId) ?: return@mapNotNull null
                    ContentItem(
                        tvdbId      = entry.tvdbId,
                        title       = cached.title,
                        contentType = entry.contentType,
                        year        = cached.year,
                        posterUrl   = cached.posterUrl,
                        tvdbRating  = cached.tvdbRating,
                        isWatched   = true
                    )
                }
            }
            .flowOn(Dispatchers.IO)

    override fun observeInProgressSeries(userId: Long, limit: Int): Flow<List<ContentItem>> =
        watchProgressDao.getInProgressSeries(userId, limit)
            .map { entries ->
                entries.mapNotNull { entry ->
                    val cached = searchCacheDao.getByTvdbId(entry.tvdbId) ?: return@mapNotNull null
                    ContentItem(
                        tvdbId      = entry.tvdbId,
                        title       = cached.title,
                        contentType = ContentType.SERIES,
                        year        = cached.year,
                        posterUrl   = cached.posterUrl,
                        tvdbRating  = cached.tvdbRating,
                        isWatched   = false
                    )
                }
            }
            .flowOn(Dispatchers.IO)

    override fun observeWatchedMovieTvdbIds(userId: Long): Flow<Set<Int>> =
        watchProgressDao.observeWatchedMovieTvdbIds(userId)
            .map { it.toSet() }
            .flowOn(Dispatchers.IO)

    override fun observeWatchHistory(userId: Long, limit: Int): Flow<List<WatchHistoryEntry>> =
        watchProgressDao.getWatchHistory(userId, limit)
            .map { entries ->
                entries.mapNotNull { entry ->
                    val title: String
                    val posterUrl: String?
                    val cached = searchCacheDao.getByTvdbId(entry.tvdbId)
                    if (cached != null) {
                        title     = cached.title
                        posterUrl = cached.posterUrl
                    } else when (entry.contentType) {
                        ContentType.MOVIE  -> {
                            val movie = movieDao.getById(entry.tvdbId) ?: return@mapNotNull null
                            title     = movie.title
                            posterUrl = movie.posterUrl
                        }
                        ContentType.SERIES -> {
                            val series = seriesDao.getById(entry.tvdbId) ?: return@mapNotNull null
                            title     = series.title
                            posterUrl = series.posterUrl
                        }
                    }
                    val rating = ratingDao.getRating(userId, entry.tvdbId, entry.contentType)?.rating
                    WatchHistoryEntry(
                        tvdbId      = entry.tvdbId,
                        contentType = entry.contentType,
                        title       = title,
                        posterUrl   = posterUrl,
                        watchedAt   = entry.watchedAt,
                        rating      = rating
                    )
                }
            }
            .flowOn(Dispatchers.IO)

    override suspend fun markMovieWatched(userId: Long, tvdbId: Int, watched: Boolean): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val now       = System.currentTimeMillis()
                val watchedAt = if (watched) now else null
                // Use UPDATE first to avoid the SQLite NULL-uniqueness issue:
                // upsert with episodeId=null inserts duplicate rows because
                // SQLite treats NULL != NULL in unique indexes.
                val updated = watchProgressDao.updateMovieWatched(userId, tvdbId, watched, watchedAt, now)
                if (updated == 0) {
                    // No row yet — first time watching this movie
                    watchProgressDao.upsert(
                        WatchProgressEntity(
                            userId      = userId,
                            tvdbId      = tvdbId,
                            contentType = ContentType.MOVIE,
                            episodeId   = null,
                            watched     = watched,
                            watchedAt   = watchedAt
                        )
                    )
                }
                runCatching { syncRepository.pushPendingProgress() }
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(AppException.DatabaseException("markMovieWatched failed", e))
            }
        }

    override suspend fun upsertRating(
        userId: Long,
        tvdbId: Int,
        type: ContentType,
        rating: Float,
        comment: String?,
        title: String,
        posterUrl: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val now = System.currentTimeMillis()
            ratingDao.upsert(
                RatingEntity(
                    userId      = userId,
                    tvdbId      = tvdbId,
                    contentType = type,
                    rating      = rating,
                    comment     = comment,
                    title       = title,
                    posterUrl   = posterUrl,
                    createdAt   = now,
                    updatedAt   = now
                )
            )
            runCatching { syncRepository.pushPendingRatings() }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(AppException.DatabaseException("upsertRating failed", e))
        }
    }

    override suspend fun deleteRating(userId: Long, tvdbId: Int, type: ContentType): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val entity = RatingEntity(
                    userId = userId, tvdbId = tvdbId, contentType = type,
                    rating = 0f, comment = null, createdAt = 0L, updatedAt = 0L
                )
                runCatching { syncRepository.pushRatingDeletion(tvdbId, type) }
                ratingDao.delete(entity)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(AppException.DatabaseException("deleteRating failed", e))
            }
        }
}

private fun RatingEntity.toDomain() = UserRating(
    ratingId    = ratingId,
    tvdbId      = tvdbId,
    contentType = contentType,
    rating      = rating,
    comment     = comment,
    createdAt   = createdAt,
    updatedAt   = updatedAt
)
