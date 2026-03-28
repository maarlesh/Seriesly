package com.seriesly.core.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.seriesly.core.common.model.ContentType
import com.seriesly.core.database.AppDatabase
import com.seriesly.core.database.entity.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WatchlistDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: WatchlistDao
    private var userId: Long = 0L

    @Before fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.watchlistDao()
        userId = db.userDao().let {
            runTest {
                it.insert(UserEntity(username = "test", passwordHash = "h", createdAt = 0, updatedAt = 0))
            }
            1L
        }
    }

    @After fun teardown() = db.close()

    @Test fun insertAndObserveWatchlist() = runTest {
        dao.insert(WatchlistEntity(userId = userId, name = "My List", isDefault = false, createdAt = 0L))
        dao.observeByUser(userId).test {
            val lists = awaitItem()
            assertEquals(1, lists.size)
            assertEquals("My List", lists[0].name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test fun defaultWatchlistAppearsFirst() = runTest {
        dao.insert(WatchlistEntity(userId = userId, name = "Custom", isDefault = false, sortOrder = 1, createdAt = 1L))
        dao.insert(WatchlistEntity(userId = userId, name = "To Watch Later", isDefault = true, sortOrder = 0, createdAt = 0L))
        dao.observeByUser(userId).test {
            val lists = awaitItem()
            assertTrue(lists[0].isDefault)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test fun duplicateItemIsIgnored() = runTest {
        val wlId = dao.insert(WatchlistEntity(userId = userId, name = "Fav", isDefault = false, createdAt = 0L))
        val item = WatchlistItemEntity(watchlistId = wlId, tvdbId = 100, contentType = ContentType.MOVIE, addedAt = 0L)
        val first = dao.insertItem(item)
        val second = dao.insertItem(item)
        assertTrue(first > 0)
        assertEquals(-1L, second)
    }

    @Test fun deleteWatchlistCascadesItems() = runTest {
        val wlId = dao.insert(WatchlistEntity(userId = userId, name = "Temp", isDefault = false, createdAt = 0L))
        dao.insertItem(WatchlistItemEntity(watchlistId = wlId, tvdbId = 99, contentType = ContentType.SERIES, addedAt = 0L))
        val entity = dao.getById(wlId)!!
        dao.delete(entity)
        dao.observeAllItems(wlId).test {
            assertEquals(0, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
