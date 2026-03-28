package com.seriesly.feature.progress.domain.usecase

import com.seriesly.core.common.model.ContentType
import com.seriesly.core.common.result.Result
import com.seriesly.core.domain.repository.ProgressRepository
import javax.inject.Inject

class DeleteRatingUseCase @Inject constructor(private val repo: ProgressRepository) {
    suspend operator fun invoke(userId: Long, tvdbId: Int, type: ContentType): Result<Unit> =
        repo.deleteRating(userId, tvdbId, type)
}
