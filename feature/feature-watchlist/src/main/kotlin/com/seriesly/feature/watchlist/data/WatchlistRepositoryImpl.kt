package com.seriesly.feature.watchlist.data

import com.seriesly.core.common.model.ContentType
import com.seriesly.core.common.result.AppException
import com.seriesly.core.common.result.Result
import com.seriesly.core.database.dao.SearchCacheDao
import com.seriesly.core.database.dao.WatchlistDao
import com.seriesly.core.database.dao.WatchlistWithCounts
import com.seriesly.core.database.entity.WatchlistEntity
import com.seriesly.core.database.entity.WatchlistItemEntity
import com.seriesly.core.domain.model.ContentFilter
import com.seriesly.core.domain.model.ContentItem
import com.seriesly.core.domain.model.Watchlist
import com.seriesly.core.domain.model.WatchlistItem
import com.seriesly.core.domain.repository.SyncRepository
import com.seriesly.core.domain.repository.WatchlistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchlistRepositoryImpl @Inject constructor(
    private val watchlistDao: WatchlistDao,
    private val searchCacheDao: SearchCacheDao,
    private val syncRepository: SyncRepository,
) : WatchlistRepository {

    override fun observeWatchlists(userId: Long): Flow<List<Watchlist>> =
        watchlistDao.observeByUserWithCounts(userId).map { rows ->
            rows.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)

    override fun observeItems(watchlistId: Long, type: ContentFilter): Flow<List<WatchlistItem>> =
        when (type) {
            ContentFilter.ALL    -> watchlistDao.observeAllItems(watchlistId)
            ContentFilter.MOVIES -> watchlistDao.observeItemsByType(watchlistId, ContentType.MOVIE)
            ContentFilter.SERIES -> watchlistDao.observeItemsByType(watchlistId, ContentType.SERIES)
        }.map { entities -> entities.map { it.toDomain() } }.flowOn(Dispatchers.IO)

    override fun observeItemsAsContent(watchlistId: Long, filter: ContentFilter): Flow<List<ContentItem>> =
        when (filter) {
            ContentFilter.ALL    -> watchlistDao.observeAllItems(watchlistId)
            ContentFilter.MOVIES -> watchlistDao.observeItemsByType(watchlistId, ContentType.MOVIE)
            ContentFilter.SERIES -> watchlistDao.observeItemsByType(watchlistId, ContentType.SERIES)
        }.map { items ->
            items.mapNotNull { item ->
                searchCacheDao.getByTvdbId(item.tvdbId)?.let { cached ->
                    ContentItem(
                        tvdbId      = cached.tvdbId,
                        title       = cached.title,
                        contentType = cached.contentType,
                        year        = cached.year,
                        posterUrl   = cached.posterUrl,
                        tvdbRating  = cached.tvdbRating
                    )
                }
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun create(userId: Long, name: String): Result<Long> =
        withContext(Dispatchers.IO) {
            try {
                val id = watchlistDao.insert(
                    WatchlistEntity(userId = userId, name = name, isDefault = false, createdAt = System.currentTimeMillis())
                )
                runCatching { syncRepository.pushPendingWatchlists() }
                Result.Success(id)
            } catch (e: Exception) {
                Result.Error(AppException.DatabaseException("Failed to create watchlist", e))
            }
        }

    override suspend fun rename(watchlistId: Long, name: String, userId: Long): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val entity = watchlistDao.getById(watchlistId)
                    ?: return@withContext Result.Error(AppException.DatabaseException("Not found", Exception()))
                watchlistDao.update(entity.copy(name = name, updatedAt = System.currentTimeMillis(), pendingSync = true))
                runCatching { syncRepository.pushPendingWatchlists() }
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(AppException.DatabaseException("Rename failed", e))
            }
        }

    override suspend fun delete(watchlistId: Long): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val entity = watchlistDao.getById(watchlistId) ?: return@withContext Result.Success(Unit)
                // Soft-delete all items in Firestore before Room cascade-deletes them
                watchlistDao.getItemsByWatchlistId(watchlistId).forEach { item ->
                    runCatching { syncRepository.pushWatchlistItemDeletion(item.itemId) }
                }
                runCatching { syncRepository.pushWatchlistDeletion(watchlistId) }
                watchlistDao.delete(entity)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(AppException.DatabaseException("Delete failed", e))
            }
        }

    override suspend fun addItem(watchlistId: Long, tvdbId: Int, type: ContentType): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val inserted = watchlistDao.insertItem(
                    WatchlistItemEntity(watchlistId = watchlistId, tvdbId = tvdbId, contentType = type, addedAt = System.currentTimeMillis())
                )
                if (inserted == -1L) return@withContext Result.Error(AppException.ValidationException("Already in this watchlist"))
                runCatching { syncRepository.pushPendingWatchlists() }
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(AppException.DatabaseException("Add failed", e))
            }
        }

    override suspend fun removeItem(watchlistId: Long, tvdbId: Int, type: ContentType): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val item = watchlistDao.getItem(watchlistId, tvdbId, type)
                if (item != null) {
                    runCatching { syncRepository.pushWatchlistItemDeletion(item.itemId) }
                }
                watchlistDao.deleteItemByContent(watchlistId, tvdbId, type)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(AppException.DatabaseException("Remove failed", e))
            }
        }

    override suspend fun reorder(orderedIds: List<Long>): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                orderedIds.forEachIndexed { index, id ->
                    watchlistDao.getById(id)?.let {
                        watchlistDao.update(it.copy(sortOrder = index, updatedAt = System.currentTimeMillis(), pendingSync = true))
                    }
                }
                runCatching { syncRepository.pushPendingWatchlists() }
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(AppException.DatabaseException("Reorder failed", e))
            }
        }

    override suspend fun getWatchlistsContaining(userId: Long, tvdbId: Int, type: ContentType): List<Long> =
        withContext(Dispatchers.IO) { watchlistDao.getWatchlistIdsContaining(userId, tvdbId, type) }

    override suspend fun nameExistsForUser(userId: Long, name: String): Boolean =
        withContext(Dispatchers.IO) { watchlistDao.nameExistsForUser(userId, name) > 0 }

    override suspend fun countForUser(userId: Long): Int =
        withContext(Dispatchers.IO) { watchlistDao.countForUser(userId) }
}

private fun WatchlistWithCounts.toDomain() = Watchlist(
    watchlistId = watchlistId,
    name        = name,
    isDefault   = isDefault,
    sortOrder   = sortOrder,
    movieCount  = movieCount,
    seriesCount = seriesCount
)

private fun WatchlistItemEntity.toDomain() = WatchlistItem(
    itemId      = itemId,
    watchlistId = watchlistId,
    tvdbId      = tvdbId,
    contentType = contentType,
    addedAt     = addedAt
)
