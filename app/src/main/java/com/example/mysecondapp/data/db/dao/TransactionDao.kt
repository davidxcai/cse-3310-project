package com.example.mysecondapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mysecondapp.data.db.entity.ListingEntity
import com.example.mysecondapp.data.db.entity.TransactionEntity
import com.example.mysecondapp.data.db.entity.UserEntity

@Dao
interface TransactionDao {

    // Record the purchase
    @Insert
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    // Get purchase history for a buyer
    // This joins with the listing table so you can show the Name and Price in the UI
    @Query("""
        SELECT listing.*, transactions.purchase_date FROM listing 
        INNER JOIN transactions ON listing.listing_id = transactions.listing_id 
        WHERE transactions.buyer_id = :buyerId
        ORDER BY transactions.purchase_date DESC
    """)
    suspend fun getPurchaseHistory(buyerId: Long): List<ListingEntity>
}