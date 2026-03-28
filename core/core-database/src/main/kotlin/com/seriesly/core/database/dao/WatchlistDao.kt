package com.seriesly.core.database.dao

import androidx.room.*
import com.seriesly.core.common.model.ContentType
import com.seriesly.core.database.entity.WatchlistEntity
import com.seriesly.core.database.entity.WatchlistItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {

    @Query("SELECT * FROM watchlists WHERE userId = :userId ORDER BY isDefault DESC, sortOrder ASC, createdAt ASC")
    fun observeByUser(userId: Long): Flow<List<WatchlistEntity>>

    @Query("SELECT * FROM watchlists WHERE watchlistId = :watchlistId")
    suspend fun getById(watchlistId: Long): WatchlistEntity?

    @Query("SELECT COUNT(*) FROM watchlists WHERE userId = :userId")
    suspend fun countForUser(userId: Long): Int

    @Query("SELECT COUNT(*) FROM watchlists WHERE userId = :userId AND LOWER(name) = LOWER(:name)")
    suspend fun nameExistsForUser(userId: Long, name: String): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(watchlist: WatchlistEntity): Long

    @Update
    suspend fun update(watchlist: WatchlistEntity)

    @Delete
    suspend fun delete(watchlist: WatchlistEntity)

    // Items
    @Query("""
        SELECT * FROM watchlist_items
        WHERE watchlistId = :watchlistId AND contentType = :type
        ORDER BY addedAt DESC
    """)
    fun observeItemsByType(watchlistId: Long, type: ContentType): Flow<List<WatchlistItemEntity>>

    @Query("SELECT * FROM watchlist_items WHERE watchlistId = :watchlistId ORDER BY addedAt DESC")
    fun observeAllItems(watchlistId: Long): Flow<List<WatchlistItemEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItem(item: WatchlistItemEntity): Long

    @Delete
    suspend fun deleteItem(item: WatchlistItemEntity)

    @Query("""
        SELECT COUNT(*) FROM watchlist_items
        WHERE watchlistId = :watchlistId AND tvdbId = :tvdbId AND contentType = :type
    """)
    suspend fun isInWatchlist(watchlistId: Long, tvdbId: Int, type: ContentType): Int

    @Query("""
        SELECT wi.watchlistId FROM watchlist_items wi
        WHERE wi.tvdbId = :tvdbId AND wi.contentType = :type
        AND wi.watchlistId IN (SELECT watchlistId FROM watchlists WHERE userId = :userId)
    """)
    suspend fun getWatchlistIdsContaining(userId: Long, tvdbId: Int, type: ContentType): List<Long>
}
