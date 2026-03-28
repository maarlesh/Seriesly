package com.seriesly.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "episodes",
    foreignKeys = [
        ForeignKey(
            entity = SeasonEntity::class,
            parentColumns = ["seasonId"],
            childColumns = ["seasonId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("seasonId"), Index("seriesTvdbId")]
)
data class EpisodeEntity(
    @PrimaryKey
    val episodeId: Int,
    val seasonId: Int,
    val seriesTvdbId: Int,
    val episodeNumber: Int,
    val title: String?,
    val overview: String?,
    val airDate: String?,
    val runtimeMinutes: Int?,
    val stillUrl: String?,
    val cachedAt: Long
)
