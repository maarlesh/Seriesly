package com.seriesly.feature.profile.domain

import com.seriesly.core.common.result.AppException
import com.seriesly.core.common.result.Result
import com.seriesly.core.database.dao.RatingDao
import com.seriesly.core.database.dao.UserDao
import com.seriesly.core.database.dao.WatchlistDao
import com.seriesly.core.security.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class UserStats(
    val username: String,
    val moviesWatched: Int,
    val seriesTracked: Int,
    val totalRatings: Int,
    val totalWatchlists: Int,
    val memberSince: Long
)

class GetUserStatsUseCase @Inject constructor(
    private val userDao: UserDao,
    private val ratingDao: RatingDao,
    private val watchlistDao: WatchlistDao,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(): Result<UserStats> = withContext(Dispatchers.IO) {
        try {
            val userId = sessionManager.getCurrentUserId()
            val user = userDao.findById(userId)
                ?: return@withContext Result.Error(AppException.AuthException())
            val stats = UserStats(
                username        = user.username,
                moviesWatched   = 0,
                seriesTracked   = 0,
                totalRatings    = ratingDao.getRatingCount(userId),
                totalWatchlists = watchlistDao.countForUser(userId),
                memberSince     = user.createdAt
            )
            Result.Success(stats)
        } catch (e: Exception) {
            Result.Error(AppException.DatabaseException("Stats error", e))
        }
    }
}
