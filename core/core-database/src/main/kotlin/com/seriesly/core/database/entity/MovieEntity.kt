package com.seriesly.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey
    val tvdbId: Int,
    val title: String,
    val overview: String,
    val year: Int?,
    val runtimeMinutes: Int?,
    val genres: List<String>,
    val posterUrl: String?,
    val backdropUrl: String?,
    val tvdbRating: Float?,
    val status: String?,
    val cachedAt: Long
)
