package com.seriesly.feature.auth.domain.usecase

import com.seriesly.core.common.result.AppException
import com.seriesly.core.common.result.Result
import com.seriesly.feature.auth.domain.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class RegisterUseCaseTest {
    private val repo = mockk<AuthRepository>()
    private val useCase = RegisterUseCase(repo)

    @Test fun `short username returns validation error`() = runTest {
        val r = useCase("ab", "pass1234", "pass1234")
        assertTrue(r is Result.Error)
        assertTrue((r as Result.Error).exception is AppException.ValidationException)
    }

    @Test fun `username with special chars returns validation error`() = runTest {
        coEvery { repo.isUsernameTaken(any()) } returns false
        val r = useCase("alice!", "pass1234", "pass1234")
        assertTrue(r is Result.Error)
        assertTrue((r as Result.Error).exception is AppException.ValidationException)
    }

    @Test fun `taken username returns validation error`() = runTest {
        coEvery { repo.isUsernameTaken("alice") } returns true
        val r = useCase("alice", "pass1234", "pass1234")
        assertTrue(r is Result.Error)
        assertTrue((r as Result.Error).exception is AppException.ValidationException)
    }

    @Test fun `short password returns validation error`() = runTest {
        coEvery { repo.isUsernameTaken("alice") } returns false
        val r = useCase("alice", "pass", "pass")
        assertTrue(r is Result.Error)
    }

    @Test fun `password without digit returns validation error`() = runTest {
        coEvery { repo.isUsernameTaken("alice") } returns false
        val r = useCase("alice", "password", "password")
        assertTrue(r is Result.Error)
    }

    @Test fun `mismatched passwords returns error`() = runTest {
        coEvery { repo.isUsernameTaken(any()) } returns false
        val r = useCase("alice", "pass1234", "different")
        assertTrue(r is Result.Error)
    }

    @Test fun `valid input calls repo register`() = runTest {
        coEvery { repo.isUsernameTaken("alice") } returns false
        coEvery { repo.register("alice", "pass1234") } returns Result.Success(1L)
        val r = useCase("alice", "pass1234", "pass1234")
        assertTrue(r is Result.Success)
        assertEquals(1L, (r as Result.Success).data)
        coVerify { repo.register("alice", "pass1234") }
    }
}
