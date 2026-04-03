package com.seriesly.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.seriesly.core.common.model.ContentType

@Entity(
    tableName = "watch_progress",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("userId"),
        Index(value = ["userId", "tvdbId", "episodeId"], unique = true)
    ]
)
data class WatchProgressEntity(
    @PrimaryKey(autoGenerate = true)
    val progressId: Long = 0,
    val userId: Long,
    val tvdbId: Int,
    val contentType: ContentType,
    val episodeId: Int?,
    val watched: Boolean,
    val watchedAt: Long?,
    val updatedAt: Long = System.currentTimeMillis(),
    val pendingSync: Boolean = true,
    val syncedAt: Long = 0L
)
