package com.seriesly.core.network.dto.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SeasonDto(
    @Json(name = "id")       val id: Int,
    @Json(name = "number")   val number: Int,
    @Json(name = "type")     val type: SeasonTypeDto?,
    @Json(name = "episodes") val episodes: List<EpisodeDto>?
)
