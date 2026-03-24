package com.example.mysecondapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mysecondapp.data.db.AppDatabase
import com.example.mysecondapp.data.db.dao.CartDao
import com.example.mysecondapp.data.db.dao.ListingDao
import com.example.mysecondapp.data.db.dao.TransactionDao
import com.example.mysecondapp.data.db.entity.CartEntity
import com.example.mysecondapp.data.db.entity.ListingEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TransactionDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var transactionDao: TransactionDao
    private lateinit var listingDao: ListingDao
    private lateinit var cartDao: CartDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Initialize in-memory database
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        transactionDao = db.transactionDao()
        listingDao = db.listingDao()
        cartDao = db.cartDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testPurchaseHistoryRetrieval() = runBlocking {
        val buyerId = 77L
        val sellerId = 88L

        // 1. Create Listings
        val item1 = ListingEntity(
            name = "Bike",
            price = 300f,
            sellerId = sellerId,
            dateAdded = 100L,
            imageUrl = "",
            condition = "Used"
        )
        val item2 = ListingEntity(name = "Helmet", price = 50f, sellerId = sellerId, dateAdded = 101L, imageUrl = "", condition = "New")

        val id1 = listingDao.addListing(item1)
        val id2 = listingDao.addListing(item2)

        // 2. Add to Cart and Checkout
        cartDao.addToCart(CartEntity(buyerId = buyerId, listingId = id1))
        cartDao.addToCart(CartEntity(buyerId = buyerId, listingId = id2))

        cartDao.checkout(buyerId)

        // 3. Verify Transaction History
        val history = transactionDao.getPurchaseHistory(buyerId)

        // Assertions
        assertEquals("Should have 2 items in purchase history", 2, history.size)

        // Check that the data joined correctly from the listing table
        val itemNames = history.map { it.name }
        assertTrue(itemNames.contains("Bike"))
        assertTrue(itemNames.contains("Helmet"))

        // Check that the listings were correctly marked as sold in the process
        assertTrue("Items in history should be marked as sold", history.all { it.isSold })
    }
}