package com.example.mysecondapp.ui.screens.auth.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysecondapp.data.db.AppDatabase
import com.example.mysecondapp.data.db.entity.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(app: Application) : AndroidViewModel(app) {

    private val userDao = AppDatabase.getInstance(app).userDao()

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun register(name: String, email: String, password: String, accountType: String) {
        viewModelScope.launch {
            // tiny validation
            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                _uiState.value = RegisterUiState(error = "Please fill all fields.")
                return@launch
            }

            // avoid duplicate emails (since you likely have unique index)
            val existing = userDao.findByEmail(email.trim())
            if (existing != null) {
                _uiState.value = RegisterUiState(error = "Email already registered.")
                return@launch
            }

            try {
                val newId = userDao.insert(
                    UserEntity(
                        name = name.trim(),
                        email = email.trim(),
                        password = password,
                        accountType = accountType
                    )
                )
                _uiState.value = RegisterUiState(success = true, userId = newId)
            } catch (e: Exception) {
                _uiState.value = RegisterUiState(error = "Register failed: ${e.message}")
            }
        }
    }
}

data class RegisterUiState(
    val success: Boolean = false,
    val userId: Long? = null,
    val error: String? = null
)
