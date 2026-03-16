package com.example.mysecondapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mysecondapp.data.db.AppDatabase
import com.example.mysecondapp.data.db.dao.CartDao
import com.example.mysecondapp.data.db.dao.ListingDao
import com.example.mysecondapp.data.db.dao.TransactionDao
import com.example.mysecondapp.data.db.dao.UserDao
import com.example.mysecondapp.data.db.entity.CartEntity
import com.example.mysecondapp.data.db.entity.ListingEntity
import com.example.mysecondapp.data.db.entity.UserEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MarketplaceFlowTest {

    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var listingDao: ListingDao
    private lateinit var cartDao: CartDao
    private lateinit var transactionDao: TransactionDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        userDao = db.userDao()
        listingDao = db.listingDao()
        cartDao = db.cartDao()
        transactionDao = db.transactionDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun fullMarketplaceUserFlow() = runBlocking {
        // --- PHASE 1: USER REGISTRATION ---
        val sellerId = userDao.insert(
            UserEntity(
                name = "Alice Seller",
                email = "alice@test.com",
                password = "123",
                accountType = "SELLER",
                isAdmin = false
            )
        )
        val buyerId = userDao.insert(UserEntity(name = "Bob Buyer", email = "bob@test.com", password = "123", accountType = "BUYER", isAdmin = false))
        val adminId = userDao.insert(UserEntity(name = "Charlie Admin", email = "admin@test.com", password = "123", accountType = "ADMIN", isAdmin = true))

        // --- PHASE 2: SELLER LISTS 5 ITEMS ---
        val items = listOf(
            ListingEntity(
                name = "Keyboard",
                price = 50f,
                sellerId = sellerId,
                dateAdded = 1L,
                imageUrl = "",
                condition = "New"
            ),
            ListingEntity(name = "Mouse", price = 25f, sellerId = sellerId, dateAdded = 2L, imageUrl = "", condition = "New"),
            ListingEntity(name = "Monitor", price = 200f, sellerId = sellerId, dateAdded = 3L, imageUrl = "", condition = "Used"),
            ListingEntity(name = "Desk", price = 150f, sellerId = sellerId, dateAdded = 4L, imageUrl = "", condition = "Used"),
            ListingEntity(name = "Chair", price = 100f, sellerId = sellerId, dateAdded = 5L, imageUrl = "", condition = "Fair")
        )

        val listingIds = items.map { listingDao.addListing(it) }
        assertEquals(5, listingDao.getListingsBySeller(sellerId).size)

        // --- PHASE 3: SELLER EDITS ATTRIBUTES ---
        // Edit 1: Price
        val keyboard = listingDao.getListingById(listingIds[0])!!
        listingDao.updateListing(keyboard.copy(price = 45f))

        // Edit 2: Name
        val mouse = listingDao.getListingById(listingIds[1])!!
        listingDao.updateListing(mouse.copy(name = "Gaming Mouse"))

        // Edit 3: Condition
        val monitor = listingDao.getListingById(listingIds[2])!!
        listingDao.updateListing(monitor.copy(condition = "Like New"))

        // --- PHASE 4: SELLER REMOVES 1 ITEM (The Desk) ---
        val desk = listingDao.getListingById(listingIds[3])!!
        listingDao.deleteListing(desk)
        assertEquals(4, listingDao.getListingsBySeller(sellerId).size)

        // --- PHASE 5: BUYER ADDS 2 ITEMS TO CART ---
        // Buyer wants the Gaming Mouse and the Monitor
        cartDao.addToCart(CartEntity(buyerId = buyerId, listingId = listingIds[1]))
        cartDao.addToCart(CartEntity(buyerId = buyerId, listingId = listingIds[2]))

        val cartItems = cartDao.getUserCart(buyerId)
        assertEquals(2, cartItems.size)

        // --- PHASE 6: BUYER CHECKOUT ---
        cartDao.checkout(buyerId)

        // Verify Cart is empty
        assertTrue(cartDao.getUserCart(buyerId).isEmpty())

        // --- PHASE 7: GLOBAL AUDIT ---
        // Verify 2 items marked as sold
        val purchasedMouse = listingDao.getListingById(listingIds[1])!!
        val remainingKeyboard = listingDao.getListingById(listingIds[0])!!

        assertTrue(purchasedMouse.isSold)
        assertFalse(remainingKeyboard.isSold)

        // Verify Admin/Buyer can see 2 items in transaction history
        val totalTransactions = transactionDao.getPurchaseHistory(buyerId)
        assertEquals(2, totalTransactions.size)
    }
}