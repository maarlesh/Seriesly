package com.seriesly.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "series")
data class SeriesEntity(
    @PrimaryKey
    val tvdbId: Int,
    val title: String,
    val overview: String,
    val firstAired: String?,
    val status: String,
    val genres: List<String>,
    val posterUrl: String?,
    val backdropUrl: String?,
    val totalSeasons: Int?,
    val totalEpisodes: Int?,
    val tvdbRating: Float?,
    val nextAiredDate: String?,
    val cachedAt: Long
)
