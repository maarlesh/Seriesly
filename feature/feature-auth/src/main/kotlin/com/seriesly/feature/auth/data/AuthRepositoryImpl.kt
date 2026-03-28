package com.seriesly.feature.auth.data

import androidx.room.withTransaction
import com.seriesly.core.common.result.AppException
import com.seriesly.core.common.result.Result
import com.seriesly.core.database.AppDatabase
import com.seriesly.core.database.dao.UserDao
import com.seriesly.core.database.dao.WatchlistDao
import com.seriesly.core.database.entity.UserEntity
import com.seriesly.core.database.entity.WatchlistEntity
import com.seriesly.core.security.crypto.PasswordHasher
import com.seriesly.core.security.session.SessionManager
import com.seriesly.feature.auth.domain.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val watchlistDao: WatchlistDao,
    private val sessionManager: SessionManager,
    private val db: AppDatabase
) : AuthRepository {

    override suspend fun register(username: String, password: String): Result<Long> =
        withContext(Dispatchers.IO) {
            try {
                val now = System.currentTimeMillis()
                val hash = PasswordHasher.hash(password)
                var userId = 0L
                db.withTransaction {
                    userId = userDao.insert(
                        UserEntity(username = username, passwordHash = hash, createdAt = now, updatedAt = now)
                    )
                    watchlistDao.insert(
                        WatchlistEntity(
                            userId = userId, name = "To Watch Later",
                            isDefault = true, sortOrder = 0, createdAt = now
                        )
                    )
                }
                sessionManager.saveSession(userId)
                Result.Success(userId)
            } catch (e: android.database.sqlite.SQLiteConstraintException) {
                Result.Error(AppException.AuthException("Username already taken"))
            } catch (e: Exception) {
                Result.Error(AppException.DatabaseException("Registration failed", e))
            }
        }

    override suspend fun login(username: String, password: String): Result<Long> =
        withContext(Dispatchers.IO) {
            val user = userDao.findByUsername(username)
                ?: return@withContext Result.Error(AppException.AuthException("No account found with that username"))
            if (!PasswordHasher.verify(password, user.passwordHash))
                return@withContext Result.Error(AppException.AuthException("Incorrect password"))
            sessionManager.saveSession(user.userId)
            Result.Success(user.userId)
        }

    override suspend fun logout() = sessionManager.clearSession()

    override suspend fun isUsernameTaken(username: String): Boolean =
        withContext(Dispatchers.IO) { userDao.usernameExists(username) > 0 }
}
