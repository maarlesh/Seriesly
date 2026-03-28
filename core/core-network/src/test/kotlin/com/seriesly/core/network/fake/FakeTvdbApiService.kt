package com.seriesly.core.network.fake

import com.seriesly.core.network.api.TvdbApiService
import com.seriesly.core.network.dto.request.LoginRequestDto
import com.seriesly.core.network.dto.response.*

class FakeTvdbApiService : TvdbApiService {
    var searchResults: List<SearchResultDto> = emptyList()
    var movieResult: MovieExtendedDto? = null
    var seriesResult: SeriesExtendedDto? = null
    var shouldThrowNetwork = false

    override suspend fun login(request: LoginRequestDto) =
        LoginResponseDto(TokenDataDto("fake-token"))

    override fun loginSync(request: LoginRequestDto): retrofit2.Call<LoginResponseDto> =
        throw UnsupportedOperationException("Not used in tests")

    override suspend fun search(query: String, type: String?, limit: Int, offset: Int) =
        TvdbResponseDto(data = searchResults, status = "success")

    override suspend fun getMovieExtended(id: Int, meta: String) =
        TvdbResponseDto(data = movieResult, status = if (movieResult != null) "success" else "failure")

    override suspend fun getSeriesExtended(id: Int, meta: String, short: Boolean) =
        TvdbResponseDto(data = seriesResult, status = if (seriesResult != null) "success" else "failure")

    override suspend fun getSeriesSeasons(id: Int) =
        TvdbResponseDto(data = seriesResult?.seasons ?: emptyList(), status = "success")

    override suspend fun getSeasonExtended(id: Int) =
        TvdbResponseDto<SeasonDto>(data = null, status = "failure")
}
