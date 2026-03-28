package com.seriesly.feature.watchlist.domain.usecase

import com.seriesly.core.common.result.AppException
import com.seriesly.core.common.result.Result
import com.seriesly.core.domain.model.Watchlist
import com.seriesly.core.domain.repository.WatchlistRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class DeleteWatchlistUseCaseTest {

    private val repo = mockk<WatchlistRepository>()
    private val useCase = DeleteWatchlistUseCase(repo)

    private fun watchlist(isDefault: Boolean) = Watchlist(
        watchlistId = 1L, name = "To Watch Later", isDefault = isDefault, sortOrder = 0
    )

    @Test
    fun `default watchlist cannot be deleted`() = runTest {
        val r = useCase(watchlist(isDefault = true))
        assertTrue(r is Result.Error)
        assertTrue((r as Result.Error).exception is AppException.ValidationException)
        coVerify(exactly = 0) { repo.delete(any()) }
    }

    @Test
    fun `non-default watchlist is deleted`() = runTest {
        coEvery { repo.delete(1L) } returns Result.Success(Unit)
        val r = useCase(watchlist(isDefault = false))
        assertTrue(r is Result.Success)
        coVerify(exactly = 1) { repo.delete(1L) }
    }

    @Test
    fun `repo error is propagated`() = runTest {
        coEvery { repo.delete(1L) } returns Result.Error(AppException.DatabaseException("fail", Exception()))
        val r = useCase(watchlist(isDefault = false))
        assertTrue(r is Result.Error)
    }
}
