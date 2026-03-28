package com.seriesly.core.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.seriesly.core.common.model.ContentType
import com.seriesly.core.database.AppDatabase
import com.seriesly.core.database.entity.UserEntity
import com.seriesly.core.database.entity.WatchProgressEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WatchProgressDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: WatchProgressDao
    private var userId: Long = 1L

    @Before fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.watchProgressDao()
        runTest {
            db.userDao().insert(UserEntity(username = "user", passwordHash = "h", createdAt = 0, updatedAt = 0))
        }
    }

    @After fun teardown() = db.close()

    @Test fun markMovieWatched() = runTest {
        val progress = WatchProgressEntity(
            userId = userId, tvdbId = 200, contentType = ContentType.MOVIE,
            episodeId = null, watched = false, watchedAt = null
        )
        dao.upsert(progress)
        dao.updateMovieWatched(userId, 200, watched = true, watchedAt = 1000L)
        dao.observeMovieProgress(userId, 200).test {
            val result = awaitItem()
            assertNotNull(result)
            assertTrue(result!!.watched)
            assertEquals(1000L, result.watchedAt)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test fun upsertAllEpisodesForSeries() = runTest {
        val episodes = (1..3).map { ep ->
            WatchProgressEntity(
                userId = userId, tvdbId = 300, contentType = ContentType.SERIES,
                episodeId = ep, watched = false, watchedAt = null
            )
        }
        dao.upsertAll(episodes)
        dao.observeSeriesProgress(userId, 300).test {
            val result = awaitItem()
            assertEquals(3, result.size)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
