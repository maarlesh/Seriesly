package com.seriesly.feature.watchlist.domain.usecase

import com.seriesly.core.common.result.AppException
import com.seriesly.core.common.result.Result
import com.seriesly.core.domain.repository.WatchlistRepository
import javax.inject.Inject

class RenameWatchlistUseCase @Inject constructor(private val repo: WatchlistRepository) {
    suspend operator fun invoke(watchlistId: Long, name: String, userId: Long): Result<Unit> {
        if (name.isBlank()) return Result.Error(AppException.ValidationException("Name cannot be empty"))
        if (name.length > 50) return Result.Error(AppException.ValidationException("Name too long (max 50 chars)"))
        return repo.rename(watchlistId, name, userId)
    }
}
