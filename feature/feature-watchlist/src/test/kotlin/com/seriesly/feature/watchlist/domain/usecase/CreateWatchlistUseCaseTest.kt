package com.seriesly.feature.watchlist.domain.usecase

import com.seriesly.core.common.result.AppException
import com.seriesly.core.common.result.Result
import com.seriesly.core.domain.repository.WatchlistRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class CreateWatchlistUseCaseTest {

    private val repo = mockk<WatchlistRepository>()
    private val useCase = CreateWatchlistUseCase(repo)

    @Test
    fun `blank name returns validation error`() = runTest {
        val r = useCase(1L, "  ")
        assertTrue(r is Result.Error)
        assertTrue((r as Result.Error).exception is AppException.ValidationException)
        coVerify(exactly = 0) { repo.create(any(), any()) }
    }

    @Test
    fun `name over 50 chars returns validation error`() = runTest {
        val r = useCase(1L, "a".repeat(51))
        assertTrue(r is Result.Error)
        assertTrue((r as Result.Error).exception is AppException.ValidationException)
        coVerify(exactly = 0) { repo.create(any(), any()) }
    }

    @Test
    fun `duplicate name returns validation error`() = runTest {
        coEvery { repo.nameExistsForUser(1L, "My List") } returns true
        val r = useCase(1L, "My List")
        assertTrue(r is Result.Error)
        assertTrue((r as Result.Error).exception is AppException.ValidationException)
        coVerify(exactly = 0) { repo.create(any(), any()) }
    }

    @Test
    fun `over 20 watchlists returns validation error`() = runTest {
        coEvery { repo.nameExistsForUser(any(), any()) } returns false
        coEvery { repo.countForUser(1L) } returns 20
        val r = useCase(1L, "New List")
        assertTrue(r is Result.Error)
        assertTrue((r as Result.Error).exception is AppException.ValidationException)
        coVerify(exactly = 0) { repo.create(any(), any()) }
    }

    @Test
    fun `valid input calls repo create and returns success`() = runTest {
        coEvery { repo.nameExistsForUser(1L, "Movies") } returns false
        coEvery { repo.countForUser(1L) } returns 3
        coEvery { repo.create(1L, "Movies") } returns Result.Success(10L)
        val r = useCase(1L, "Movies")
        assertTrue(r is Result.Success)
        assertEquals(10L, (r as Result.Success).data)
        coVerify(exactly = 1) { repo.create(1L, "Movies") }
    }

    @Test
    fun `exactly 50 char name is valid`() = runTest {
        val name = "a".repeat(50)
        coEvery { repo.nameExistsForUser(1L, name) } returns false
        coEvery { repo.countForUser(1L) } returns 0
        coEvery { repo.create(1L, name) } returns Result.Success(1L)
        val r = useCase(1L, name)
        assertTrue(r is Result.Success)
    }
}
