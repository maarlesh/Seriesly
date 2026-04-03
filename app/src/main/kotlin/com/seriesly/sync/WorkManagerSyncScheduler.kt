package com.seriesly.sync

import android.content.Context
import com.seriesly.core.domain.repository.SyncScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class WorkManagerSyncScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) : SyncScheduler {
    override fun schedulePendingSync() {
        SyncWorker.enqueue(context)
    }
}
