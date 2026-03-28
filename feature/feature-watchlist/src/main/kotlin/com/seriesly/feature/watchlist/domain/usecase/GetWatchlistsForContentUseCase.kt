package com.seriesly.feature.watchlist.domain.usecase

import com.seriesly.core.common.model.ContentType
import com.seriesly.core.domain.repository.WatchlistRepository
import javax.inject.Inject

class GetWatchlistsForContentUseCase @Inject constructor(private val repo: WatchlistRepository) {
    suspend operator fun invoke(userId: Long, tvdbId: Int, type: ContentType): List<Long> =
        repo.getWatchlistsContaining(userId, tvdbId, type)
}
