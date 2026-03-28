package com.seriesly.core.network.dto.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResultDto(
    @Json(name = "tvdb_id")   val tvdbId: String?,
    @Json(name = "objectID")  val objectId: String?,
    @Json(name = "name")      val name: String,
    @Json(name = "type")      val type: String,
    @Json(name = "year")      val year: String?,
    @Json(name = "image_url") val imageUrl: String?,
    @Json(name = "poster")    val poster: String?,
    @Json(name = "score")     val score: Float?
) {
    fun resolvedTvdbId(): Int? = (tvdbId ?: objectId)?.toIntOrNull()
}
