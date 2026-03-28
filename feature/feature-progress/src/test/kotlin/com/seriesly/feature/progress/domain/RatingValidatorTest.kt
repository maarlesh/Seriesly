package com.seriesly.feature.progress.domain

import org.junit.Assert.*
import org.junit.Test

class RatingValidatorTest {

    @Test fun `0_5 is valid`() = assertTrue(RatingValidator.isValid(0.5f))
    @Test fun `1_0 is valid`() = assertTrue(RatingValidator.isValid(1.0f))
    @Test fun `2_5 is valid`() = assertTrue(RatingValidator.isValid(2.5f))
    @Test fun `5_0 is valid`() = assertTrue(RatingValidator.isValid(5.0f))

    @Test fun `0_0 is invalid`() = assertFalse(RatingValidator.isValid(0.0f))
    @Test fun `5_5 is invalid`() = assertFalse(RatingValidator.isValid(5.5f))
    @Test fun `0_7 is invalid`() = assertFalse(RatingValidator.isValid(0.7f))
    @Test fun `3_3 is invalid`() = assertFalse(RatingValidator.isValid(3.3f))

    @Test fun `normalise 3_7 snaps to 3_5`() = assertEquals(3.5f, RatingValidator.normalise(3.7f))
    @Test fun `normalise 0_1 snaps to 0_5`() = assertEquals(0.5f, RatingValidator.normalise(0.1f))
    @Test fun `normalise 5_9 clamps to 5_0`() = assertEquals(5.0f, RatingValidator.normalise(5.9f))
    @Test fun `normalise 2_8 snaps to 2_5`() = assertEquals(2.5f, RatingValidator.normalise(2.8f))
}
