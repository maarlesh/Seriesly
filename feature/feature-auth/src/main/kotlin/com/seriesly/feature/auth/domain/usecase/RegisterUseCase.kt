package com.seriesly.feature.auth.domain.usecase

import com.seriesly.core.common.result.AppException
import com.seriesly.core.common.result.Result
import com.seriesly.feature.auth.domain.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(username: String, password: String, confirm: String): Result<Long> {
        if (username.length < 3)
            return Result.Error(AppException.ValidationException("Username must be at least 3 characters"))
        if (!username.matches(Regex("^[a-zA-Z0-9_]{3,30}\$")))
            return Result.Error(AppException.ValidationException("Username: letters, numbers and underscore only"))
        if (repo.isUsernameTaken(username))
            return Result.Error(AppException.ValidationException("Username is already taken"))
        if (password.length < 8)
            return Result.Error(AppException.ValidationException("Password must be at least 8 characters"))
        if (!password.any { it.isDigit() })
            return Result.Error(AppException.ValidationException("Password must contain at least one number"))
        if (password != confirm)
            return Result.Error(AppException.ValidationException("Passwords do not match"))
        return repo.register(username, password)
    }
}
