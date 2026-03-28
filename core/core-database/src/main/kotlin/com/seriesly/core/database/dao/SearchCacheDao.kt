package com.seriesly.core.database.dao

import androidx.room.*
import com.seriesly.core.database.entity.SearchCacheEntity

@Dao
interface SearchCacheDao {

    @Query("SELECT * FROM search_cache WHERE queryHash = :queryHash ORDER BY cachedAt DESC")
    suspend fun getByQueryHash(queryHash: String): List<SearchCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SearchCacheEntity>)

    @Query("DELETE FROM search_cache WHERE queryHash = :queryHash")
    suspend fun deleteByQueryHash(queryHash: String)

    @Query("DELETE FROM search_cache WHERE cachedAt < :expiryThreshold")
    suspend fun deleteExpired(expiryThreshold: Long)

    @Query("""
        DELETE FROM search_cache WHERE queryHash IN (
            SELECT queryHash FROM search_cache
            GROUP BY queryHash
            ORDER BY MAX(cachedAt) ASC
            LIMIT MAX(0, (SELECT COUNT(DISTINCT queryHash) FROM search_cache) - 500)
        )
    """)
    suspend fun evictExcess()

    @Query("SELECT COUNT(DISTINCT queryHash) FROM search_cache")
    suspend fun getDistinctQueryCount(): Int

    @Query("SELECT * FROM search_cache WHERE tvdbId = :tvdbId LIMIT 1")
    suspend fun getByTvdbId(tvdbId: Int): SearchCacheEntity?
}
