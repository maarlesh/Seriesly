package com.seriesly.core.common.extension

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import timber.log.Timber

fun <T> Flow<T>.catchAndLog(tag: String = "Flow"): Flow<T> = catch { e ->
    Timber.tag(tag).e(e, "Unhandled flow error")
}
