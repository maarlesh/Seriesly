package com.seriesly.core.database.dao

import androidx.room.*
import com.seriesly.core.common.model.ContentType
import com.seriesly.core.database.entity.RatingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RatingDao {

    @Query("""
        SELECT * FROM ratings
        WHERE userId = :userId AND tvdbId = :tvdbId AND contentType = :type
    """)
    fun observeRating(userId: Long, tvdbId: Int, type: ContentType): Flow<RatingEntity?>

    @Query("SELECT * FROM ratings WHERE userId = :userId ORDER BY updatedAt DESC")
    fun observeAllRatings(userId: Long): Flow<List<RatingEntity>>

    @Query("""
        SELECT * FROM ratings WHERE userId = :userId AND contentType = :type
        ORDER BY updatedAt DESC
    """)
    fun observeRatingsByType(userId: Long, type: ContentType): Flow<List<RatingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(rating: RatingEntity)

    @Delete
    suspend fun delete(rating: RatingEntity)

    @Query("SELECT COUNT(*) FROM ratings WHERE userId = :userId")
    suspend fun getRatingCount(userId: Long): Int
}
