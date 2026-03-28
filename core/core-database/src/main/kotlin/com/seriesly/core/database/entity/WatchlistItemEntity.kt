package com.seriesly.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.seriesly.core.common.model.ContentType

@Entity(
    tableName = "watchlist_items",
    foreignKeys = [
        ForeignKey(
            entity = WatchlistEntity::class,
            parentColumns = ["watchlistId"],
            childColumns = ["watchlistId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["watchlistId", "tvdbId", "contentType"], unique = true)
    ]
)
data class WatchlistItemEntity(
    @PrimaryKey(autoGenerate = true)
    val itemId: Long = 0,
    val watchlistId: Long,
    val tvdbId: Int,
    val contentType: ContentType,
    val addedAt: Long
)
