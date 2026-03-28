package com.seriesly.core.network.dto.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponseDto(@Json(name = "data") val data: TokenDataDto)

@JsonClass(generateAdapter = true)
data class TokenDataDto(@Json(name = "token") val token: String)
