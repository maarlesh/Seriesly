package com.seriesly.core.network.interceptor

import com.seriesly.core.network.TvdbApiKeyProvider
import com.seriesly.core.network.api.TvdbApiService
import com.seriesly.core.network.dto.request.LoginRequestDto
import com.seriesly.core.network.token.TvdbTokenStore
import dagger.Lazy
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Attaches Bearer token to every request.
 * On 401, refreshes the token once and retries.
 * Uses Lazy<TvdbApiService> to break circular DI dependency.
 */
@Singleton
class TvdbAuthInterceptor @Inject constructor(
    private val tokenStore: TvdbTokenStore,
    private val apiService: Lazy<TvdbApiService>,
    private val apiKeyProvider: TvdbApiKeyProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // Skip auth for the login endpoint — avoids recursive token refresh / deadlock
        if (chain.request().url.encodedPath.endsWith("/login")) {
            return chain.proceed(chain.request())
        }
        val token = tokenStore.getToken() ?: refreshToken()
        val response = chain.proceed(withToken(chain, token))
        return if (response.code == 401) {
            Timber.w("401 — refreshing TVDB token")
            response.close()
            chain.proceed(withToken(chain, refreshToken()))
        } else response
    }

    private fun withToken(chain: Interceptor.Chain, token: String) =
        chain.request().newBuilder().header("Authorization", "Bearer $token").build()

    private fun refreshToken(): String {
        val token = apiService.get().loginSync(LoginRequestDto(apiKeyProvider.getKey())).execute()
            .body()?.data?.token
            ?: throw IllegalStateException("TVDB login returned null token — check API key")
        tokenStore.saveToken(token)
        return token
    }
}
