package com.seriesly.core.common.result

sealed class AppException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NetworkException(message: String = "Network error") : AppException(message)
    class ApiException(val code: Int, message: String) : AppException(message)
    class AuthException(message: String = "Authentication failed") : AppException(message)
    class DatabaseException(message: String, cause: Throwable) : AppException(message, cause)
    class RateLimitException(val retryAfterMs: Long = 60_000L) : AppException("Rate limited")
    class CacheException(message: String = "Cache error") : AppException(message)
    class ValidationException(message: String) : AppException(message)
    class UnknownException(cause: Throwable) : AppException("Unexpected error", cause)
}
