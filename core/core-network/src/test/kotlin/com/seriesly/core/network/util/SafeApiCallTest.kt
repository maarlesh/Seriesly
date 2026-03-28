package com.seriesly.core.network.util

import com.google.common.truth.Truth.assertThat
import com.seriesly.core.common.result.AppException
import com.seriesly.core.common.result.Result
import com.seriesly.core.network.dto.response.TvdbResponseDto
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class SafeApiCallTest {

    private fun <T> successResponse(data: T) = TvdbResponseDto(data = data, status = "success")
    private fun emptyResponse() = TvdbResponseDto<String>(data = null, status = "failure", message = "Empty")

    private fun httpException(code: Int): HttpException {
        val body = "error".toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Any>(code, body))
    }

    @Test
    fun `returns Success when data is present`() = runTest {
        val result = safeApiCall(
            apiCall = { successResponse("hello") },
            mapper = { it.uppercase() }
        )
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data).isEqualTo("HELLO")
    }

    @Test
    fun `returns ApiException when data is null`() = runTest {
        val result = safeApiCall(
            apiCall = { emptyResponse() },
            mapper = { it }
        )
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).exception).isInstanceOf(AppException.ApiException::class.java)
    }

    @Test
    fun `returns AuthException on HTTP 401`() = runTest {
        val result = safeApiCall<String, String>(
            apiCall = { throw httpException(401) },
            mapper = { it }
        )
        assertThat((result as Result.Error).exception).isInstanceOf(AppException.AuthException::class.java)
    }

    @Test
    fun `returns ApiException with 404 on HTTP 404`() = runTest {
        val result = safeApiCall<String, String>(
            apiCall = { throw httpException(404) },
            mapper = { it }
        )
        val error = (result as Result.Error).exception as AppException.ApiException
        assertThat(error.code).isEqualTo(404)
    }

    @Test
    fun `returns NetworkException on IOException`() = runTest {
        val result = safeApiCall<String, String>(
            apiCall = { throw IOException("timeout") },
            mapper = { it }
        )
        assertThat((result as Result.Error).exception).isInstanceOf(AppException.NetworkException::class.java)
    }

    @Test
    fun `returns UnknownException on unexpected error`() = runTest {
        val result = safeApiCall<String, String>(
            apiCall = { throw RuntimeException("boom") },
            mapper = { it }
        )
        assertThat((result as Result.Error).exception).isInstanceOf(AppException.UnknownException::class.java)
    }
}
