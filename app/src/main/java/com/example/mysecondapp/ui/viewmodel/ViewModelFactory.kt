package com.example.mysecondapp.ui.viewmodel

import ListingsViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mysecondapp.data.db.MarketplaceRepository

class ViewModelFactory(private val repository: MarketplaceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MarketplaceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MarketplaceViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(ListingsViewModel::class.java)) {
            return ListingsViewModel(repository) as T
        }
        // Inside ViewModelFactory.kt
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(repository) as T
        }
        // Add other ViewModels (like ListingsViewModel) here as well
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}