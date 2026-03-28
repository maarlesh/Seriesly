package com.seriesly.feature.auth.domain

import org.junit.Assert.*
import org.junit.Test

class LoginLockoutTrackerTest {

    @Test fun `not locked out initially`() {
        val tracker = LoginLockoutTracker()
        assertFalse(tracker.isLockedOut())
        assertEquals(0L, tracker.remainingSeconds())
    }

    @Test fun `locks out after 5 failures`() {
        val tracker = LoginLockoutTracker()
        repeat(5) { tracker.recordFailure() }
        assertTrue(tracker.isLockedOut())
        assertTrue(tracker.remainingSeconds() > 0)
    }

    @Test fun `not locked out after 4 failures`() {
        val tracker = LoginLockoutTracker()
        repeat(4) { tracker.recordFailure() }
        assertFalse(tracker.isLockedOut())
    }

    @Test fun `resets after successful login`() {
        val tracker = LoginLockoutTracker()
        repeat(4) { tracker.recordFailure() }
        tracker.reset()
        assertFalse(tracker.isLockedOut())
        assertEquals(0L, tracker.remainingSeconds())
    }

    @Test fun `reset after lockout clears lockout`() {
        val tracker = LoginLockoutTracker()
        repeat(5) { tracker.recordFailure() }
        tracker.reset()
        assertFalse(tracker.isLockedOut())
    }
}
