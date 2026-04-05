package com.example.mysecondapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mysecondapp.data.db.entity.TransactionEntity
import com.example.mysecondapp.data.model.PurchasedListing

@Dao
interface TransactionDao {

    // Record the purchase
    @Insert
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    // Get purchase history for a buyer
    @Query("""
        SELECT 
            listing.listing_id AS id, 
            listing.name AS name, 
            listing.price AS price, 
            listing.condition AS condition, 
            users.name AS sellerName, 
            transactions.purchase_date AS purchaseDate
        FROM listing 
        INNER JOIN transactions ON listing.listing_id = transactions.listing_id 
        INNER JOIN users ON listing.seller_id = users.id
        WHERE transactions.buyer_id = :buyerId
        ORDER BY transactions.purchase_date DESC
    """)
    suspend fun getPurchaseHistory(buyerId: Long): List<PurchasedListing>
}
