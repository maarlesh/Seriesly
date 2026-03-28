package com.seriesly.feature.auth.domain.usecase

import com.seriesly.feature.auth.domain.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke() = repo.logout()
}
