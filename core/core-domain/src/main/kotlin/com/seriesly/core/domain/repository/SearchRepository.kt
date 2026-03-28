package com.seriesly.core.domain.repository

import com.seriesly.core.common.result.Result
import com.seriesly.core.domain.model.ContentFilter
import com.seriesly.core.domain.model.ContentItem
import com.seriesly.core.domain.model.Movie
import com.seriesly.core.domain.model.Series
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun search(query: String, filter: ContentFilter): Flow<Result<List<ContentItem>>>
    suspend fun getMovieDetail(tvdbId: Int): Result<Movie>
    suspend fun getSeriesDetail(tvdbId: Int): Result<Series>
    suspend fun refreshMovie(tvdbId: Int): Result<Movie>
    suspend fun refreshSeries(tvdbId: Int): Result<Series>
}
