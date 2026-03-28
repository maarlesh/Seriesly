package com.seriesly.feature.progress.domain.usecase

import com.seriesly.core.common.model.ContentType
import com.seriesly.core.common.result.AppException
import com.seriesly.core.common.result.Result
import com.seriesly.core.domain.repository.ProgressRepository
import com.seriesly.feature.progress.domain.RatingValidator
import javax.inject.Inject

class UpsertRatingUseCase @Inject constructor(private val repo: ProgressRepository) {
    suspend operator fun invoke(
        userId: Long, tvdbId: Int, type: ContentType, rating: Float, comment: String?
    ): Result<Unit> {
        if (!RatingValidator.isValid(rating))
            return Result.Error(AppException.ValidationException("Rating must be between 0.5 and 5.0 in 0.5 steps"))
        if ((comment?.length ?: 0) > RatingValidator.MAX_COMMENT_LENGTH)
            return Result.Error(AppException.ValidationException("Comment too long"))
        return repo.upsertRating(userId, tvdbId, type, rating, comment)
    }
}
