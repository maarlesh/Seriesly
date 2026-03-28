package com.seriesly.feature.auth.domain.usecase

import com.seriesly.core.common.result.AppException
import com.seriesly.core.common.result.Result
import com.seriesly.feature.auth.domain.AuthRepository
import com.seriesly.feature.auth.domain.LoginLockoutTracker
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repo: AuthRepository,
    private val lockout: LoginLockoutTracker
) {
    suspend operator fun invoke(username: String, password: String): Result<Long> {
        if (lockout.isLockedOut())
            return Result.Error(AppException.AuthException("Too many attempts. Wait ${lockout.remainingSeconds()}s"))
        val result = repo.login(username, password)
        if (result is Result.Error) lockout.recordFailure() else lockout.reset()
        return result
    }
}
