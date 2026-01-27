package com.example.mysecondapp.ui.screens.auth.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysecondapp.data.db.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(app: Application) : AndroidViewModel(app) {
    private val userDao = AppDatabase.getInstance(app).userDao()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(loading = true)

            if (email.isBlank() || password.isBlank()) {
                _uiState.value = LoginUiState(error = "Enter email + password.")
                return@launch
            }

            val user = userDao.login(email.trim(), password)
            if (user == null) {
                _uiState.value = LoginUiState(error = "Invalid email or password.")
            } else {
                _uiState.value = LoginUiState(successUserId = user.id)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class LoginUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val successUserId: Long? = null
)
