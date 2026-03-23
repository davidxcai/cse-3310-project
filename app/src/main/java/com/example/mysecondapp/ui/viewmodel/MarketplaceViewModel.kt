package com.example.mysecondapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysecondapp.data.db.MarketplaceRepository
import com.example.mysecondapp.data.db.entity.ListingEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MarketplaceViewModel(private val repository: MarketplaceRepository) : ViewModel() {

    private val _listings = MutableStateFlow<List<ListingEntity>>(emptyList())
    val listings: StateFlow<List<ListingEntity>> = _listings

    fun loadMarketplace(query: String = "") {
        viewModelScope.launch {
            val results = if (query.isEmpty()) {
                // If no search query, get everything available
                repository.getAllListings()
            } else {
                // Use the search method we built earlier
                repository.searchListings(query)
            }
            _listings.value = results
        }
    }
    // NEW: Function to add item to database
    fun addToCart(userId: Long, listingId: Long) {
        viewModelScope.launch {
            repository.addItemToCart(userId, listingId)
            // Tip: You could add a 'UiEvent' here to trigger a Snackbar
        }
    }


}