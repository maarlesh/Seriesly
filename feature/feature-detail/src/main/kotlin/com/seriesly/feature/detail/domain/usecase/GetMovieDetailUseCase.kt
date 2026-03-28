package com.seriesly.feature.detail.domain.usecase

import com.seriesly.core.common.result.Result
import com.seriesly.core.domain.model.Movie
import com.seriesly.core.domain.repository.ContentDetailRepository
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val repo: ContentDetailRepository
) {
    suspend operator fun invoke(tvdbId: Int): Result<Movie> = repo.fetchMovieDetail(tvdbId)
}
