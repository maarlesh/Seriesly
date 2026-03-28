package com.seriesly.core.domain.model

data class SeriesProgress(
    val seriesTvdbId: Int,
    val status: SeriesWatchStatus,
    val totalAiredEpisodes: Int,
    val watchedEpisodes: Int
) {
    val progressPercent: Float
        get() = if (totalAiredEpisodes > 0) watchedEpisodes / totalAiredEpisodes.toFloat() else 0f
}
