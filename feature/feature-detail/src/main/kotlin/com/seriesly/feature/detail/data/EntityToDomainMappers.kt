package com.seriesly.feature.detail.data

import com.seriesly.core.database.entity.EpisodeEntity
import com.seriesly.core.database.entity.MovieEntity
import com.seriesly.core.database.entity.SeasonEntity
import com.seriesly.core.database.entity.SeriesEntity
import com.seriesly.core.domain.model.Episode
import com.seriesly.core.domain.model.Movie
import com.seriesly.core.domain.model.Season
import com.seriesly.core.domain.model.Series
import com.seriesly.core.domain.model.SeriesStatus

fun MovieEntity.toDomain(): Movie = Movie(
    tvdbId         = tvdbId,
    title          = title,
    overview       = overview,
    year           = year,
    runtimeMinutes = runtimeMinutes,
    genres         = genres,
    posterUrl      = posterUrl,
    backdropUrl    = backdropUrl,
    tvdbRating     = tvdbRating,
    status         = status
)

fun EpisodeEntity.toDomain(isWatched: Boolean = false): Episode = Episode(
    episodeId      = episodeId,
    seasonId       = seasonId,
    episodeNumber  = episodeNumber,
    title          = title,
    overview       = overview,
    airDate        = airDate,
    runtimeMinutes = runtimeMinutes,
    stillUrl       = stillUrl,
    isWatched      = isWatched
)

fun SeriesEntity.toDomain(
    seasons: List<SeasonEntity>,
    episodesBySeason: Map<Int, List<EpisodeEntity>>
): Series = Series(
    tvdbId        = tvdbId,
    title         = title,
    overview      = overview,
    firstAired    = firstAired,
    status        = when (status) {
        "Continuing" -> SeriesStatus.CONTINUING
        "Ended"      -> SeriesStatus.ENDED
        else         -> SeriesStatus.UNKNOWN
    },
    genres        = genres,
    posterUrl     = posterUrl,
    backdropUrl   = backdropUrl,
    totalSeasons  = totalSeasons ?: 0,
    totalEpisodes = totalEpisodes ?: 0,
    tvdbRating    = tvdbRating,
    nextAiredDate = nextAiredDate,
    seasons       = seasons.map { s ->
        val eps = episodesBySeason[s.seasonId] ?: emptyList()
        Season(
            seasonId      = s.seasonId,
            seasonNumber  = s.seasonNumber,
            episodeCount  = eps.size,
            episodes      = eps.map { it.toDomain() }
        )
    }
)
