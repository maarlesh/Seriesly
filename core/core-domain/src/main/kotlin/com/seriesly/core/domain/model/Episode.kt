package com.seriesly.core.domain.model

data class Episode(
    val episodeId: Int,
    val seasonId: Int,
    val episodeNumber: Int,
    val title: String?,
    val overview: String?,
    val airDate: String?,
    val runtimeMinutes: Int?,
    val stillUrl: String?,
    val isWatched: Boolean = false
) {
    fun isAired(today: String): Boolean = airDate != null && airDate <= today
}
