package com.seriesly.core.data.sync

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.seriesly.core.common.model.ContentType
import com.seriesly.core.common.result.AppException
import com.seriesly.core.common.result.Result
import com.seriesly.core.database.dao.RatingDao
import com.seriesly.core.database.dao.WatchProgressDao
import com.seriesly.core.database.dao.WatchlistDao
import com.seriesly.core.database.entity.RatingEntity
import com.seriesly.core.database.entity.WatchProgressEntity
import com.seriesly.core.database.entity.WatchlistEntity
import com.seriesly.core.database.entity.WatchlistItemEntity
import com.seriesly.core.domain.repository.SyncRepository
import com.seriesly.core.domain.repository.SyncScheduler
import com.seriesly.core.security.session.SessionManager
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreSyncRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val watchlistDao: WatchlistDao,
    private val ratingDao: RatingDao,
    private val progressDao: WatchProgressDao,
    private val sessionManager: SessionManager,
    private val syncScheduler: SyncScheduler,
) : SyncRepository {

    private fun uid(): String? = auth.currentUser?.uid

    override suspend fun pushPendingWatchlists() {
        val uid = uid() ?: return
        val watchlists = watchlistDao.getUnsyncedWatchlists()
        val items = watchlistDao.getUnsyncedWatchlistItems()
        if (watchlists.isEmpty() && items.isEmpty()) return

        try {
            val batch = firestore.batch()

            watchlists.forEach { w ->
                val doc = firestore.collection("users").document(uid)
                    .collection("watchlists").document(w.watchlistId.toString())
                batch.set(
                    doc, mapOf(
                        "name" to w.name,
                        "isDefault" to w.isDefault,
                        "sortOrder" to w.sortOrder,
                        "updatedAt" to w.updatedAt,
                        "deleted" to false,
                    )
                )
            }

            items.forEach { item ->
                val doc = firestore.collection("users").document(uid)
                    .collection("watchlistItems").document(item.itemId.toString())
                batch.set(
                    doc, mapOf(
                        "tvdbId" to item.tvdbId,
                        "contentType" to item.contentType.name,
                        "watchlistId" to item.watchlistId,
                        "addedAt" to item.addedAt,
                        "updatedAt" to item.updatedAt,
                        "title" to item.title,
                        "posterUrl" to item.posterUrl,
                        "year" to item.year,
                        "deleted" to false,
                    )
                )
            }

            batch.commit().await()

            val now = System.currentTimeMillis()
            watchlists.forEach { watchlistDao.markWatchlistSynced(it.watchlistId, now) }
            items.forEach { watchlistDao.markWatchlistItemSynced(it.itemId, now) }
        } catch (e: Exception) {
            syncScheduler.schedulePendingSync()
            throw e
        }
    }

    override suspend fun pushPendingRatings() {
        val uid = uid() ?: return
        val ratings = ratingDao.getUnsyncedRatings()
        if (ratings.isEmpty()) return

        try {
            val batch = firestore.batch()

            ratings.forEach { r ->
                // Doc ID: "{tvdbId}_{contentType}" — unique per content item per user
                val docId = "${r.tvdbId}_${r.contentType.name}"
                val doc = firestore.collection("users").document(uid)
                    .collection("ratings").document(docId)
                batch.set(
                    doc, mapOf(
                        "tvdbId" to r.tvdbId,
                        "contentType" to r.contentType.name,
                        "rating" to r.rating,
                        "comment" to r.comment,
                        "title" to r.title,
                        "posterUrl" to r.posterUrl,
                        "updatedAt" to r.updatedAt,
                        "deleted" to false,
                    )
                )
            }

            batch.commit().await()

            val now = System.currentTimeMillis()
            ratings.forEach { ratingDao.markRatingSynced(it.ratingId, now) }
        } catch (e: Exception) {
            syncScheduler.schedulePendingSync()
            throw e
        }
    }

    override suspend fun pushPendingProgress() {
        val uid = uid() ?: return
        val pending = progressDao.getUnsyncedProgress()
        if (pending.isEmpty()) return

        try {
            val now = System.currentTimeMillis()

            for (entry in pending) {
                val docRef = firestore.collection("users").document(uid)
                    .collection("progress").document(entry.tvdbId.toString())

                val data: Map<String, Any> = when {
                    entry.episodeId != null && entry.watched ->
                        mapOf(
                            "completedEpisodeIds" to FieldValue.arrayUnion(entry.episodeId),
                            "updatedAt" to entry.updatedAt,
                        )

                    entry.episodeId != null && !entry.watched ->
                        mapOf(
                            "completedEpisodeIds" to FieldValue.arrayRemove(entry.episodeId),
                            "updatedAt" to entry.updatedAt,
                        )

                    else -> // movie — no episode list, just a watched flag
                        mapOf(
                            "watched" to entry.watched,
                            "updatedAt" to entry.updatedAt,
                        )
                }

                docRef.set(data, SetOptions.merge()).await()
                progressDao.markProgressSynced(entry.progressId, now)
            }
        } catch (e: Exception) {
            syncScheduler.schedulePendingSync()
            throw e
        }
    }

    override suspend fun pushWatchlistDeletion(watchlistId: Long) {
        val uid = uid() ?: return
        firestore.collection("users").document(uid)
            .collection("watchlists").document(watchlistId.toString())
            .set(mapOf("deleted" to true, "updatedAt" to System.currentTimeMillis()))
            .await()
    }

    override suspend fun pushWatchlistItemDeletion(itemId: Long) {
        val uid = uid() ?: return
        firestore.collection("users").document(uid)
            .collection("watchlistItems").document(itemId.toString())
            .set(mapOf("deleted" to true, "updatedAt" to System.currentTimeMillis()))
            .await()
    }

    override suspend fun pushRatingDeletion(tvdbId: Int, type: ContentType) {
        val uid = uid() ?: return
        val docId = "${tvdbId}_${type.name}"
        firestore.collection("users").document(uid)
            .collection("ratings").document(docId)
            .set(mapOf("deleted" to true, "updatedAt" to System.currentTimeMillis()))
            .await()
    }

    override suspend fun pullAll(): Result<Unit> {
        return try {
            val uid = uid() ?: return Result.Success(Unit)
            val userId = sessionManager.getCurrentUserId()
            if (userId == -1L) return Result.Success(Unit)

            val lastPullAt = sessionManager.getLastPullAt()
            val now = System.currentTimeMillis()
            val userRef = firestore.collection("users").document(uid)

            pullWatchlists(userRef, userId, lastPullAt, now)
            pullWatchlistItems(userRef, userId, lastPullAt, now)
            pullRatings(userRef, userId, lastPullAt, now)
            pullProgress(userRef, userId, lastPullAt, now)

            sessionManager.saveLastPullAt(now)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(AppException.NetworkException("Pull failed: ${e.message}"))
        }
    }

    // ── Pull helpers ───────────────────────────────────────────────────────────

    private suspend fun pullWatchlists(
        userRef: DocumentReference, userId: Long, lastPullAt: Long, now: Long
    ) {
        userRef.collection("watchlists")
            .whereGreaterThan("updatedAt", lastPullAt)
            .get().await()
            .forEach { doc ->
                val remoteUpdatedAt = doc.getLong("updatedAt") ?: 0L
                val watchlistId = doc.id.toLongOrNull() ?: return@forEach
                if (doc.getBoolean("deleted") == true) {
                    watchlistDao.getById(watchlistId)?.let { watchlistDao.delete(it) }
                    return@forEach
                }
                val local = watchlistDao.getById(watchlistId)
                if (!SyncMerger.remoteWins(local?.updatedAt ?: 0L, remoteUpdatedAt)) return@forEach
                watchlistDao.upsertWatchlist(
                    WatchlistEntity(
                        watchlistId = watchlistId,
                        userId      = userId,
                        name        = doc.getString("name") ?: "",
                        isDefault   = doc.getBoolean("isDefault") ?: false,
                        sortOrder   = (doc.getLong("sortOrder") ?: 0L).toInt(),
                        createdAt   = local?.createdAt ?: remoteUpdatedAt,
                        updatedAt   = remoteUpdatedAt,
                        pendingSync = false,
                        syncedAt    = now
                    )
                )
            }
    }

    private suspend fun pullWatchlistItems(
        userRef: DocumentReference, userId: Long, lastPullAt: Long, now: Long
    ) {
        userRef.collection("watchlistItems")
            .whereGreaterThan("updatedAt", lastPullAt)
            .get().await()
            .forEach { doc ->
                val remoteUpdatedAt = doc.getLong("updatedAt") ?: 0L
                val itemId = doc.id.toLongOrNull() ?: return@forEach
                if (doc.getBoolean("deleted") == true) {
                    watchlistDao.getItemById(itemId)?.let { watchlistDao.deleteItem(it) }
                    return@forEach
                }
                val local = watchlistDao.getItemById(itemId)
                if (!SyncMerger.remoteWins(local?.updatedAt ?: 0L, remoteUpdatedAt)) return@forEach
                val contentType = runCatching {
                    ContentType.valueOf(doc.getString("contentType") ?: "")
                }.getOrNull() ?: return@forEach
                val watchlistId = doc.getLong("watchlistId") ?: return@forEach
                val tvdbId = (doc.getLong("tvdbId") ?: return@forEach).toInt()
                watchlistDao.upsertWatchlistItem(
                    WatchlistItemEntity(
                        itemId      = itemId,
                        watchlistId = watchlistId,
                        tvdbId      = tvdbId,
                        contentType = contentType,
                        addedAt     = doc.getLong("addedAt") ?: remoteUpdatedAt,
                        updatedAt   = remoteUpdatedAt,
                        title       = doc.getString("title") ?: local?.title ?: "",
                        posterUrl   = doc.getString("posterUrl") ?: local?.posterUrl,
                        year        = doc.getLong("year")?.toInt() ?: local?.year,
                        pendingSync = false,
                        syncedAt    = now
                    )
                )
            }
    }

    private suspend fun pullRatings(
        userRef: DocumentReference, userId: Long, lastPullAt: Long, now: Long
    ) {
        userRef.collection("ratings")
            .whereGreaterThan("updatedAt", lastPullAt)
            .get().await()
            .forEach { doc ->
                val remoteUpdatedAt = doc.getLong("updatedAt") ?: 0L
                val tvdbId = (doc.getLong("tvdbId") ?: return@forEach).toInt()
                val contentType = runCatching {
                    ContentType.valueOf(doc.getString("contentType") ?: "")
                }.getOrNull() ?: return@forEach
                if (doc.getBoolean("deleted") == true) {
                    ratingDao.getRating(userId, tvdbId, contentType)?.let { ratingDao.delete(it) }
                    return@forEach
                }
                val local = ratingDao.getRating(userId, tvdbId, contentType)
                if (!SyncMerger.remoteWins(local?.updatedAt ?: 0L, remoteUpdatedAt)) return@forEach
                ratingDao.upsert(
                    RatingEntity(
                        ratingId    = local?.ratingId ?: 0L,
                        userId      = userId,
                        tvdbId      = tvdbId,
                        contentType = contentType,
                        rating      = (doc.getDouble("rating") ?: 0.0).toFloat(),
                        comment     = doc.getString("comment"),
                        title       = doc.getString("title") ?: local?.title ?: "",
                        posterUrl   = doc.getString("posterUrl") ?: local?.posterUrl,
                        createdAt   = local?.createdAt ?: remoteUpdatedAt,
                        updatedAt   = remoteUpdatedAt,
                        pendingSync = false,
                        syncedAt    = now
                    )
                )
            }
    }

    private suspend fun pullProgress(
        userRef: DocumentReference, userId: Long, lastPullAt: Long, now: Long
    ) {
        userRef.collection("progress")
            .whereGreaterThan("updatedAt", lastPullAt)
            .get().await()
            .forEach { doc ->
                val remoteUpdatedAt = doc.getLong("updatedAt") ?: 0L
                val tvdbId = doc.id.toIntOrNull() ?: return@forEach
                val localRows = progressDao.getProgressByContent(userId, tvdbId)
                val localMax = localRows.maxOfOrNull { it.updatedAt } ?: 0L
                if (!SyncMerger.remoteWins(localMax, remoteUpdatedAt)) return@forEach

                @Suppress("UNCHECKED_CAST")
                val completedIds = (doc["completedEpisodeIds"] as? List<*>)
                    ?.mapNotNull { (it as? Long)?.toInt() }

                if (completedIds != null) {
                    // Series — sync episode-level rows
                    completedIds.forEach { epId ->
                        val existing = localRows.find { it.episodeId == epId }
                        progressDao.upsert(
                            WatchProgressEntity(
                                progressId  = existing?.progressId ?: 0L,
                                userId      = userId,
                                tvdbId      = tvdbId,
                                contentType = ContentType.SERIES,
                                episodeId   = epId,
                                watched     = true,
                                watchedAt   = remoteUpdatedAt,
                                updatedAt   = remoteUpdatedAt,
                                pendingSync = false,
                                syncedAt    = now
                            )
                        )
                    }
                    // Local watched episodes absent from Firestore → mark unwatched
                    localRows
                        .filter { it.watched && it.episodeId != null && it.episodeId !in completedIds }
                        .forEach { row ->
                            progressDao.upsert(
                                row.copy(
                                    watched     = false,
                                    watchedAt   = null,
                                    updatedAt   = remoteUpdatedAt,
                                    pendingSync = false,
                                    syncedAt    = now
                                )
                            )
                        }
                } else {
                    // Movie
                    val watched = doc.getBoolean("watched") ?: return@forEach
                    val existing = localRows.firstOrNull()
                    progressDao.upsert(
                        WatchProgressEntity(
                            progressId  = existing?.progressId ?: 0L,
                            userId      = userId,
                            tvdbId      = tvdbId,
                            contentType = ContentType.MOVIE,
                            episodeId   = null,
                            watched     = watched,
                            watchedAt   = if (watched) remoteUpdatedAt else null,
                            updatedAt   = remoteUpdatedAt,
                            pendingSync = false,
                            syncedAt    = now
                        )
                    )
                }
            }
    }
}
