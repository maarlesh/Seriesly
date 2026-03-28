package com.seriesly.feature.watchlist.data

import com.seriesly.core.common.model.ContentType
import com.seriesly.core.common.result.AppException
import com.seriesly.core.common.result.Result
import com.seriesly.core.database.dao.SearchCacheDao
import com.seriesly.core.database.dao.WatchlistDao
import com.seriesly.core.database.entity.WatchlistEntity
import com.seriesly.core.database.entity.WatchlistItemEntity
import com.seriesly.core.domain.model.ContentFilter
import com.seriesly.core.domain.model.ContentItem
import com.seriesly.core.domain.model.Watchlist
import com.seriesly.core.domain.model.WatchlistItem
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
    private val searchCacheDao: SearchCacheDao
) : WatchlistRepository {

    override fun observeWatchlists(userId: Long): Flow<List<Watchlist>> =
        watchlistDao.observeByUser(userId).map { entities ->
            entities.map { it.toDomain() }
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
                watchlistDao.update(entity.copy(name = name))
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(AppException.DatabaseException("Rename failed", e))
            }
        }

    override suspend fun delete(watchlistId: Long): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val entity = watchlistDao.getById(watchlistId) ?: return@withContext Result.Success(Unit)
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
                if (inserted == -1L) Result.Error(AppException.ValidationException("Already in this watchlist"))
                else Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(AppException.DatabaseException("Add failed", e))
            }
        }

    override suspend fun removeItem(watchlistId: Long, tvdbId: Int, type: ContentType): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                watchlistDao.deleteItem(
                    WatchlistItemEntity(watchlistId = watchlistId, tvdbId = tvdbId, contentType = type, addedAt = 0L)
                )
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(AppException.DatabaseException("Remove failed", e))
            }
        }

    override suspend fun reorder(orderedIds: List<Long>): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                orderedIds.forEachIndexed { index, id ->
                    watchlistDao.getById(id)?.let { watchlistDao.update(it.copy(sortOrder = index)) }
                }
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

private fun WatchlistEntity.toDomain() = Watchlist(
    watchlistId = watchlistId,
    name        = name,
    isDefault   = isDefault,
    sortOrder   = sortOrder
)

private fun WatchlistItemEntity.toDomain() = WatchlistItem(
    itemId      = itemId,
    watchlistId = watchlistId,
    tvdbId      = tvdbId,
    contentType = contentType,
    addedAt     = addedAt
)
