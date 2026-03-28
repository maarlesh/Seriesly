package com.seriesly.core.network.api

import com.seriesly.core.network.dto.request.LoginRequestDto
import com.seriesly.core.network.dto.response.*
import retrofit2.http.*

interface TvdbApiService {

    @POST("login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

    @POST("login")
    fun loginSync(@Body request: LoginRequestDto): retrofit2.Call<LoginResponseDto>

    @GET("search")
    suspend fun search(
        @Query("query")  query: String,
        @Query("type")   type: String? = null,
        @Query("limit")  limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): TvdbResponseDto<List<SearchResultDto>>

    @GET("movies/{id}/extended")
    suspend fun getMovieExtended(
        @Path("id")    id: Int,
        @Query("meta") meta: String = "translations"
    ): TvdbResponseDto<MovieExtendedDto>

    @GET("series/{id}/extended")
    suspend fun getSeriesExtended(
        @Path("id")     id: Int,
        @Query("meta")  meta: String = "episodes",
        @Query("short") short: Boolean = false
    ): TvdbResponseDto<SeriesExtendedDto>

    @GET("series/{id}/seasons/official")
    suspend fun getSeriesSeasons(@Path("id") id: Int): TvdbResponseDto<List<SeasonDto>>

    @GET("seasons/{id}/extended")
    suspend fun getSeasonExtended(@Path("id") id: Int): TvdbResponseDto<SeasonDto>
}
