package com.seriesly.feature.auth.domain

import com.seriesly.core.common.result.Result

interface AuthRepository {
    suspend fun register(username: String, password: String): Result<Long>
    suspend fun login(username: String, password: String): Result<Long>
    suspend fun logout()
    suspend fun isUsernameTaken(username: String): Boolean
}
