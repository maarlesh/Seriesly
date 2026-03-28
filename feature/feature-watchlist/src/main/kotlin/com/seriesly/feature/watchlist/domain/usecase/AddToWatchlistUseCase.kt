package com.seriesly.feature.watchlist.domain.usecase

import com.seriesly.core.common.model.ContentType
import com.seriesly.core.common.result.Result
import com.seriesly.core.domain.repository.WatchlistRepository
import javax.inject.Inject

class AddToWatchlistUseCase @Inject constructor(private val repo: WatchlistRepository) {
    suspend operator fun invoke(watchlistId: Long, tvdbId: Int, type: ContentType): Result<Unit> =
        repo.addItem(watchlistId, tvdbId, type)
}
