package com.example.mysecondapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysecondapp.data.db.MarketplaceRepository
import com.example.mysecondapp.data.db.entity.ListingEntity
import com.example.mysecondapp.data.db.entity.ListingWithSeller
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CartViewModel(private val repository: MarketplaceRepository) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<ListingWithSeller>>(emptyList())
    val cartItems: StateFlow<List<ListingWithSeller>> = _cartItems
    
    private var observeJob: Job? = null

    fun loadCart(userId: Long) {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            repository.observeCartWithSeller(userId).collectLatest { items ->
                _cartItems.value = items
            }
        }
    }

    fun removeFromCart(userId: Long, listingId: Long) {
        viewModelScope.launch {
            repository.removeFromCart(userId, listingId)
        }
    }
    
    fun checkout(userId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.performCheckout(userId)
            onSuccess()
        }
    }
}
