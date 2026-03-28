package com.seriesly.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.seriesly.core.common.model.ContentType

@Entity(
    tableName = "ratings",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId", "tvdbId", "contentType"], unique = true)
    ]
)
data class RatingEntity(
    @PrimaryKey(autoGenerate = true)
    val ratingId: Long = 0,
    val userId: Long,
    val tvdbId: Int,
    val contentType: ContentType,
    val rating: Float,
    val comment: String?,
    val createdAt: Long,
    val updatedAt: Long
)
