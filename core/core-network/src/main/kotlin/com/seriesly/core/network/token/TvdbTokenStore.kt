package com.seriesly.core.network.token

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TvdbTokenStore @Inject constructor(private val encryptedPrefs: SharedPreferences) {
    companion object {
        private const val KEY_TOKEN    = "tvdb_bearer_token"
        private const val KEY_SAVED_AT = "tvdb_token_saved_at"
        private const val TTL_MS       = 29L * 24 * 60 * 60 * 1000  // 29 days
    }

    fun getToken(): String? {
        val savedAt = encryptedPrefs.getLong(KEY_SAVED_AT, 0L)
        val expired = System.currentTimeMillis() - savedAt > TTL_MS
        return if (expired) null else encryptedPrefs.getString(KEY_TOKEN, null)
    }

    fun saveToken(token: String) {
        encryptedPrefs.edit()
            .putString(KEY_TOKEN, token)
            .putLong(KEY_SAVED_AT, System.currentTimeMillis())
            .apply()
    }

    fun clearToken() = encryptedPrefs.edit().remove(KEY_TOKEN).remove(KEY_SAVED_AT).apply()
}
