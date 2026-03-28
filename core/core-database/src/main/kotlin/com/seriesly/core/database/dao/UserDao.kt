package com.seriesly.core.database.dao

import androidx.room.*
import com.seriesly.core.database.entity.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun findByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    suspend fun findById(userId: Long): UserEntity?

    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    suspend fun usernameExists(username: String): Int

    @Update
    suspend fun update(user: UserEntity)
}
