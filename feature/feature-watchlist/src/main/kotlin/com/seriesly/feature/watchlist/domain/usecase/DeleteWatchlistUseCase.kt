package com.seriesly.feature.watchlist.domain.usecase

import com.seriesly.core.common.result.AppException
import com.seriesly.core.common.result.Result
import com.seriesly.core.domain.model.Watchlist
import com.seriesly.core.domain.repository.WatchlistRepository
import javax.inject.Inject

class DeleteWatchlistUseCase @Inject constructor(private val repo: WatchlistRepository) {
    suspend operator fun invoke(watchlist: Watchlist): Result<Unit> {
        if (watchlist.isDefault) return Result.Error(AppException.ValidationException("Cannot delete the default watchlist"))
        return repo.delete(watchlist.watchlistId)
    }
}
