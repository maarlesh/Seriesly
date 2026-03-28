package com.seriesly.feature.progress.data

import com.seriesly.core.common.model.ContentType
import com.seriesly.core.common.result.AppException
import com.seriesly.core.common.result.Result
import com.seriesly.core.database.AppDatabase
import com.seriesly.core.database.dao.SeriesDao
import com.seriesly.core.database.dao.WatchProgressDao
import com.seriesly.core.database.entity.WatchProgressEntity
import androidx.room.withTransaction
import com.seriesly.core.domain.model.SeriesProgress
import com.seriesly.core.domain.model.SeriesWatchStatus
import com.seriesly.core.domain.repository.SeriesProgressRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeriesProgressRepositoryImpl @Inject constructor(
    private val watchProgressDao: WatchProgressDao,
    private val seriesDao: SeriesDao,
    private val db: AppDatabase
) : SeriesProgressRepository {

    override fun observeSeriesProgress(userId: Long, tvdbId: Int): Flow<SeriesProgress> =
        combine(
            watchProgressDao.observeSeriesProgress(userId, tvdbId),
            seriesDao.observeEpisodeCount(tvdbId)
        ) { progressList, episodeCount ->
            val watchedCount = progressList.count { it.watched && it.episodeId != null }
            SeriesProgress(
                seriesTvdbId          = tvdbId,
                status                = deriveStatus(watchedCount, episodeCount),
                totalAiredEpisodes    = episodeCount,
                watchedEpisodes       = watchedCount
            )
        }
        .flowOn(Dispatchers.IO)

    override fun observeEpisodeWatched(userId: Long, tvdbId: Int): Flow<Map<Int, Boolean>> =
        watchProgressDao.observeSeriesProgress(userId, tvdbId)
            .map { list ->
                list.filter { it.episodeId != null }
                    .associate { it.episodeId!! to it.watched }
            }
            .flowOn(Dispatchers.IO)

    override suspend fun markEpisodeWatched(
        userId: Long, tvdbId: Int, episodeId: Int, watched: Boolean
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            watchProgressDao.upsert(
                WatchProgressEntity(
                    userId      = userId,
                    tvdbId      = tvdbId,
                    contentType = ContentType.SERIES,
                    episodeId   = episodeId,
                    watched     = watched,
                    watchedAt   = if (watched) System.currentTimeMillis() else null
                )
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(AppException.DatabaseException("markEpisodeWatched failed", e))
        }
    }

    override suspend fun markSeasonEpisodesWatched(
        userId: Long, tvdbId: Int, episodeIds: List<Int>, watched: Boolean
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val now = if (watched) System.currentTimeMillis() else null
            val progressList = episodeIds.map { epId ->
                WatchProgressEntity(
                    userId      = userId,
                    tvdbId      = tvdbId,
                    contentType = ContentType.SERIES,
                    episodeId   = epId,
                    watched     = watched,
                    watchedAt   = now
                )
            }
            db.withTransaction { watchProgressDao.upsertAll(progressList) }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(AppException.DatabaseException("markSeasonEpisodesWatched failed", e))
        }
    }

    override suspend fun markAllAiredWatched(
        userId: Long, tvdbId: Int, watched: Boolean
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val today        = LocalDate.now().toString()
            val airedEpisodes = seriesDao.getAiredEpisodes(tvdbId, today)
            val now          = if (watched) System.currentTimeMillis() else null
            val progressList = airedEpisodes.map { ep ->
                WatchProgressEntity(
                    userId      = userId,
                    tvdbId      = tvdbId,
                    contentType = ContentType.SERIES,
                    episodeId   = ep.episodeId,
                    watched     = watched,
                    watchedAt   = now
                )
            }
            db.withTransaction { watchProgressDao.upsertAll(progressList) }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(AppException.DatabaseException("markAllAiredWatched failed", e))
        }
    }

    private fun deriveStatus(watched: Int, totalAired: Int): SeriesWatchStatus = when {
        totalAired == 0 || watched == 0 -> SeriesWatchStatus.NOT_STARTED
        watched >= totalAired           -> SeriesWatchStatus.WATCHED
        else                            -> SeriesWatchStatus.IN_PROGRESS
    }
}
