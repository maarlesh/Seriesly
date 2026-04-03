package com.seriesly.core.network.dto.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TvdbResponseDto<T>(
    @Json(name = "data")    val data: T?,
    @Json(name = "status")  val status: String,
    @Json(name = "message") val message: String? = null,
    @Json(name = "links")   val links: PaginationLinksDto? = null
)

@JsonClass(generateAdapter = true)
data class PaginationLinksDto(
    @Json(name = "prev")        val prev: String? = null,
    @Json(name = "self")        val self: String? = null,
    @Json(name = "next")        val next: String? = null,
    @Json(name = "total_items") val totalItems: Int? = null,
    @Json(name = "page_size")   val pageSize: Int? = null
)
