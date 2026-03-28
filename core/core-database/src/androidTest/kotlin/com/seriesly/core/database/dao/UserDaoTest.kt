package com.seriesly.core.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.seriesly.core.database.AppDatabase
import com.seriesly.core.database.entity.UserEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: UserDao

    @Before fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.userDao()
    }

    @After fun teardown() = db.close()

    @Test fun insertAndFindByUsername() = runTest {
        val user = UserEntity(username = "alice", passwordHash = "hash", createdAt = 0L, updatedAt = 0L)
        dao.insert(user)
        val found = dao.findByUsername("alice")
        assertNotNull(found)
        assertEquals("alice", found?.username)
    }

    @Test fun usernameIsUniqueConstraint() = runTest {
        dao.insert(UserEntity(username = "alice", passwordHash = "h1", createdAt = 0L, updatedAt = 0L))
        try {
            dao.insert(UserEntity(username = "alice", passwordHash = "h2", createdAt = 0L, updatedAt = 0L))
            fail("Should have thrown on duplicate username")
        } catch (e: Exception) { /* expected */ }
    }

    @Test fun usernameExistsReturnsTrueForKnownUser() = runTest {
        dao.insert(UserEntity(username = "bob", passwordHash = "h", createdAt = 0L, updatedAt = 0L))
        assertEquals(1, dao.usernameExists("bob"))
        assertEquals(0, dao.usernameExists("unknown"))
    }
}
