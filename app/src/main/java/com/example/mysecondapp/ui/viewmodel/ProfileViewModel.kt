package com.example.mysecondapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysecondapp.data.db.MarketplaceRepository
import com.example.mysecondapp.data.db.entity.UserEntity
import com.example.mysecondapp.data.model.PurchasedListing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: MarketplaceRepository) : ViewModel() {
    private val _user = MutableStateFlow<UserEntity?>(null)
    val user: StateFlow<UserEntity?> = _user.asStateFlow()

    private val _purchaseHistory = MutableStateFlow<List<PurchasedListing>>(emptyList())
    val purchaseHistory: StateFlow<List<PurchasedListing>> = _purchaseHistory.asStateFlow()

    fun loadUser(userId: Long) {
        viewModelScope.launch {
            _user.value = repository.getUser(userId)
            _purchaseHistory.value = repository.getPurchaseHistory(userId)
        }
    }

    fun updateName(userId: Long, newName: String) {
        viewModelScope.launch {
            repository.updateUserName(userId, newName)
            loadUser(userId)
        }
    }

    fun updateEmail(userId: Long, newEmail: String) {
        viewModelScope.launch {
            repository.updateUserEmail(userId, newEmail)
            loadUser(userId)
        }
    }

    fun updatePassword(userId: Long, newPass: String) {
        viewModelScope.launch {
            repository.updateUserPassword(userId, newPass)
            loadUser(userId)
        }
    }

    fun updateAccountType(userId: Long, newType: String) {
        viewModelScope.launch {
            repository.updateAccountType(userId, newType)
            loadUser(userId)
        }
    }

    fun updateDarkMode(userId: Long, isDark: Boolean) {
        viewModelScope.launch {
            repository.updateDarkMode(userId, isDark)
            loadUser(userId)
        }
    }
}
