package com.seriesly.feature.auth.data

import androidx.room.withTransaction
import com.google.firebase.auth.FirebaseAuth
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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val watchlistDao: WatchlistDao,
    private val sessionManager: SessionManager,
    private val db: AppDatabase,
    private val firebaseAuth: FirebaseAuth,
) : AuthRepository {

    private fun String.toFirebaseEmail() = "$this@seriesly.internal"

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
                runCatching {
                    firebaseAuth.createUserWithEmailAndPassword(username.toFirebaseEmail(), password).await()
                }
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

            // Local account exists — verify password normally
            if (user != null) {
                if (!PasswordHasher.verify(password, user.passwordHash))
                    return@withContext Result.Error(AppException.AuthException("Incorrect password"))
                sessionManager.saveSession(user.userId)
                runCatching {
                    firebaseAuth.signInWithEmailAndPassword(username.toFirebaseEmail(), password).await()
                }
                return@withContext Result.Success(user.userId)
            }

            // Local account not found — try Firebase recovery (new device / fresh install)
            recoverFromFirebase(username, password)
        }

    /**
     * Called when a local Room account doesn't exist but the user claims to have one.
     * Verifies identity via Firebase Auth, then re-creates the local record so the
     * app can operate normally. pullAll() (triggered from ProfileScreen) will restore
     * watchlists, ratings, and progress from Firestore afterwards.
     */
    private suspend fun recoverFromFirebase(username: String, password: String): Result<Long> {
        return try {
            // Verify credentials against Firebase — throws if wrong password / unknown user
            firebaseAuth.signInWithEmailAndPassword(username.toFirebaseEmail(), password).await()

            // Re-create local user with a freshly hashed password
            val now = System.currentTimeMillis()
            val hash = PasswordHasher.hash(password)
            val userId = userDao.insert(
                UserEntity(username = username, passwordHash = hash, createdAt = now, updatedAt = now)
            )
            sessionManager.saveSession(userId)
            // No default watchlist created here — pullAll() will restore the real ones
            Result.Success(userId)
        } catch (e: Exception) {
            Result.Error(AppException.AuthException("No account found with that username"))
        }
    }

    override suspend fun logout() {
        sessionManager.clearSession()
        firebaseAuth.signOut()
    }

    override suspend fun isUsernameTaken(username: String): Boolean =
        withContext(Dispatchers.IO) { userDao.usernameExists(username) > 0 }
}
