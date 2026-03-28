package com.seriesly.feature.progress.domain.usecase

import com.seriesly.core.common.result.Result
import com.seriesly.core.domain.repository.SeriesProgressRepository
import javax.inject.Inject

class MarkSeasonWatchedUseCase @Inject constructor(private val repo: SeriesProgressRepository) {
    suspend operator fun invoke(userId: Long, tvdbId: Int, episodeIds: List<Int>, watched: Boolean): Result<Unit> =
        repo.markSeasonEpisodesWatched(userId, tvdbId, episodeIds, watched)
}
