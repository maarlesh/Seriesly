package com.seriesly.feature.watchlist.domain.usecase

import com.seriesly.core.common.result.AppException
import com.seriesly.core.common.result.Result
import com.seriesly.core.domain.repository.WatchlistRepository
import javax.inject.Inject

class CreateWatchlistUseCase @Inject constructor(private val repo: WatchlistRepository) {
    suspend operator fun invoke(userId: Long, name: String): Result<Long> {
        if (name.isBlank()) return Result.Error(AppException.ValidationException("Name cannot be empty"))
        if (name.length > 50) return Result.Error(AppException.ValidationException("Name too long (max 50 chars)"))
        if (repo.nameExistsForUser(userId, name)) return Result.Error(AppException.ValidationException("Name already used"))
        if (repo.countForUser(userId) >= 20) return Result.Error(AppException.ValidationException("Maximum 20 watchlists reached"))
        return repo.create(userId, name)
    }
}
