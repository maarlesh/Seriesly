package com.seriesly.core.security.crypto

import org.mindrot.jbcrypt.BCrypt

object PasswordHasher {
    private const val COST_FACTOR = 12

    fun hash(plaintext: String): String =
        BCrypt.hashpw(plaintext, BCrypt.gensalt(COST_FACTOR))

    fun verify(plaintext: String, hashed: String): Boolean =
        BCrypt.checkpw(plaintext, hashed)
}
