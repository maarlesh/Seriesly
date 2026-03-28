package com.seriesly.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.seriesly.core.common.model.ContentType

@Entity(
    tableName = "search_cache",
    indices = [
        Index(value = ["queryHash"]),
        Index(value = ["tvdbId"])
    ]
)
data class SearchCacheEntity(
    @PrimaryKey(autoGenerate = true)
    val cacheId: Long = 0,
    val queryHash: String,
    val query: String,
    val tvdbId: Int,
    val title: String,
    val contentType: ContentType,
    val year: Int?,
    val posterUrl: String?,
    val tvdbRating: Float?,
    val cachedAt: Long
)
