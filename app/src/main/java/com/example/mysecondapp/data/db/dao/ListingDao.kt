package com.example.mysecondapp.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mysecondapp.data.db.entity.ListingEntity
import com.example.mysecondapp.data.db.entity.UserEntity

@Dao
interface ListingDao {

    // Add a new listing
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addListing(listing: ListingEntity): Long

    // Update an existing listing (matches by the listing_id Primary Key)
    @Update
    suspend fun updateListing(listing: ListingEntity)

    // Get all available/unsold listings
    @Query("SELECT * FROM listing WHERE is_sold = 0 AND is_hidden = 0")
    suspend fun getAllListings(): List<ListingEntity>

    // Search Listing
    // Returns a list of Listings because search usually finds multiple items
    @Query("SELECT * FROM listing WHERE name LIKE '%' || :searchQuery || '%' AND is_sold = 0 AND is_hidden = 0")
    suspend fun searchListingsByName(searchQuery: String): List<ListingEntity>

    // Get a specific listing by its ID
    @Query("SELECT * FROM listing WHERE listing_id = :id LIMIT 1")
    suspend fun getListingById(id: Long): ListingEntity?

    // Delete a specific listing
    @Delete
    suspend fun deleteListing(listing: ListingEntity)

    // Get all listings for a specific seller
    @Query("SELECT * FROM listing WHERE seller_id = :sellerId")
    suspend fun getListingsBySeller(sellerId: Long): List<ListingEntity>
}