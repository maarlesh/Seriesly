package com.seriesly.core.network.dto.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequestDto(@Json(name = "apikey") val apiKey: String)
