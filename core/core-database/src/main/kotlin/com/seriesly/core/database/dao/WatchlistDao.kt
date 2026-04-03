package com.seriesly.core.database.dao

import androidx.room.*
import com.seriesly.core.common.model.ContentType
import com.seriesly.core.database.entity.WatchlistEntity
import com.seriesly.core.database.entity.WatchlistItemEntity
import kotlinx.coroutines.flow.Flow

data class WatchlistWithCounts(
    @ColumnInfo(name = "watchlistId") val watchlistId: Long,
    @ColumnInfo(name = "userId")      val userId: Long,
    @ColumnInfo(name = "name")        val name: String,
    @ColumnInfo(name = "isDefault")   val isDefault: Boolean,
    @ColumnInfo(name = "sortOrder")   val sortOrder: Int,
    @ColumnInfo(name = "createdAt")   val createdAt: Long,
    @ColumnInfo(name = "movieCount")  val movieCount: Int,
    @ColumnInfo(name = "seriesCount") val seriesCount: Int
)

@Dao
interface WatchlistDao {

    @Query("SELECT * FROM watchlists WHERE userId = :userId ORDER BY isDefault DESC, sortOrder ASC, createdAt ASC")
    fun observeByUser(userId: Long): Flow<List<WatchlistEntity>>

    @Query("""
        SELECT w.watchlistId, w.userId, w.name, w.isDefault, w.sortOrder, w.createdAt,
            COALESCE(SUM(CASE WHEN wi.contentType = 'MOVIE'  THEN 1 ELSE 0 END), 0) AS movieCount,
            COALESCE(SUM(CASE WHEN wi.contentType = 'SERIES' THEN 1 ELSE 0 END), 0) AS seriesCount
        FROM watchlists w
        LEFT JOIN watchlist_items wi ON w.watchlistId = wi.watchlistId
        WHERE w.userId = :userId
        GROUP BY w.watchlistId
        ORDER BY w.isDefault DESC, w.sortOrder ASC, w.createdAt ASC
    """)
    fun observeByUserWithCounts(userId: Long): Flow<List<WatchlistWithCounts>>

    @Query("SELECT * FROM watchlists WHERE watchlistId = :watchlistId")
    suspend fun getById(watchlistId: Long): WatchlistEntity?

    @Query("SELECT COUNT(*) FROM watchlists WHERE userId = :userId")
    suspend fun countForUser(userId: Long): Int

    @Query("SELECT COUNT(*) FROM watchlists WHERE userId = :userId")
    fun observeCountForUser(userId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM watchlists WHERE userId = :userId AND LOWER(name) = LOWER(:name)")
    suspend fun nameExistsForUser(userId: Long, name: String): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(watchlist: WatchlistEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWatchlist(watchlist: WatchlistEntity)

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

    @Query("SELECT * FROM watchlist_items WHERE watchlistId = :watchlistId")
    suspend fun getItemsByWatchlistId(watchlistId: Long): List<WatchlistItemEntity>

    @Query("SELECT * FROM watchlist_items WHERE itemId = :itemId")
    suspend fun getItemById(itemId: Long): WatchlistItemEntity?

    @Query("SELECT * FROM watchlist_items WHERE watchlistId = :watchlistId AND tvdbId = :tvdbId AND contentType = :type LIMIT 1")
    suspend fun getItem(watchlistId: Long, tvdbId: Int, type: ContentType): WatchlistItemEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItem(item: WatchlistItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWatchlistItem(item: WatchlistItemEntity)

    @Delete
    suspend fun deleteItem(item: WatchlistItemEntity)

    @Query("DELETE FROM watchlist_items WHERE watchlistId = :watchlistId AND tvdbId = :tvdbId AND contentType = :type")
    suspend fun deleteItemByContent(watchlistId: Long, tvdbId: Int, type: ContentType)

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

    @Query("SELECT * FROM watchlists WHERE pendingSync = 1")
    suspend fun getUnsyncedWatchlists(): List<WatchlistEntity>

    @Query("SELECT * FROM watchlist_items WHERE pendingSync = 1")
    suspend fun getUnsyncedWatchlistItems(): List<WatchlistItemEntity>

    @Query("UPDATE watchlists SET pendingSync = 0, syncedAt = :now WHERE watchlistId = :id")
    suspend fun markWatchlistSynced(id: Long, now: Long)

    @Query("UPDATE watchlist_items SET pendingSync = 0, syncedAt = :now WHERE itemId = :id")
    suspend fun markWatchlistItemSynced(id: Long, now: Long)
}
