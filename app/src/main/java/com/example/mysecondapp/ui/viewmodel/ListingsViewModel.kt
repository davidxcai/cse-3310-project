import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysecondapp.data.db.MarketplaceRepository
import com.example.mysecondapp.data.db.entity.ListingEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ListingsViewModel(private val repository: MarketplaceRepository) : ViewModel() {

    private val _userListings = MutableStateFlow<List<ListingEntity>>(emptyList())
    val userListings: StateFlow<List<ListingEntity>> = _userListings

    fun fetchListings(userId: Long) {
        viewModelScope.launch {
            // Use the repository method we created earlier
            val results = repository.getListingsBySeller(userId)
            _userListings.value = results
        }
    }
}