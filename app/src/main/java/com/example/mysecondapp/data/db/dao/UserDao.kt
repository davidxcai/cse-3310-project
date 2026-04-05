package com.example.mysecondapp.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mysecondapp.data.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // Create new user
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    @Update
    suspend fun update(user: UserEntity)

    @Delete
    suspend fun delete(user: UserEntity)

    // Find user by email
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    // Get user by ID
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun observeUserById(userId: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE name LIKE '%' || :query || '%'")
    fun searchUsersByName(query: String): Flow<List<UserEntity>>

    // Update Email
    @Query("UPDATE users SET email = :newEmail WHERE id = :userId")
    suspend fun updateEmail(userId: Long, newEmail: String)

    // Update Password
    @Query("UPDATE users SET password = :newPassword WHERE id = :userId")
    suspend fun updatePassword(userId: Long, newPassword: String)

    // Update Name
    @Query("UPDATE users SET name = :newName WHERE id = :userId")
    suspend fun updateName(userId: Long, newName: String)

    // Update Account Type
    @Query("UPDATE users SET account_type = :newType WHERE id = :userId")
    suspend fun updateAccountType(userId: Long, newType: String)

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
