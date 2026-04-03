package com.seriesly.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.seriesly.core.database.dao.SeriesDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class CacheEvictionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val seriesDao: SeriesDao,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val threshold = System.currentTimeMillis() - THIRTY_DAYS_MS
        seriesDao.pruneStaleEpisodesNotReferenced(threshold)
        seriesDao.pruneStaleSeriesNotReferenced(threshold)
        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "seriesly_cache_eviction"
        private const val THIRTY_DAYS_MS = 30L * 24 * 60 * 60 * 1000

        fun enqueue(context: Context) {
            val request = PeriodicWorkRequestBuilder<CacheEvictionWorker>(7, TimeUnit.DAYS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresDeviceIdle(true)
                        .setRequiresBatteryNotLow(true)
                        .build()
                )
                .build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
        }
    }
}
