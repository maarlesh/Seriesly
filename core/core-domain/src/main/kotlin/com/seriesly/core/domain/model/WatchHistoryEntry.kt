package com.seriesly.core.domain.model

import com.seriesly.core.common.model.ContentType

data class WatchHistoryEntry(
    val tvdbId: Int,
    val contentType: ContentType,
    val title: String,
    val posterUrl: String?,
    val watchedAt: Long,
    val rating: Float?
)
