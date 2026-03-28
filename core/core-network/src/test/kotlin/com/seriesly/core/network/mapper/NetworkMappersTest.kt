package com.seriesly.core.network.mapper

import com.google.common.truth.Truth.assertThat
import com.seriesly.core.common.model.ContentType
import com.seriesly.core.network.dto.response.*
import org.junit.Test

class NetworkMappersTest {

    private val now = 1_000_000L

    @Test
    fun `SearchResultDto toEntity maps movie type correctly`() {
        val dto = SearchResultDto(
            tvdbId = "123", objectId = null, name = "The Matrix",
            type = "movie", year = "1999", imageUrl = "https://img.png",
            poster = null, score = 8.5f
        )
        val entity = dto.toEntity("matrix", "hash123", now)
        assertThat(entity).isNotNull()
        assertThat(entity!!.tvdbId).isEqualTo(123)
        assertThat(entity.contentType).isEqualTo(ContentType.MOVIE)
        assertThat(entity.title).isEqualTo("The Matrix")
        assertThat(entity.year).isEqualTo(1999)
        assertThat(entity.posterUrl).isEqualTo("https://img.png")
        assertThat(entity.cachedAt).isEqualTo(now)
    }

    @Test
    fun `SearchResultDto toEntity maps series type correctly`() {
        val dto = SearchResultDto(
            tvdbId = null, objectId = "456", name = "Breaking Bad",
            type = "series", year = "2008", imageUrl = null,
            poster = "https://poster.png", score = 9.5f
        )
        val entity = dto.toEntity("breaking", "hash456", now)
        assertThat(entity).isNotNull()
        assertThat(entity!!.tvdbId).isEqualTo(456)
        assertThat(entity.contentType).isEqualTo(ContentType.SERIES)
        assertThat(entity.posterUrl).isEqualTo("https://poster.png")
    }

    @Test
    fun `SearchResultDto toEntity returns null when no valid tvdbId`() {
        val dto = SearchResultDto(
            tvdbId = null, objectId = null, name = "Unknown",
            type = "movie", year = null, imageUrl = null,
            poster = null, score = null
        )
        assertThat(dto.toEntity("q", "h", now)).isNull()
    }

    @Test
    fun `SearchResultDto toEntity returns null when tvdbId is non-numeric`() {
        val dto = SearchResultDto(
            tvdbId = "abc", objectId = null, name = "Bad ID",
            type = "movie", year = null, imageUrl = null,
            poster = null, score = null
        )
        assertThat(dto.toEntity("q", "h", now)).isNull()
    }

    @Test
    fun `MovieExtendedDto toEntity maps all fields`() {
        val dto = MovieExtendedDto(
            id = 100, name = "Inception", overview = "Dream heist",
            year = 2010, runtime = 148,
            genres = listOf(GenreDto("Sci-Fi"), GenreDto("Thriller")),
            image = "https://poster.jpg",
            artworks = listOf(ArtworkDto(type = 3, image = "https://backdrop.jpg")),
            score = 8.8f, status = StatusDto("Released")
        )
        val entity = dto.toEntity(now)
        assertThat(entity.tvdbId).isEqualTo(100)
        assertThat(entity.title).isEqualTo("Inception")
        assertThat(entity.genres).containsExactly("Sci-Fi", "Thriller")
        assertThat(entity.backdropUrl).isEqualTo("https://backdrop.jpg")
        assertThat(entity.runtimeMinutes).isEqualTo(148)
        assertThat(entity.cachedAt).isEqualTo(now)
    }

    @Test
    fun `MovieExtendedDto toEntity handles null optional fields`() {
        val dto = MovieExtendedDto(
            id = 200, name = "Short Film", overview = null,
            year = null, runtime = null, genres = null,
            image = null, artworks = null, score = null, status = null
        )
        val entity = dto.toEntity(now)
        assertThat(entity.overview).isEmpty()
        assertThat(entity.genres).isEmpty()
        assertThat(entity.backdropUrl).isNull()
    }

    @Test
    fun `SeriesExtendedDto toEntity normalises Continuing status`() {
        val dto = SeriesExtendedDto(
            id = 300, name = "The Crown", overview = "Royal drama",
            firstAired = "2016-11-04",
            status = StatusDto("Continuing"), genres = listOf(GenreDto("Drama")),
            image = "https://crown.jpg", score = 8.7f,
            seasons = listOf(SeasonDto(1, 1, null, listOf()), SeasonDto(2, 2, null, listOf())),
            episodes = null,
            nextAired = "2024-01-01"
        )
        val entity = dto.toEntity(now)
        assertThat(entity.status).isEqualTo("Continuing")
        assertThat(entity.totalSeasons).isEqualTo(2)
    }

    @Test
    fun `SeriesExtendedDto toEntity normalises Ended status`() {
        val dto = SeriesExtendedDto(
            id = 400, name = "Breaking Bad", overview = null,
            firstAired = null, status = StatusDto("Ended"), genres = null,
            image = null, score = null, seasons = null, episodes = null, nextAired = null
        )
        assertThat(dto.toEntity(now).status).isEqualTo("Ended")
    }

    @Test
    fun `EpisodeDto toEntity maps correctly`() {
        val dto = EpisodeDto(
            id = 500, number = 3, seasonNumber = 1, name = "Pilot",
            overview = "First episode", aired = "2008-01-15",
            runtime = 47, image = "https://still.jpg"
        )
        val entity = dto.toEntity(seasonId = 10, seriesTvdbId = 300, now = now)
        assertThat(entity.episodeId).isEqualTo(500)
        assertThat(entity.episodeNumber).isEqualTo(3)
        assertThat(entity.seasonId).isEqualTo(10)
        assertThat(entity.airDate).isEqualTo("2008-01-15")
    }
}
