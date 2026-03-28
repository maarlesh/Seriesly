package com.seriesly.core.domain.repository

import com.seriesly.core.common.result.Result
import com.seriesly.core.domain.model.SeriesProgress
import kotlinx.coroutines.flow.Flow

interface SeriesProgressRepository {
    fun observeSeriesProgress(userId: Long, tvdbId: Int): Flow<SeriesProgress>
    fun observeEpisodeWatched(userId: Long, tvdbId: Int): Flow<Map<Int, Boolean>>
    suspend fun markEpisodeWatched(userId: Long, tvdbId: Int, episodeId: Int, watched: Boolean): Result<Unit>
    suspend fun markSeasonEpisodesWatched(userId: Long, tvdbId: Int, episodeIds: List<Int>, watched: Boolean): Result<Unit>
    suspend fun markAllAiredWatched(userId: Long, tvdbId: Int, watched: Boolean): Result<Unit>
}
