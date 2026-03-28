package com.seriesly.core.network.mapper

import com.seriesly.core.common.model.ContentType
import com.seriesly.core.database.entity.*
import com.seriesly.core.network.dto.response.*

fun SearchResultDto.toEntity(query: String, queryHash: String, now: Long): SearchCacheEntity? {
    val id = resolvedTvdbId() ?: return null
    return SearchCacheEntity(
        queryHash   = queryHash,
        query       = query,
        tvdbId      = id,
        title       = name,
        contentType = if (type.equals("movie", ignoreCase = true)) ContentType.MOVIE else ContentType.SERIES,
        year        = year?.toIntOrNull(),
        posterUrl   = imageUrl ?: poster,
        tvdbRating  = score,
        cachedAt    = now
    )
}

fun MovieExtendedDto.toEntity(now: Long) = MovieEntity(
    tvdbId         = id,
    title          = name,
    overview       = overview.orEmpty(),
    year           = year,
    runtimeMinutes = runtime,
    genres         = genres?.map { it.name } ?: emptyList(),
    posterUrl      = image,
    backdropUrl    = artworks?.firstOrNull { it.type == 3 }?.image,
    tvdbRating     = score,
    status         = status?.name,
    cachedAt       = now
)

fun SeriesExtendedDto.toEntity(now: Long): SeriesEntity {
    val officialSeasons = seasons ?: emptyList()
    return SeriesEntity(
        tvdbId        = id,
        title         = name,
        overview      = overview.orEmpty(),
        firstAired    = firstAired,
        status        = normaliseStatus(status?.name),
        genres        = genres?.map { it.name } ?: emptyList(),
        posterUrl     = image,
        backdropUrl   = null,
        totalSeasons  = officialSeasons.maxOfOrNull { it.number },
        totalEpisodes = officialSeasons.sumOf { it.episodes?.size ?: 0 },
        tvdbRating    = score,
        nextAiredDate = nextAired,
        cachedAt      = now
    )
}

private fun normaliseStatus(raw: String?) = when {
    raw == null -> "Unknown"
    raw.contains("continu", ignoreCase = true) -> "Continuing"
    raw.contains("end", ignoreCase = true)     -> "Ended"
    else -> raw
}

fun SeasonDto.toEntity(seriesTvdbId: Int, now: Long) = SeasonEntity(
    seasonId     = id,
    seriesTvdbId = seriesTvdbId,
    seasonNumber = number,
    episodeCount = episodes?.size ?: 0,
    airDate      = null,
    cachedAt     = now
)

fun EpisodeDto.toEntity(seasonId: Int, seriesTvdbId: Int, now: Long) = EpisodeEntity(
    episodeId      = id,
    seasonId       = seasonId,
    seriesTvdbId   = seriesTvdbId,
    episodeNumber  = number,
    title          = name,
    overview       = overview,
    airDate        = aired,
    runtimeMinutes = runtime,
    stillUrl       = image,
    cachedAt       = now
)
