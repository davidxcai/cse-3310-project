import com.example.mysecondapp.data.db.AppDatabase
import com.example.mysecondapp.data.db.entity.CartEntity
import com.example.mysecondapp.data.db.entity.ListingEntity
import com.example.mysecondapp.data.db.entity.UserEntity

class MarketplaceRepository(private val db: AppDatabase) {

    private val userDao = db.userDao()
    private val listingDao = db.listingDao()
    private val cartDao = db.cartDao()
    private val transactionDao = db.transactionDao()

    // --- User Actions ---
    suspend fun registerUser(user: UserEntity) = userDao.insert(user)
    suspend fun login(email: String, pass: String) = userDao.login(email, pass)
    suspend fun getUser(id: Long) = userDao.getUserById(id)

    // --- Listing Actions ---
    suspend fun createListing(listing: ListingEntity) = listingDao.addListing(listing)
    suspend fun updateListing(listing: ListingEntity) = listingDao.updateListing(listing)
    suspend fun deleteListing(listing: ListingEntity) = listingDao.deleteListing(listing)

    // This is useful for the main "Shop" screen
    suspend fun getAllAvailableListings() = listingDao.getListingsBySeller(-1) // Or a specific query for isSold = false

    // --- Cart & Transaction Actions ---
    suspend fun addItemToCart(buyerId: Long, listingId: Long) {
        cartDao.addToCart(CartEntity(buyerId = buyerId, listingId = listingId))
    }

    suspend fun getCartWithItems(buyerId: Long) = cartDao.getUserCart(buyerId)

    // The "Big Red Button"
    suspend fun performCheckout(buyerId: Long) = cartDao.checkout(buyerId)

    suspend fun getPurchaseHistory(buyerId: Long) = transactionDao.getPurchaseHistory(buyerId)
}