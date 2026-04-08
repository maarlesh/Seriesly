package com.seriesly.feature.profile.domain

import com.seriesly.core.common.result.AppException
import com.seriesly.core.common.result.Result
import com.seriesly.core.database.dao.RatingDao
import com.seriesly.core.database.dao.UserDao
import com.seriesly.core.database.dao.WatchlistDao
import com.seriesly.core.database.dao.WatchProgressDao
import com.seriesly.core.security.session.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
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
    private val watchProgressDao: WatchProgressDao,
    private val sessionManager: SessionManager
) {
    operator fun invoke(): Flow<Result<UserStats>> = flow {
        val userId = sessionManager.getCurrentUserId()
        val user = userDao.findById(userId)
            ?: run { emit(Result.Error(AppException.AuthException())); return@flow }

        emitAll(
            combine(
                watchlistDao.observeCountForUser(userId),
                ratingDao.observeRatingCount(userId),
                watchProgressDao.observeMoviesWatchedCount(userId),
                watchProgressDao.observeSeriesTrackedCount(userId)
            ) { watchlistCount, ratingCount, moviesWatched, seriesTracked ->
                Result.Success(
                    UserStats(
                        username        = user.username,
                        moviesWatched   = moviesWatched,
                        seriesTracked   = seriesTracked,
                        totalRatings    = ratingCount,
                        totalWatchlists = watchlistCount,
                        memberSince     = user.createdAt
                    )
                )
            }
        )
    }
}
