package com.example.mysecondapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.mysecondapp.data.db.entity.CartEntity
import com.example.mysecondapp.data.db.entity.ListingEntity
import com.example.mysecondapp.data.db.entity.ListingWithSeller
import com.example.mysecondapp.data.db.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToCart(cartEntry: CartEntity): Long

    @Query("DELETE FROM cart WHERE buyer_id = :userId AND listing_id = :listingId")
    suspend fun removeFromCart(userId: Long, listingId: Long)

    @Query("DELETE FROM cart WHERE buyer_id = :buyerId")
    suspend fun emptyCart(buyerId: Long)

    @Query("""
        SELECT listing.* FROM listing 
        INNER JOIN cart ON listing.listing_id = cart.listing_id 
        WHERE cart.buyer_id = :buyerId
    """)
    suspend fun getUserCart(buyerId: Long): List<ListingEntity>

    @Query("""
        SELECT listing.* FROM listing 
        INNER JOIN cart ON listing.listing_id = cart.listing_id 
        WHERE cart.buyer_id = :buyerId
    """)
    fun observeUserCart(buyerId: Long): Flow<List<ListingEntity>>

    @Transaction
    @Query("""
        SELECT listing.* FROM listing 
        INNER JOIN cart ON listing.listing_id = cart.listing_id 
        WHERE cart.buyer_id = :buyerId
    """)
    fun observeUserCartWithSeller(buyerId: Long): Flow<List<ListingWithSeller>>

    @Insert
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    @Query("UPDATE listing SET is_sold = 1 WHERE listing_id IN (:listingIds)")
    suspend fun markItemsAsSold(listingIds: List<Long>)

    @Transaction
    suspend fun checkout(buyerId: Long) {
        val itemsInCart = getUserCart(buyerId)
        if (itemsInCart.isEmpty()) return

        val transactionRecords = itemsInCart.map { item ->
            TransactionEntity(
                buyerId = buyerId,
                listingId = item.id
            )
        }

        insertTransactions(transactionRecords)
        markItemsAsSold(itemsInCart.map { it.id })
        emptyCart(buyerId)
    }

    @Query("SELECT EXISTS(SELECT 1 FROM cart WHERE buyer_id = :buyerId AND listing_id = :listingId)")
    suspend fun isItemInCart(buyerId: Long, listingId: Long): Boolean
}
