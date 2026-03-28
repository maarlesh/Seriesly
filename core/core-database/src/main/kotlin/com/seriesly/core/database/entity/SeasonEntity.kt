package com.seriesly.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "seasons",
    foreignKeys = [
        ForeignKey(
            entity = SeriesEntity::class,
            parentColumns = ["tvdbId"],
            childColumns = ["seriesTvdbId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("seriesTvdbId")]
)
data class SeasonEntity(
    @PrimaryKey
    val seasonId: Int,
    val seriesTvdbId: Int,
    val seasonNumber: Int,
    val episodeCount: Int,
    val airDate: String?,
    val cachedAt: Long
)
