package com.example.mysecondapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysecondapp.data.db.MarketplaceRepository
import com.example.mysecondapp.data.db.entity.ListingEntity
import com.example.mysecondapp.data.db.entity.ListingWithSeller
import com.example.mysecondapp.data.db.entity.UserEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortOption {
    NEWEST,
    OLDEST,
    PRICE_LOW_TO_HIGH,
    PRICE_HIGH_TO_LOW
}

class MarketplaceViewModel(private val repository: MarketplaceRepository) : ViewModel() {

    private val _rawListings = MutableStateFlow<List<ListingWithSeller>>(emptyList())
    private val _sortOption = MutableStateFlow(SortOption.NEWEST)
    val sortOption: StateFlow<SortOption> = _sortOption

    val listings: StateFlow<List<ListingWithSeller>> = combine(_rawListings, _sortOption) { listings, sort ->
        when (sort) {
            SortOption.NEWEST -> listings.sortedByDescending { it.listing.dateAdded }
            SortOption.OLDEST -> listings.sortedBy { it.listing.dateAdded }
            SortOption.PRICE_LOW_TO_HIGH -> listings.sortedBy { it.listing.price }
            SortOption.PRICE_HIGH_TO_LOW -> listings.sortedByDescending { it.listing.price }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent

    // AI Suggestions state
    private val _suggestions = MutableStateFlow<List<ListingEntity>>(emptyList())
    val suggestions: StateFlow<List<ListingEntity>> = _suggestions

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser

    private var userObserveJob: Job? = null

    fun loadUser(userId: Long) {
        userObserveJob?.cancel()
        userObserveJob = viewModelScope.launch {
            repository.observeUser(userId).collectLatest { user ->
                _currentUser.value = user
            }
        }
    }

    fun loadMarketplace(userId: Long, query: String = "") {
        viewModelScope.launch {
            val allListings = if (query.isEmpty()) {
                repository.getAllListingsWithSeller()
            } else {
                repository.searchListingsWithSeller(query)
            }
            _rawListings.value = allListings
        }
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun addToCart(userId: Long, listingId: Long) {
        viewModelScope.launch {
            repository.addItemToCart(userId, listingId)
            _uiEvent.emit("Item added to cart!")
        }
    }

    fun fetchAiSuggestions(query: String) {
        if (query.isBlank()) {
            _suggestions.value = emptyList()
            return
        }
        viewModelScope.launch {
            delay(500)
            val results = repository.searchListings(query)
            _suggestions.value = results.take(5)
        }
    }

    fun clearSuggestions() {
        _suggestions.value = emptyList()
    }
}