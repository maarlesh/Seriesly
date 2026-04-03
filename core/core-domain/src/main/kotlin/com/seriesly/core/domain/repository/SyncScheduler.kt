package com.seriesly.core.domain.repository

interface SyncScheduler {
    fun schedulePendingSync()
}
