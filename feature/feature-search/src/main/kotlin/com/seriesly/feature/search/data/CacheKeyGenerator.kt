package com.seriesly.feature.search.data

object CacheKeyGenerator {
    fun forQuery(query: String): String {
        val normalised = query.trim().lowercase()
        return java.security.MessageDigest.getInstance("SHA-256")
            .digest(normalised.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }
}
