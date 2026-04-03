package com.seriesly.core.database.dao

import androidx.room.*
import com.seriesly.core.database.entity.EpisodeEntity
import com.seriesly.core.database.entity.SeasonEntity
import com.seriesly.core.database.entity.SeriesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SeriesDao {

    @Query("SELECT * FROM series WHERE tvdbId = :tvdbId")
    fun observeById(tvdbId: Int): Flow<SeriesEntity?>

    @Query("SELECT * FROM series WHERE tvdbId = :tvdbId")
    suspend fun getById(tvdbId: Int): SeriesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(series: SeriesEntity)

    // Seasons
    @Query("SELECT * FROM seasons WHERE seriesTvdbId = :seriesTvdbId ORDER BY seasonNumber ASC")
    fun observeSeasons(seriesTvdbId: Int): Flow<List<SeasonEntity>>

    @Query("SELECT * FROM seasons WHERE seriesTvdbId = :seriesTvdbId ORDER BY seasonNumber ASC")
    suspend fun getSeasons(seriesTvdbId: Int): List<SeasonEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSeasons(seasons: List<SeasonEntity>)

    // Episodes
    @Query("SELECT * FROM episodes WHERE seasonId = :seasonId ORDER BY episodeNumber ASC")
    fun observeEpisodesBySeason(seasonId: Int): Flow<List<EpisodeEntity>>

    @Query("SELECT * FROM episodes WHERE seriesTvdbId = :seriesTvdbId ORDER BY episodeNumber ASC")
    suspend fun getAllEpisodes(seriesTvdbId: Int): List<EpisodeEntity>

    @Query("SELECT * FROM episodes WHERE seriesTvdbId = :seriesTvdbId AND (airDate IS NULL OR airDate <= :today) ORDER BY episodeNumber ASC")
    suspend fun getAiredEpisodes(seriesTvdbId: Int, today: String): List<EpisodeEntity>

    @Query("SELECT COUNT(*) FROM episodes WHERE seriesTvdbId = :seriesTvdbId")
    fun observeEpisodeCount(seriesTvdbId: Int): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEpisodes(episodes: List<EpisodeEntity>)

    @Query("DELETE FROM series WHERE cachedAt < :expiryThreshold")
    suspend fun deleteExpired(expiryThreshold: Long)

    @Query("""
        DELETE FROM series
        WHERE cachedAt < :threshold
        AND tvdbId NOT IN (SELECT tvdbId FROM watchlist_items WHERE contentType = 'SERIES')
        AND tvdbId NOT IN (SELECT DISTINCT tvdbId FROM watch_progress WHERE contentType = 'SERIES')
    """)
    suspend fun pruneStaleSeriesNotReferenced(threshold: Long)

    @Query("""
        DELETE FROM episodes
        WHERE cachedAt < :threshold
        AND seriesTvdbId NOT IN (SELECT tvdbId FROM watchlist_items WHERE contentType = 'SERIES')
        AND seriesTvdbId NOT IN (SELECT DISTINCT tvdbId FROM watch_progress WHERE contentType = 'SERIES')
    """)
    suspend fun pruneStaleEpisodesNotReferenced(threshold: Long)
}
