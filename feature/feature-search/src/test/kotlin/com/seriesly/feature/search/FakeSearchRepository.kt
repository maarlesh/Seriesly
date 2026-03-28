package com.seriesly.feature.search

import com.seriesly.core.common.model.ContentType
import com.seriesly.core.common.result.AppException
import com.seriesly.core.common.result.Result
import com.seriesly.core.domain.model.ContentFilter
import com.seriesly.core.domain.model.ContentItem
import com.seriesly.core.domain.model.Movie
import com.seriesly.core.domain.model.Series
import com.seriesly.core.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeSearchRepository : SearchRepository {

    var shouldError = false
    val results = mutableListOf<ContentItem>()

    override fun search(query: String, filter: ContentFilter): Flow<Result<List<ContentItem>>> = flow {
        emit(Result.Loading)
        if (shouldError) {
            emit(Result.Error(AppException.NetworkException("No connection")))
        } else {
            val filtered = when (filter) {
                ContentFilter.ALL    -> results
                ContentFilter.MOVIES -> results.filter { it.contentType == ContentType.MOVIE }
                ContentFilter.SERIES -> results.filter { it.contentType == ContentType.SERIES }
            }
            emit(Result.Success(filtered))
        }
    }

    override suspend fun getMovieDetail(tvdbId: Int): Result<Movie> =
        Result.Error(AppException.ApiException(501, "Not implemented"))

    override suspend fun getSeriesDetail(tvdbId: Int): Result<Series> =
        Result.Error(AppException.ApiException(501, "Not implemented"))

    override suspend fun refreshMovie(tvdbId: Int): Result<Movie> =
        Result.Error(AppException.ApiException(501, "Not implemented"))

    override suspend fun refreshSeries(tvdbId: Int): Result<Series> =
        Result.Error(AppException.ApiException(501, "Not implemented"))
}
