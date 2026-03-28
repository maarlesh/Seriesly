package com.seriesly.feature.progress.domain

object RatingValidator {
    val VALID_RANGE = 0.5f..5.0f
    const val STEP = 0.5f
    const val MAX_COMMENT_LENGTH = 500

    fun isValid(rating: Float): Boolean =
        rating in VALID_RANGE && (rating * 2) % 1 == 0f

    fun normalise(raw: Float): Float =
        ((raw * 2).toInt() / 2f).coerceIn(VALID_RANGE)
}
