package com.seriesly.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "watchlists",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class WatchlistEntity(
    @PrimaryKey(autoGenerate = true)
    val watchlistId: Long = 0,
    val userId: Long,
    val name: String,
    val isDefault: Boolean = false,
    val sortOrder: Int = 0,
    val createdAt: Long,
    val updatedAt: Long = System.currentTimeMillis(),
    val pendingSync: Boolean = true,
    val syncedAt: Long = 0L
)
