package com.seriesly.core.database.dao

import androidx.room.*
import com.seriesly.core.common.model.ContentType
import com.seriesly.core.database.entity.WatchProgressEntity
import kotlinx.coroutines.flow.Flow

data class RecentWatchEntry(
    @ColumnInfo(name = "tvdbId") val tvdbId: Int,
    @ColumnInfo(name = "contentType") val contentType: ContentType,
    @ColumnInfo(name = "lastWatchedAt") val lastWatchedAt: Long?
)

data class InProgressSeriesEntry(
    @ColumnInfo(name = "tvdbId") val tvdbId: Int,
    @ColumnInfo(name = "lastWatchedAt") val lastWatchedAt: Long?
)

@Dao
interface WatchProgressDao {

    @Query("""
        SELECT * FROM watch_progress
        WHERE userId = :userId AND tvdbId = :tvdbId
        AND contentType = 'MOVIE' AND episodeId IS NULL
    """)
    fun observeMovieProgress(userId: Long, tvdbId: Int): Flow<WatchProgressEntity?>

    @Query("""
        SELECT * FROM watch_progress
        WHERE userId = :userId AND tvdbId = :tvdbId
        AND contentType = 'SERIES'
        ORDER BY episodeId ASC
    """)
    fun observeSeriesProgress(userId: Long, tvdbId: Int): Flow<List<WatchProgressEntity>>

    @Query("""
        SELECT COUNT(*) FROM watch_progress
        WHERE userId = :userId AND tvdbId = :tvdbId
        AND watched = 1 AND episodeId IN (:episodeIds)
    """)
    fun observeWatchedCountForEpisodes(
        userId: Long,
        tvdbId: Int,
        episodeIds: List<Int>
    ): Flow<Int>

    @Query("""
        SELECT * FROM watch_progress
        WHERE userId = :userId AND episodeId = :episodeId
    """)
    fun observeEpisodeProgress(userId: Long, episodeId: Int): Flow<WatchProgressEntity?>

    @Query("""
        SELECT tvdbId, contentType, MAX(watchedAt) as lastWatchedAt
        FROM watch_progress
        WHERE userId = :userId AND watched = 1 AND contentType = 'MOVIE'
        GROUP BY tvdbId
        ORDER BY MAX(watchedAt) DESC
        LIMIT :limit
    """)
    fun getRecentlyWatched(userId: Long, limit: Int): Flow<List<RecentWatchEntry>>

    @Query("""
        SELECT wp.tvdbId, MAX(wp.watchedAt) as lastWatchedAt
        FROM watch_progress wp
        WHERE wp.userId = :userId
          AND wp.watched = 1
          AND wp.contentType = 'SERIES'
          AND wp.episodeId IS NOT NULL
        GROUP BY wp.tvdbId
        HAVING COUNT(wp.episodeId) < (
            SELECT COUNT(*) FROM episodes WHERE seriesTvdbId = wp.tvdbId
        )
        ORDER BY MAX(wp.watchedAt) DESC
        LIMIT :limit
    """)
    fun getInProgressSeries(userId: Long, limit: Int): Flow<List<InProgressSeriesEntry>>

    @Query("SELECT * FROM watch_progress WHERE userId = :userId AND tvdbId = :tvdbId")
    suspend fun getProgressByContent(userId: Long, tvdbId: Int): List<WatchProgressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: WatchProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(progressList: List<WatchProgressEntity>)

    @Query("""
        UPDATE watch_progress SET watched = :watched, watchedAt = :watchedAt,
        updatedAt = :updatedAt, pendingSync = 1
        WHERE userId = :userId AND tvdbId = :tvdbId AND contentType = 'MOVIE' AND episodeId IS NULL
    """)
    suspend fun updateMovieWatched(userId: Long, tvdbId: Int, watched: Boolean, watchedAt: Long?, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT * FROM watch_progress WHERE pendingSync = 1")
    suspend fun getUnsyncedProgress(): List<WatchProgressEntity>

    @Query("UPDATE watch_progress SET pendingSync = 0, syncedAt = :now WHERE progressId = :id")
    suspend fun markProgressSynced(id: Long, now: Long)
}
