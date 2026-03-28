package com.seriesly.feature.watchlist.domain.usecase

import com.seriesly.core.domain.model.Watchlist
import com.seriesly.core.domain.repository.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveWatchlistsUseCase @Inject constructor(private val repo: WatchlistRepository) {
    operator fun invoke(userId: Long): Flow<List<Watchlist>> = repo.observeWatchlists(userId)
}
