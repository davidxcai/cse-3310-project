package com.example.mysecondapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mysecondapp.data.db.AppDatabase
import com.example.mysecondapp.data.db.dao.CartDao
import com.example.mysecondapp.data.db.dao.ListingDao
import com.example.mysecondapp.data.db.entity.CartEntity
import com.example.mysecondapp.data.db.entity.ListingEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CartDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var cartDao: CartDao
    private lateinit var listingDao: ListingDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        cartDao = db.cartDao()
        listingDao = db.listingDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testCartCheckoutWorkflow() = runBlocking {
        val buyerId = 100L
        val sellerId = 200L

        // 1. Seed the Database with Listing items
        val item1 = ListingEntity(
            name = "Guitar",
            price = 500f,
            sellerId = sellerId,
            dateAdded = 1L,
            imageUrl = "",
            condition = "Used"
        )
        val item2 = ListingEntity(name = "Amp", price = 200f, sellerId = sellerId, dateAdded = 2L, imageUrl = "", condition = "New")

        val id1 = listingDao.addListing(item1)
        val id2 = listingDao.addListing(item2)

        // 2. Add items to Cart
        cartDao.addToCart(CartEntity(buyerId = buyerId, listingId = id1))
        cartDao.addToCart(CartEntity(buyerId = buyerId, listingId = id2))

        // 3. Verify items are in the cart
        val cartItems = cartDao.getUserCart(buyerId)
        assertEquals(2, cartItems.size)
        assertEquals("Guitar", cartItems[0].name)

        // 4. Perform Checkout
        cartDao.checkout(buyerId)

        // 5. Assertions after Checkout
        // A. Cart should be empty
        val emptyCart = cartDao.getUserCart(buyerId)
        assertTrue(emptyCart.isEmpty())

        // B. Listings should be marked as sold
        val soldItem1 = listingDao.getListingById(id1)
        val soldItem2 = listingDao.getListingById(id2)
        assertTrue(soldItem1?.isSold == true)
        assertTrue(soldItem2?.isSold == true)
    }

    @Test
    fun testRemoveSingleItemFromCart() = runBlocking {
        val buyerId = 1L
        // Seed one item
        val id = listingDao.addListing(ListingEntity(name = "Table", price = 50f, sellerId = 2L, dateAdded = 3L, imageUrl = "", condition = "Good"))

        // Add to cart
        val cartId = cartDao.addToCart(CartEntity(buyerId = buyerId, listingId = id))

        // Remove it
//        cartDao.removeFromCart(cartId)

        // Verify
        assertFalse(cartDao.isItemInCart(buyerId, id))
    }


}