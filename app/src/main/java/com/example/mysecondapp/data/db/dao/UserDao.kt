package com.example.mysecondapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mysecondapp.data.db.entity.UserEntity

@Dao
interface UserDao {

    // Register
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    // Validate if user exists
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    // Login
    @Query("""
        SELECT * FROM users
        WHERE email = :email AND password = :password
        LIMIT 1
    """)
    suspend fun login(email: String, password: String): UserEntity?
}