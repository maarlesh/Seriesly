package com.seriesly.core.network.util

import com.seriesly.core.common.result.AppException
import com.seriesly.core.common.result.Result
import com.seriesly.core.network.dto.response.TvdbResponseDto
import kotlinx.coroutines.delay
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

private const val MAX_RETRIES = 3

suspend fun <T, R> safeApiCall(
    tag: String = "API",
    apiCall: suspend () -> TvdbResponseDto<T>,
    mapper: (T) -> R
): Result<R> = safeApiCallWithRetry(tag, 0, apiCall, mapper)

private suspend fun <T, R> safeApiCallWithRetry(
    tag: String,
    attempt: Int,
    apiCall: suspend () -> TvdbResponseDto<T>,
    mapper: (T) -> R
): Result<R> = try {
    val response = apiCall()
    val data = response.data
    if (data != null) Result.Success(mapper(data))
    else Result.Error(AppException.ApiException(0, response.message ?: "Empty response"))
} catch (e: HttpException) {
    Timber.tag(tag).e("HTTP ${e.code()}")
    when (e.code()) {
        401 -> Result.Error(AppException.AuthException())
        404 -> Result.Error(AppException.ApiException(404, "Not found"))
        429 -> {
            val retryAfter = e.response()?.headers()?.get("Retry-After")?.toLongOrNull() ?: 60L
            if (attempt < MAX_RETRIES) {
                delay(retryAfter * 1000)
                safeApiCallWithRetry(tag, attempt + 1, apiCall, mapper)
            } else Result.Error(AppException.RateLimitException(retryAfter * 1000))
        }
        in 500..599 -> {
            val backoff = (1L shl attempt) * 1000
            if (attempt < MAX_RETRIES) {
                delay(backoff)
                safeApiCallWithRetry(tag, attempt + 1, apiCall, mapper)
            } else Result.Error(AppException.ApiException(e.code(), "Server error"))
        }
        else -> Result.Error(AppException.ApiException(e.code(), e.message()))
    }
} catch (e: IOException) {
    Result.Error(AppException.NetworkException(e.message ?: "No internet"))
} catch (e: Exception) {
    Result.Error(AppException.UnknownException(e))
}
