package com.seriesly.core.database.dao

import androidx.room.*
import com.seriesly.core.database.entity.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Query("SELECT * FROM movies WHERE tvdbId = :tvdbId")
    fun observeById(tvdbId: Int): Flow<MovieEntity?>

    @Query("SELECT * FROM movies WHERE tvdbId = :tvdbId")
    suspend fun getById(tvdbId: Int): MovieEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(movie: MovieEntity)

    @Query("UPDATE movies SET cachedAt = :timestamp WHERE tvdbId = :tvdbId")
    suspend fun updateCacheTimestamp(tvdbId: Int, timestamp: Long)

    @Query("DELETE FROM movies WHERE cachedAt < :expiryThreshold")
    suspend fun deleteExpired(expiryThreshold: Long)
}
