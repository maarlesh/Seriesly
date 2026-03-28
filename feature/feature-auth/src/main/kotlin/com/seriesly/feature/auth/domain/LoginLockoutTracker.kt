package com.seriesly.feature.auth.domain

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginLockoutTracker @Inject constructor() {
    private var failureCount = 0
    private var lockoutUntil = 0L
    private val maxAttempts = 5
    private val lockoutMs = 30_000L

    fun isLockedOut(): Boolean = System.currentTimeMillis() < lockoutUntil

    fun remainingSeconds(): Long =
        ((lockoutUntil - System.currentTimeMillis()) / 1000).coerceAtLeast(0)

    fun recordFailure() {
        failureCount++
        if (failureCount >= maxAttempts) lockoutUntil = System.currentTimeMillis() + lockoutMs
    }

    fun reset() {
        failureCount = 0
        lockoutUntil = 0L
    }
}
