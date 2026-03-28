package com.seriesly.core.domain.model

import com.seriesly.core.common.model.ContentType

data class ContentItem(
    val tvdbId: Int,
    val title: String,
    val contentType: ContentType,
    val year: Int?,
    val posterUrl: String?,
    val tvdbRating: Float?,
    val isWatched: Boolean = false,
    val userRating: Float? = null
)

enum class ContentFilter { ALL, MOVIES, SERIES }
