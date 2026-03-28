package com.seriesly.feature.progress.domain.usecase

import com.seriesly.core.common.model.ContentType
import com.seriesly.core.common.result.AppException
import com.seriesly.core.common.result.Result
import com.seriesly.core.domain.repository.ProgressRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class UpsertRatingUseCaseTest {

    private val repo = mockk<ProgressRepository>()
    private val useCase = UpsertRatingUseCase(repo)

    @Test fun `rating 0_0 returns validation error`() = runTest {
        val r = useCase(1L, 100, ContentType.MOVIE, 0.0f, null)
        assertTrue(r is Result.Error)
        assertTrue((r as Result.Error).exception is AppException.ValidationException)
        coVerify(exactly = 0) { repo.upsertRating(any(), any(), any(), any(), any()) }
    }

    @Test fun `rating 5_5 returns validation error`() = runTest {
        val r = useCase(1L, 100, ContentType.MOVIE, 5.5f, null)
        assertTrue(r is Result.Error)
    }

    @Test fun `rating 0_7 returns validation error`() = runTest {
        val r = useCase(1L, 100, ContentType.MOVIE, 0.7f, null)
        assertTrue(r is Result.Error)
    }

    @Test fun `valid rating 4_5 calls repo`() = runTest {
        coEvery { repo.upsertRating(1L, 100, ContentType.MOVIE, 4.5f, null) } returns Result.Success(Unit)
        val r = useCase(1L, 100, ContentType.MOVIE, 4.5f, null)
        assertTrue(r is Result.Success)
        coVerify(exactly = 1) { repo.upsertRating(1L, 100, ContentType.MOVIE, 4.5f, null) }
    }

    @Test fun `comment over 500 chars returns validation error`() = runTest {
        val r = useCase(1L, 100, ContentType.MOVIE, 3.0f, "x".repeat(501))
        assertTrue(r is Result.Error)
    }
}
