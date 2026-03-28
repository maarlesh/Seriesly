package com.seriesly.core.network.dto.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SeriesExtendedDto(
    @Json(name = "id")         val id: Int,
    @Json(name = "name")       val name: String,
    @Json(name = "overview")   val overview: String?,
    @Json(name = "firstAired") val firstAired: String?,
    @Json(name = "status")     val status: StatusDto?,
    @Json(name = "genres")     val genres: List<GenreDto>?,
    @Json(name = "image")      val image: String?,
    @Json(name = "score")      val score: Float?,
    @Json(name = "seasons")    val seasons: List<SeasonDto>?,
    @Json(name = "episodes")   val episodes: List<EpisodeDto>?,
    @Json(name = "nextAired")  val nextAired: String?
)
