package com.seriesly.core.data.sync

import com.google.android.gms.tasks.Tasks
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.seriesly.core.common.model.ContentType
import com.seriesly.core.common.result.Result
import com.seriesly.core.database.dao.RatingDao
import com.seriesly.core.database.dao.WatchProgressDao
import com.seriesly.core.database.dao.WatchlistDao
import com.seriesly.core.database.entity.RatingEntity
import com.seriesly.core.database.entity.WatchlistEntity
import com.seriesly.core.database.entity.WatchlistItemEntity
import com.seriesly.core.domain.repository.SyncScheduler
import com.seriesly.core.security.session.SessionManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class FirestoreSyncRepositoryTest {

    private val firestore: FirebaseFirestore = mockk()
    private val auth: FirebaseAuth = mockk()
    private val watchlistDao: WatchlistDao = mockk(relaxed = true)
    private val ratingDao: RatingDao = mockk(relaxed = true)
    private val progressDao: WatchProgressDao = mockk(relaxed = true)
    private val sessionManager: SessionManager = mockk()
    private val syncScheduler: SyncScheduler = mockk(relaxed = true)

    private val firebaseUser: FirebaseUser = mockk()
    private val userDoc: DocumentReference = mockk()
    private val collectionRef: CollectionReference = mockk()
    private val docRef: DocumentReference = mockk()

    private lateinit var repository: FirestoreSyncRepository

    @Before
    fun setUp() {
        repository = FirestoreSyncRepository(
            firestore = firestore,
            auth = auth,
            watchlistDao = watchlistDao,
            ratingDao = ratingDao,
            progressDao = progressDao,
            sessionManager = sessionManager,
            syncScheduler = syncScheduler,
        )
        // Wire Firestore path: users/{uid}/…
        every { firestore.collection("users") } returns collectionRef
        every { collectionRef.document(any()) } returns userDoc
        every { userDoc.collection(any()) } returns collectionRef
        every { collectionRef.document(any()) } returns docRef
    }

    // ── pushPendingWatchlists ─────────────────────────────────────────────────

    @Test
    fun `pushPendingWatchlists does nothing when no user is signed in`() = runTest {
        every { auth.currentUser } returns null
        coEvery { watchlistDao.getUnsyncedWatchlists() } returns emptyList()
        coEvery { watchlistDao.getUnsyncedWatchlistItems() } returns emptyList()

        repository.pushPendingWatchlists()

        coVerify(exactly = 0) { watchlistDao.getUnsyncedWatchlists() }
    }

    @Test
    fun `pushPendingWatchlists does nothing when queues are empty`() = runTest {
        every { auth.currentUser } returns firebaseUser
        every { firebaseUser.uid } returns "uid-1"
        coEvery { watchlistDao.getUnsyncedWatchlists() } returns emptyList()
        coEvery { watchlistDao.getUnsyncedWatchlistItems() } returns emptyList()

        repository.pushPendingWatchlists()

        verify(exactly = 0) { syncScheduler.schedulePendingSync() }
    }

    @Test
    fun `pushPendingWatchlists schedules sync and rethrows when Firestore batch fails`() = runTest {
        every { auth.currentUser } returns firebaseUser
        every { firebaseUser.uid } returns "uid-1"

        val watchlist = WatchlistEntity(
            watchlistId = 1L, userId = 1L, name = "Favourites",
            createdAt = 1000L, updatedAt = 2000L
        )
        coEvery { watchlistDao.getUnsyncedWatchlists() } returns listOf(watchlist)
        coEvery { watchlistDao.getUnsyncedWatchlistItems() } returns emptyList()

        val batch = mockk<com.google.firebase.firestore.WriteBatch>(relaxed = true)
        every { firestore.batch() } returns batch
        every { batch.set(any(), any<Map<String, Any>>()) } returns batch
        every { batch.commit() } returns Tasks.forException(RuntimeException("offline"))

        var threw = false
        try {
            repository.pushPendingWatchlists()
        } catch (_: Exception) {
            threw = true
        }

        assertThat(threw).isTrue()
        verify(exactly = 1) { syncScheduler.schedulePendingSync() }
    }

    // ── pushPendingRatings ────────────────────────────────────────────────────

    @Test
    fun `pushPendingRatings does nothing when no user is signed in`() = runTest {
        every { auth.currentUser } returns null

        repository.pushPendingRatings()

        coVerify(exactly = 0) { ratingDao.getUnsyncedRatings() }
    }

    @Test
    fun `pushPendingRatings does nothing when ratings queue is empty`() = runTest {
        every { auth.currentUser } returns firebaseUser
        every { firebaseUser.uid } returns "uid-1"
        coEvery { ratingDao.getUnsyncedRatings() } returns emptyList()

        repository.pushPendingRatings()

        verify(exactly = 0) { syncScheduler.schedulePendingSync() }
    }

    @Test
    fun `pushPendingRatings schedules sync and rethrows when Firestore batch fails`() = runTest {
        every { auth.currentUser } returns firebaseUser
        every { firebaseUser.uid } returns "uid-1"

        val rating = RatingEntity(
            ratingId = 1L, userId = 1L, tvdbId = 42,
            contentType = ContentType.SERIES, rating = 8.5f,
            comment = null, createdAt = 1000L, updatedAt = 2000L
        )
        coEvery { ratingDao.getUnsyncedRatings() } returns listOf(rating)

        val batch = mockk<com.google.firebase.firestore.WriteBatch>(relaxed = true)
        every { firestore.batch() } returns batch
        every { batch.set(any(), any<Map<String, Any>>()) } returns batch
        every { batch.commit() } returns Tasks.forException(RuntimeException("offline"))

        var threw = false
        try {
            repository.pushPendingRatings()
        } catch (_: Exception) {
            threw = true
        }

        assertThat(threw).isTrue()
        verify(exactly = 1) { syncScheduler.schedulePendingSync() }
    }

    // ── pushPendingProgress ───────────────────────────────────────────────────

    @Test
    fun `pushPendingProgress does nothing when no user is signed in`() = runTest {
        every { auth.currentUser } returns null

        repository.pushPendingProgress()

        coVerify(exactly = 0) { progressDao.getUnsyncedProgress() }
    }

    @Test
    fun `pushPendingProgress does nothing when progress queue is empty`() = runTest {
        every { auth.currentUser } returns firebaseUser
        every { firebaseUser.uid } returns "uid-1"
        coEvery { progressDao.getUnsyncedProgress() } returns emptyList()

        repository.pushPendingProgress()

        verify(exactly = 0) { syncScheduler.schedulePendingSync() }
    }

    // ── pullAll ───────────────────────────────────────────────────────────────

    @Test
    fun `pullAll returns Success with no-op when no Firebase user is signed in`() = runTest {
        every { auth.currentUser } returns null

        val result = repository.pullAll()

        assertThat(result).isInstanceOf(Result.Success::class.java)
    }

    @Test
    fun `pullAll returns Success with no-op when local userId is not set`() = runTest {
        every { auth.currentUser } returns firebaseUser
        every { firebaseUser.uid } returns "uid-1"
        every { sessionManager.getCurrentUserId() } returns -1L

        val result = repository.pullAll()

        assertThat(result).isInstanceOf(Result.Success::class.java)
    }

    @Test
    fun `pullAll returns Error when Firestore query throws`() = runTest {
        every { auth.currentUser } returns firebaseUser
        every { firebaseUser.uid } returns "uid-1"
        every { sessionManager.getCurrentUserId() } returns 1L
        every { sessionManager.getLastPullAt() } returns 0L

        // Make the watchlists collection query fail
        val query = mockk<com.google.firebase.firestore.Query>()
        every { collectionRef.whereGreaterThan(any<String>(), any()) } returns query
        every { query.get() } returns Tasks.forException(RuntimeException("network error"))

        val result = repository.pullAll()

        assertThat(result).isInstanceOf(Result.Error::class.java)
    }
}
