package com.seriesly.core.domain.model

data class Season(
    val seasonId: Int,
    val seasonNumber: Int,
    val episodeCount: Int,
    val episodes: List<Episode> = emptyList(),
    val watchedCount: Int = 0
) {
    val isComplete: Boolean get() = watchedCount == episodeCount && episodeCount > 0
    val progressPercent: Float get() = if (episodeCount > 0) watchedCount / episodeCount.toFloat() else 0f
}
