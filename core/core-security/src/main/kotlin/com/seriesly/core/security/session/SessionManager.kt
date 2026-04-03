package com.seriesly.core.security.session

import android.content.SharedPreferences
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val encryptedPrefs: SharedPreferences
) {
    companion object {
        private const val KEY_USER_ID      = "active_user_id"
        private const val KEY_TOKEN        = "session_token"
        private const val KEY_LAST_PULL_AT = "last_pull_at"
    }

    fun isLoggedIn(): Boolean = encryptedPrefs.contains(KEY_TOKEN)

    fun saveSession(userId: Long) {
        encryptedPrefs.edit()
            .putLong(KEY_USER_ID, userId)
            .putString(KEY_TOKEN, UUID.randomUUID().toString())
            .apply()
    }

    fun getCurrentUserId(): Long = encryptedPrefs.getLong(KEY_USER_ID, -1L)

    fun clearSession() {
        encryptedPrefs.edit()
            .remove(KEY_USER_ID)
            .remove(KEY_TOKEN)
            .remove(KEY_LAST_PULL_AT)
            .apply()
    }

    fun getLastPullAt(): Long = encryptedPrefs.getLong(KEY_LAST_PULL_AT, 0L)

    fun saveLastPullAt(time: Long) {
        encryptedPrefs.edit().putLong(KEY_LAST_PULL_AT, time).apply()
    }
}
