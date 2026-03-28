package com.seriesly.feature.search.data

import com.seriesly.core.common.model.ContentType
import com.seriesly.core.common.result.AppException
import com.seriesly.core.common.result.Result
import com.seriesly.core.database.dao.SearchCacheDao
import com.seriesly.core.database.entity.SearchCacheEntity
import com.seriesly.core.domain.model.ContentFilter
import com.seriesly.core.domain.model.ContentItem
import com.seriesly.core.domain.model.Movie
import com.seriesly.core.domain.model.Series
import com.seriesly.core.domain.repository.SearchRepository
import com.seriesly.core.network.api.TvdbApiService
import com.seriesly.core.network.mapper.toEntity
import com.seriesly.core.network.util.safeApiCall
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

private const val CACHE_TTL_MS = 24 * 60 * 60 * 1000L

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val searchCacheDao: SearchCacheDao,
    private val apiService: TvdbApiService
) : SearchRepository {

    override fun search(query: String, filter: ContentFilter): Flow<Result<List<ContentItem>>> = flow {
        emit(Result.Loading)
        try {
            val hash = CacheKeyGenerator.forQuery(query)
            val now = System.currentTimeMillis()
            val cached = searchCacheDao.getByQueryHash(hash)

            if (cached.isNotEmpty()) {
                emit(Result.Success(cached.toContentItems(filter)))
            }

            val fresh = cached.isEmpty() || (now - cached.first().cachedAt) >= CACHE_TTL_MS

            if (fresh) {
                val networkResult = safeApiCall(
                    tag = "Search",
                    apiCall = { apiService.search(query = query, type = filter.toApiType()) },
                    mapper = { list -> list.mapNotNull { it.toEntity(query, hash, now) } }
                )
                when (networkResult) {
                    is Result.Success -> {
                        searchCacheDao.deleteByQueryHash(hash)
                        searchCacheDao.insertAll(networkResult.data)
                        searchCacheDao.evictExcess()
                        emit(Result.Success(networkResult.data.toContentItems(filter)))
                    }
                    is Result.Error -> if (cached.isEmpty()) emit(networkResult)
                    else -> {}
                }
            }
        } catch (e: Exception) {
            Log.e("SearchRepository", "Search flow error for query='$query'", e)
            emit(Result.Error(AppException.UnknownException(e)))
        }
    }.flowOn(Dispatchers.IO)

    // Stubbed — implemented in TASK-07 (feature-detail)
    override suspend fun getMovieDetail(tvdbId: Int): Result<Movie> =
        Result.Error(AppException.ApiException(501, "Not implemented"))

    override suspend fun getSeriesDetail(tvdbId: Int): Result<Series> =
        Result.Error(AppException.ApiException(501, "Not implemented"))

    override suspend fun refreshMovie(tvdbId: Int): Result<Movie> =
        Result.Error(AppException.ApiException(501, "Not implemented"))

    override suspend fun refreshSeries(tvdbId: Int): Result<Series> =
        Result.Error(AppException.ApiException(501, "Not implemented"))

    private fun ContentFilter.toApiType(): String? = when (this) {
        ContentFilter.MOVIES -> "movie"
        ContentFilter.SERIES -> "series"
        ContentFilter.ALL    -> null
    }

    private fun List<SearchCacheEntity>.toContentItems(filter: ContentFilter) =
        filter { filter == ContentFilter.ALL || it.contentType.matches(filter) }
            .map { it.toContentItem() }

    private fun ContentType.matches(filter: ContentFilter) =
        (filter == ContentFilter.MOVIES && this == ContentType.MOVIE) ||
        (filter == ContentFilter.SERIES && this == ContentType.SERIES)
}

private fun SearchCacheEntity.toContentItem() = ContentItem(
    tvdbId      = tvdbId,
    title       = title,
    contentType = contentType,
    year        = year,
    posterUrl   = posterUrl,
    tvdbRating  = tvdbRating
)
