package com.seriesly.feature.watchlist.domain.usecase

import com.seriesly.core.common.result.Result
import com.seriesly.core.domain.repository.WatchlistRepository
import javax.inject.Inject

class ReorderWatchlistsUseCase @Inject constructor(private val repo: WatchlistRepository) {
    suspend operator fun invoke(orderedIds: List<Long>): Result<Unit> = repo.reorder(orderedIds)
}
