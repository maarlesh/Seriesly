package com.seriesly.core.domain.repository

import com.seriesly.core.common.result.Result
import com.seriesly.core.domain.model.Movie
import com.seriesly.core.domain.model.Season
import com.seriesly.core.domain.model.Series
import kotlinx.coroutines.flow.Flow

interface ContentDetailRepository {
    fun observeMovie(tvdbId: Int): Flow<Movie?>
    fun observeSeries(tvdbId: Int): Flow<Series?>
    fun observeSeasons(seriesTvdbId: Int): Flow<List<Season>>
    suspend fun fetchMovieDetail(tvdbId: Int): Result<Movie>
    suspend fun fetchSeriesDetail(tvdbId: Int): Result<Series>
    suspend fun fetchSeasons(seriesTvdbId: Int): Result<List<Season>>
}
