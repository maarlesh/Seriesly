package com.seriesly.core.domain.model

data class Movie(
    val tvdbId: Int,
    val title: String,
    val overview: String,
    val year: Int?,
    val runtimeMinutes: Int?,
    val genres: List<String>,
    val posterUrl: String?,
    val backdropUrl: String?,
    val tvdbRating: Float?,
    val status: String?,
    val isWatched: Boolean = false,
    val userRating: Float? = null,
    val userComment: String? = null
)
