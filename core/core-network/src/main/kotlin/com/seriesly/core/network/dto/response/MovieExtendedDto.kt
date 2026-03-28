package com.seriesly.core.network.dto.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MovieExtendedDto(
    @Json(name = "id")       val id: Int,
    @Json(name = "name")     val name: String,
    @Json(name = "overview") val overview: String?,
    @Json(name = "year")     val year: Int?,
    @Json(name = "runtime")  val runtime: Int?,
    @Json(name = "genres")   val genres: List<GenreDto>?,
    @Json(name = "image")    val image: String?,
    @Json(name = "artworks") val artworks: List<ArtworkDto>?,
    @Json(name = "score")    val score: Float?,
    @Json(name = "status")   val status: StatusDto?
)

@JsonClass(generateAdapter = true)
data class GenreDto(@Json(name = "name") val name: String)

@JsonClass(generateAdapter = true)
data class ArtworkDto(
    @Json(name = "type")  val type: Int,
    @Json(name = "image") val image: String
)

@JsonClass(generateAdapter = true)
data class StatusDto(@Json(name = "name") val name: String?)
