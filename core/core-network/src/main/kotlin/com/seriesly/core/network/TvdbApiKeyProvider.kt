package com.seriesly.core.network

fun interface TvdbApiKeyProvider {
    fun getKey(): String
}
