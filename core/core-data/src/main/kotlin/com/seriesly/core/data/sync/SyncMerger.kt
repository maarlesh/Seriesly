package com.seriesly.core.data.sync

object SyncMerger {
    /**
     * Returns true when the remote version should overwrite the local one.
     * Firestore wins on a strictly greater updatedAt; ties keep local to
     * avoid overwriting pending offline changes with the same timestamp.
     */
    fun remoteWins(localUpdatedAt: Long, remoteUpdatedAt: Long): Boolean =
        remoteUpdatedAt > localUpdatedAt
}
