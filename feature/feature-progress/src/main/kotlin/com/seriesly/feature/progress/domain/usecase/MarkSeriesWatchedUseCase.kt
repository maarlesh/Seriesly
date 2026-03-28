package com.seriesly.feature.progress.domain.usecase

import com.seriesly.core.common.result.Result
import com.seriesly.core.domain.repository.SeriesProgressRepository
import javax.inject.Inject

class MarkSeriesWatchedUseCase @Inject constructor(private val repo: SeriesProgressRepository) {
    suspend operator fun invoke(userId: Long, tvdbId: Int, watched: Boolean): Result<Unit> =
        repo.markAllAiredWatched(userId, tvdbId, watched)
}
