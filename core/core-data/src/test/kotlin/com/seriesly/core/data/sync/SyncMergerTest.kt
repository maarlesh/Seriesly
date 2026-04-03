package com.seriesly.core.data.sync

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SyncMergerTest {

    @Test
    fun `remoteWins returns true when remote is strictly newer`() {
        assertThat(SyncMerger.remoteWins(localUpdatedAt = 1000L, remoteUpdatedAt = 2000L)).isTrue()
    }

    @Test
    fun `remoteWins returns false when local is newer`() {
        assertThat(SyncMerger.remoteWins(localUpdatedAt = 2000L, remoteUpdatedAt = 1000L)).isFalse()
    }

    @Test
    fun `remoteWins returns false on equal timestamps — keeps local to protect pending offline writes`() {
        assertThat(SyncMerger.remoteWins(localUpdatedAt = 1000L, remoteUpdatedAt = 1000L)).isFalse()
    }

    @Test
    fun `remoteWins returns true when local has never been updated (zero)`() {
        assertThat(SyncMerger.remoteWins(localUpdatedAt = 0L, remoteUpdatedAt = 1L)).isTrue()
    }

    @Test
    fun `remoteWins returns false when both timestamps are zero`() {
        assertThat(SyncMerger.remoteWins(localUpdatedAt = 0L, remoteUpdatedAt = 0L)).isFalse()
    }

    @Test
    fun `remoteWins returns false when remote is zero and local has data`() {
        assertThat(SyncMerger.remoteWins(localUpdatedAt = 500L, remoteUpdatedAt = 0L)).isFalse()
    }
}
