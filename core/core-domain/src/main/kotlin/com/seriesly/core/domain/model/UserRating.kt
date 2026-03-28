package com.seriesly.core.domain.model

import com.seriesly.core.common.model.ContentType

data class UserRating(
    val ratingId: Long,
    val tvdbId: Int,
    val contentType: ContentType,
    val rating: Float,
    val comment: String?,
    val createdAt: Long,
    val updatedAt: Long
)

data class RatedItem(
    val tvdbId: Int,
    val title: String,
    val contentType: ContentType,
    val posterUrl: String?,
    val rating: Float,
    val comment: String?,
    val ratedAt: Long
)
