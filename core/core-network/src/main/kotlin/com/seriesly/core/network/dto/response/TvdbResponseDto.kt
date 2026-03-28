package com.seriesly.core.network.dto.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TvdbResponseDto<T>(
    @Json(name = "data")    val data: T?,
    @Json(name = "status")  val status: String,
    @Json(name = "message") val message: String? = null
)
