package com.example.mysecondapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mysecondapp.data.db.AppDatabase
import com.example.mysecondapp.data.db.dao.UserDao
import com.example.mysecondapp.data.db.entity.UserEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        userDao = db.userDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testRegisterAndFindUser() = runBlocking {
        val user =
            UserEntity(email = "test@example.com", password = "password123", name = "Test User", accountType = "Buyer", isAdmin = false, preferDarkMode = false)

        // 1. Test Insert
        val id = userDao.insert(user)

        // 2. Test getUserById
        val retrievedUser = userDao.getUserById(id)
        assertNotNull(retrievedUser)
        assertEquals("test@example.com", retrievedUser?.email)
    }

    @Test
    fun testLoginFlow() = runBlocking {
        val email = "login@example.com"
        val pass = "securePass"
        val user = UserEntity(email = email, password = pass, name = "Login User", accountType = "Buyer", isAdmin = false, preferDarkMode = false)
        userDao.insert(user)

        // 1. Test Successful Login
        val loggedInUser = userDao.login(email, pass)
        assertNotNull(loggedInUser)
        assertEquals("Login User", loggedInUser?.name)

        // 2. Test Failed Login (Wrong Password)
        val wrongPass = userDao.login(email, "wrong_password")
        assertNull(wrongPass)

        // 3. Test Failed Login (Non-existent Email)
        val wrongEmail = userDao.login("nobody@example.com", pass)
        assertNull(wrongEmail)
    }

    @Test
    fun testFindByEmail() = runBlocking {
        val email = "findme@example.com"
        userDao.insert(UserEntity(email = email, password = "123", name = "Find Me", accountType = "Buyer", isAdmin = false, preferDarkMode = false))

        val found = userDao.findByEmail(email)
        assertNotNull(found)
        assertEquals("Find Me", found?.name)
    }
}