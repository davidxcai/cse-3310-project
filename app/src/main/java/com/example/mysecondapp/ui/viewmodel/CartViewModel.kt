package com.example.mysecondapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysecondapp.data.db.MarketplaceRepository
import com.example.mysecondapp.data.db.entity.ListingEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel(private val repository: MarketplaceRepository) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<ListingEntity>>(emptyList())
    val cartItems: StateFlow<List<ListingEntity>> = _cartItems

    fun loadCart(userId: Long) {
        viewModelScope.launch {
            val items = repository.getCartWithItems(userId)
            _cartItems.value = items
        }
    }

    fun removeFromCart(userId: Long, listingId: Long) {
        viewModelScope.launch {
            repository.removeFromCart(userId, listingId)
            // Refresh the local state so the UI knows this item is gone
            loadCart(userId)
        }
    }
    fun checkout(userId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.performCheckout(userId)
            _cartItems.value = emptyList() // Clear UI
            onSuccess()
        }
    }
}