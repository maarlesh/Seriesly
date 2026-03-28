package com.seriesly.feature.detail.data

import com.seriesly.core.common.result.Result
import com.seriesly.core.database.dao.MovieDao
import com.seriesly.core.database.dao.SeriesDao
import com.seriesly.core.domain.model.Movie
import com.seriesly.core.domain.model.Season
import com.seriesly.core.domain.model.Series
import com.seriesly.core.domain.repository.ContentDetailRepository
import com.seriesly.core.network.api.TvdbApiService
import com.seriesly.core.network.mapper.toEntity
import com.seriesly.core.network.util.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private const val DETAIL_CACHE_TTL_MS = 7 * 24 * 60 * 60 * 1000L

@Singleton
class DetailRepositoryImpl @Inject constructor(
    private val movieDao: MovieDao,
    private val seriesDao: SeriesDao,
    private val apiService: TvdbApiService
) : ContentDetailRepository {

    override suspend fun fetchMovieDetail(tvdbId: Int): Result<Movie> = withContext(Dispatchers.IO) {
        val cached = movieDao.getById(tvdbId)
        val now    = System.currentTimeMillis()

        if (cached != null && now - cached.cachedAt < DETAIL_CACHE_TTL_MS)
            return@withContext Result.Success(cached.toDomain())

        val networkResult = safeApiCall(
            tag     = "MovieDetail",
            apiCall = { apiService.getMovieExtended(tvdbId) },
            mapper  = { it.toEntity(now) }
        )
        when (networkResult) {
            is Result.Success -> {
                movieDao.upsert(networkResult.data)
                Result.Success(networkResult.data.toDomain())
            }
            is Result.Error   -> if (cached != null) Result.Success(cached.toDomain()) else networkResult
            is Result.Loading -> Result.Loading
        }
    }

    override suspend fun fetchSeriesDetail(tvdbId: Int): Result<Series> = withContext(Dispatchers.IO) {
        val cached   = seriesDao.getById(tvdbId)
        val seasons  = seriesDao.getSeasons(tvdbId)
        val allEps   = seriesDao.getAllEpisodes(tvdbId)
        val now      = System.currentTimeMillis()

        if (cached != null && now - cached.cachedAt < DETAIL_CACHE_TTL_MS
                && seasons.isNotEmpty() && allEps.isNotEmpty()) {
            val epsBySeason = allEps.groupBy { it.seasonId }
            return@withContext Result.Success(cached.toDomain(seasons, epsBySeason))
        }

        val networkResult = safeApiCall(
            tag     = "SeriesDetail",
            apiCall = { apiService.getSeriesExtended(tvdbId) },
            mapper  = { dto ->
                val seriesEntity    = dto.toEntity(now)
                val allSeasons      = dto.seasons ?: emptyList()
                val officialSeasons = allSeasons.filter {
                    it.type?.type?.equals("official", ignoreCase = true) == true
                }.ifEmpty { allSeasons }  // fall back to all if no type info
                val seasonEntities  = officialSeasons.map { s -> s.toEntity(tvdbId, now) }
                val seasonNumToId   = officialSeasons.associate { it.number to it.id }
                val episodeEntities = dto.episodes?.mapNotNull { e ->
                    val seasonId = e.seasonNumber?.let { seasonNumToId[it] } ?: return@mapNotNull null
                    e.toEntity(seasonId, tvdbId, now)
                } ?: emptyList()
                Triple(seriesEntity, seasonEntities, episodeEntities)
            }
        )
        when (networkResult) {
            is Result.Success -> {
                val (series, seasonList, episodes) = networkResult.data
                seriesDao.upsert(series)
                seriesDao.upsertSeasons(seasonList)
                seriesDao.upsertEpisodes(episodes)
                val updatedSeasons = seriesDao.getSeasons(tvdbId)
                val allEps         = seriesDao.getAllEpisodes(tvdbId)
                val epsBySeason    = allEps.groupBy { it.seasonId }
                Result.Success(series.toDomain(updatedSeasons, epsBySeason))
            }
            is Result.Error -> {
                if (cached != null && seasons.isNotEmpty()) {
                    val allEps      = seriesDao.getAllEpisodes(tvdbId)
                    val epsBySeason = allEps.groupBy { it.seasonId }
                    Result.Success(cached.toDomain(seasons, epsBySeason))
                } else networkResult
            }
            is Result.Loading -> Result.Loading
        }
    }

    override suspend fun fetchSeasons(seriesTvdbId: Int): Result<List<Season>> =
        withContext(Dispatchers.IO) {
            try {
                val seasons     = seriesDao.getSeasons(seriesTvdbId)
                val allEps      = seriesDao.getAllEpisodes(seriesTvdbId)
                val epsBySeason = allEps.groupBy { it.seasonId }
                Result.Success(seasons.map { s ->
                    val eps = epsBySeason[s.seasonId] ?: emptyList()
                    Season(
                        seasonId     = s.seasonId,
                        seasonNumber = s.seasonNumber,
                        episodeCount = eps.size,
                        episodes     = eps.map { it.toDomain() }
                    )
                })
            } catch (e: Exception) {
                Result.Error(com.seriesly.core.common.result.AppException.DatabaseException("fetchSeasons failed", e))
            }
        }

    override fun observeMovie(tvdbId: Int): Flow<Movie?> =
        movieDao.observeById(tvdbId)
            .map { it?.toDomain() }
            .flowOn(Dispatchers.IO)

    override fun observeSeries(tvdbId: Int): Flow<Series?> =
        combine(
            seriesDao.observeById(tvdbId),
            seriesDao.observeSeasons(tvdbId)
        ) { entity, seasons -> entity to seasons }
        .map { (entity, seasons) ->
            entity?.let {
                val allEps   = seriesDao.getAllEpisodes(tvdbId)
                val epsBySeason = allEps.groupBy { it.seasonId }
                it.toDomain(seasons, epsBySeason)
            }
        }
        .flowOn(Dispatchers.IO)

    override fun observeSeasons(seriesTvdbId: Int): Flow<List<Season>> =
        seriesDao.observeSeasons(seriesTvdbId)
            .map { seasons ->
                val allEps  = seriesDao.getAllEpisodes(seriesTvdbId)
                val epsBySeason = allEps.groupBy { it.seasonId }
                seasons.map { s ->
                    val eps = epsBySeason[s.seasonId] ?: emptyList()
                    Season(
                        seasonId     = s.seasonId,
                        seasonNumber = s.seasonNumber,
                        episodeCount = eps.size,
                        episodes     = eps.map { it.toDomain() }
                    )
                }
            }
            .flowOn(Dispatchers.IO)
}
