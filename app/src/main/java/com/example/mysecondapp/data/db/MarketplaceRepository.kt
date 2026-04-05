package com.example.mysecondapp.data.db

import com.example.mysecondapp.data.db.entity.CartEntity
import com.example.mysecondapp.data.db.entity.ListingEntity
import com.example.mysecondapp.data.db.entity.UserEntity
import com.example.mysecondapp.data.db.entity.ListingWithSeller
import com.example.mysecondapp.data.model.PurchasedListing
import kotlinx.coroutines.flow.Flow

class MarketplaceRepository(private val db: AppDatabase) {

    private val userDao = db.userDao()
    private val listingDao = db.listingDao()
    private val cartDao = db.cartDao()
    private val transactionDao = db.transactionDao()

    // --- User Actions ---
    suspend fun registerUser(user: UserEntity) = userDao.insert(user)
    suspend fun login(email: String, pass: String) = userDao.login(email, pass)
    suspend fun getUser(id: Long) = userDao.getUserById(id)
    fun observeUser(id: Long): Flow<UserEntity?> = userDao.observeUserById(id)
    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()
    fun searchUsersByName(query: String): Flow<List<UserEntity>> = userDao.searchUsersByName(query)
    suspend fun deleteUser(user: UserEntity) = userDao.delete(user)
    suspend fun updateUserEmail(id: Long, email: String) = userDao.updateEmail(id, email)
    suspend fun updateUserPassword(id: Long, pass: String) = userDao.updatePassword(id, pass)
    suspend fun updateUserName(id: Long, name: String) = userDao.updateName(id, name)
    suspend fun updateAccountType(id: Long, type: String) = userDao.updateAccountType(id, type)
    suspend fun updateDarkMode(id: Long, isDark: Boolean) = userDao.updateDarkMode(id, isDark)

    // --- Listing Actions ---
    suspend fun getListingById(id: Long): ListingEntity? {
        return listingDao.getListingById(id)
    }
    suspend fun createListing(listing: ListingEntity) = listingDao.addListing(listing)
    suspend fun updateListing(listing: ListingEntity) = listingDao.updateListing(listing)
    suspend fun deleteListing(listing: ListingEntity) = listingDao.deleteListing(listing)

    // This is useful for the main "Shop" screen
    suspend fun getAllListings() = listingDao.getAllListings()
    suspend fun getAllListingsWithSeller() = listingDao.getAllListingsWithSeller()
    
    suspend fun getListingsBySeller(sellerId: Long) = listingDao.getListingsBySeller(sellerId)
    suspend fun getListingsBySellerWithSeller(sellerId: Long) = listingDao.getListingsBySellerWithSeller(sellerId)

    suspend fun searchListings(searchQuery: String) = listingDao.searchListingsByName(searchQuery)
    suspend fun searchListingsWithSeller(searchQuery: String) = listingDao.searchListingsByNameWithSeller(searchQuery)

    // --- Cart & Transaction Actions ---
    suspend fun addItemToCart(buyerId: Long, listingId: Long) {
        cartDao.addToCart(CartEntity(buyerId = buyerId, listingId = listingId))
    }
    suspend fun removeFromCart(userId: Long, listingId: Long) {
        cartDao.removeFromCart(userId, listingId)
    }

    suspend fun getCartWithItems(buyerId: Long) = cartDao.getUserCart(buyerId)
    
    // Reactive flow for cart items
    fun observeCart(buyerId: Long): Flow<List<ListingEntity>> = cartDao.observeUserCart(buyerId)

    // The "Big Red Button"
    suspend fun performCheckout(buyerId: Long) = cartDao.checkout(buyerId)

    suspend fun getPurchaseHistory(buyerId: Long): List<PurchasedListing> = transactionDao.getPurchaseHistory(buyerId)
}
