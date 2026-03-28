package com.seriesly.core.domain.model

data class Series(
    val tvdbId: Int,
    val title: String,
    val overview: String,
    val firstAired: String?,
    val status: SeriesStatus,
    val genres: List<String>,
    val posterUrl: String?,
    val backdropUrl: String?,
    val totalSeasons: Int,
    val totalEpisodes: Int,
    val tvdbRating: Float?,
    val nextAiredDate: String?,
    val seasons: List<Season> = emptyList(),
    val watchedEpisodes: Int = 0,
    val userRating: Float? = null
)

enum class SeriesStatus { CONTINUING, ENDED, UNKNOWN }

enum class SeriesWatchStatus { NOT_STARTED, IN_PROGRESS, WATCHED }
