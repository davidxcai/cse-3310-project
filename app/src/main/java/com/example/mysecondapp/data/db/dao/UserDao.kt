package com.example.mysecondapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mysecondapp.data.db.entity.UserEntity

@Dao
interface UserDao {
    // Create new user
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    // Find user by email
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    // Get user by ID
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserEntity?

    // Update Email
    @Query("UPDATE users SET email = :newEmail WHERE id = :userId")
    suspend fun updateEmail(userId: Long, newEmail: String)

    // Update Password
    @Query("UPDATE users SET password = :newPassword WHERE id = :userId")
    suspend fun updatePassword(userId: Long, newPassword: String)

    // Update Dark Mode Preference
    @Query("UPDATE users SET prefer_dark_mode = :isDark WHERE id = :userId")
    suspend fun updateDarkMode(userId: Long, isDark: Boolean)

    // Login
    @Query("""
        SELECT * FROM users
        WHERE email = :email AND password = :password
        LIMIT 1
    """)
    suspend fun login(email: String, password: String): UserEntity?
}