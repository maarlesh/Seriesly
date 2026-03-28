package com.seriesly.core.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.seriesly.core.common.model.ContentType
import com.seriesly.core.database.AppDatabase
import com.seriesly.core.database.entity.RatingEntity
import com.seriesly.core.database.entity.UserEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RatingDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: RatingDao
    private var userId: Long = 1L

    @Before fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.ratingDao()
        runTest {
            db.userDao().insert(UserEntity(username = "rater", passwordHash = "h", createdAt = 0, updatedAt = 0))
        }
    }

    @After fun teardown() = db.close()

    @Test fun upsertAndObserveRating() = runTest {
        val rating = RatingEntity(
            userId = userId, tvdbId = 100, contentType = ContentType.MOVIE,
            rating = 4.5f, comment = "Great!", createdAt = 0L, updatedAt = 0L
        )
        dao.upsert(rating)
        dao.observeRating(userId, 100, ContentType.MOVIE).test {
            val result = awaitItem()
            assertNotNull(result)
            assertEquals(4.5f, result!!.rating)
            assertEquals("Great!", result.comment)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test fun deleteRating() = runTest {
        val rating = RatingEntity(
            userId = userId, tvdbId = 200, contentType = ContentType.SERIES,
            rating = 3.0f, comment = null, createdAt = 0L, updatedAt = 0L
        )
        dao.upsert(rating)
        assertEquals(1, dao.getRatingCount(userId))
        val saved = db.ratingDao().observeAllRatings(userId).let {
            runTest { it }
        }
        dao.delete(rating.copy(ratingId = 1L))
        assertEquals(0, dao.getRatingCount(userId))
    }

    @Test fun getRatingCountReturnsCorrectCount() = runTest {
        dao.upsert(RatingEntity(userId = userId, tvdbId = 1, contentType = ContentType.MOVIE, rating = 4f, comment = null, createdAt = 0, updatedAt = 0))
        dao.upsert(RatingEntity(userId = userId, tvdbId = 2, contentType = ContentType.SERIES, rating = 3f, comment = null, createdAt = 0, updatedAt = 0))
        assertEquals(2, dao.getRatingCount(userId))
    }
}
