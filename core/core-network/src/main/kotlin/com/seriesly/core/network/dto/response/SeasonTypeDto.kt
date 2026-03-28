package com.seriesly.core.network.dto.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SeasonTypeDto(
    @Json(name = "id")   val id: Int?,
    @Json(name = "type") val type: String?   // "official", "dvd", "absolute", etc.
)
