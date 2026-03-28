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
        private const val KEY_USER_ID = "active_user_id"
        private const val KEY_TOKEN   = "session_token"
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
            .apply()
    }
}
