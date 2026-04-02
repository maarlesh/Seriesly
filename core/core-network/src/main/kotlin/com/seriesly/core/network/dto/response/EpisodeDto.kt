package com.seriesly.core.network.dto.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EpisodeDto(
    @Json(name = "id")           val id: Int,
    @Json(name = "number")       val number: Int,
    @Json(name = "seasonNumber") val seasonNumber: Int?,
    @Json(name = "name")         val name: String?,
    @Json(name = "overview")     val overview: String?,
    @Json(name = "aired")        val aired: String?,
    @Json(name = "runtime")      val runtime: Int?,
    @Json(name = "image")        val image: String?,
    @Json(name = "translations") val translations: EpisodeTranslationsDto?
)

@JsonClass(generateAdapter = true)
data class EpisodeTranslationsDto(
    @Json(name = "nameTranslations")     val nameTranslations: List<NameTranslationDto>?,
    @Json(name = "overviewTranslations") val overviewTranslations: List<OverviewTranslationDto>?
)
