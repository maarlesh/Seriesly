package com.seriesly.feature.progress.domain.usecase

import com.seriesly.core.common.result.Result
import com.seriesly.core.domain.repository.SeriesProgressRepository
import javax.inject.Inject

class MarkEpisodeWatchedUseCase @Inject constructor(private val repo: SeriesProgressRepository) {
    suspend operator fun invoke(userId: Long, tvdbId: Int, episodeId: Int, watched: Boolean): Result<Unit> =
        repo.markEpisodeWatched(userId, tvdbId, episodeId, watched)
}
