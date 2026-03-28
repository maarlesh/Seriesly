package com.seriesly.core.domain.model

import com.seriesly.core.common.model.ContentType

data class Watchlist(
    val watchlistId: Long,
    val name: String,
    val isDefault: Boolean,
    val sortOrder: Int,
    val movieCount: Int = 0,
    val seriesCount: Int = 0
)

data class WatchlistItem(
    val itemId: Long,
    val watchlistId: Long,
    val tvdbId: Int,
    val contentType: ContentType,
    val addedAt: Long
)
