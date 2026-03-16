package com.example.mysecondapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mysecondapp.data.db.AppDatabase
import com.example.mysecondapp.data.db.dao.ListingDao
import com.example.mysecondapp.data.db.entity.ListingEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ListingDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: ListingDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Using an in-memory database so the data is cleared after each test
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.listingDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testListingWorkflow() = runBlocking {
        val sellerId = 1L

        // 1. Add 3 different items
        val item1 = ListingEntity(name = "Laptop", price = 1000f, sellerId = sellerId, dateAdded = 12345L, imageUrl = "", condition = "New")
        val item2 = ListingEntity(
            name = "Phone",
            price = 500f,
            sellerId = sellerId,
            dateAdded = 12346L,
            imageUrl = "",
            condition = "Used"
        )
        val item3 = ListingEntity(name = "Watch", price = 200f, sellerId = sellerId, dateAdded = 12347L, imageUrl = "", condition = "Fair")

        val id1 = dao.addListing(item1)
        val id2 = dao.addListing(item2)
        val id3 = dao.addListing(item3)

        // 2. Change the price of item 1
        val updatedItem1 = dao.getListingById(id1)?.copy(price = 950f)
        updatedItem1?.let { dao.updateListing(it) }

        // 3. Change the name of item 2
        val updatedItem2 = dao.getListingById(id2)?.copy(name = "Smartphone")
        updatedItem2?.let { dao.updateListing(it) }

        // 4. Change the condition of item 3
        val updatedItem3 = dao.getListingById(id3)?.copy(condition = "Refurbished")
        updatedItem3?.let { dao.updateListing(it) }

        // 5. Verify the updates worked via getListingById
        val check1 = dao.getListingById(id1)
        assertEquals(950f, check1?.price)

        val check2 = dao.getListingById(id2)
        assertEquals("Smartphone", check2?.name)

        // 6. Delete the last item (Watch)
        val itemToDelete = dao.getListingById(id3)
        itemToDelete?.let { dao.deleteListing(it) }

        // 7. Verify deletion
        val checkDeleted = dao.getListingById(id3)
        assertNull(checkDeleted)

        // 8. Get all remaining items from seller
        val sellerListings = dao.getListingsBySeller(sellerId)
        assertEquals(2, sellerListings.size)
    }
}