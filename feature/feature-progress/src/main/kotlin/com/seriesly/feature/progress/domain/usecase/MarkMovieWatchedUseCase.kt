package com.seriesly.feature.progress.domain.usecase

import com.seriesly.core.common.result.Result
import com.seriesly.core.domain.repository.ProgressRepository
import javax.inject.Inject

class MarkMovieWatchedUseCase @Inject constructor(private val repo: ProgressRepository) {
    suspend operator fun invoke(userId: Long, tvdbId: Int, watched: Boolean): Result<Unit> =
        repo.markMovieWatched(userId, tvdbId, watched)
}
