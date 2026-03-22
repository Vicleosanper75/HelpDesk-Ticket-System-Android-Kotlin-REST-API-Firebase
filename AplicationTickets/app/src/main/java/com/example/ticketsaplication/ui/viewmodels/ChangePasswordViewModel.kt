package com.example.ticketsaplication.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ticketsaplication.data.remote.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ChangePasswordUiState {
    data object Idle : ChangePasswordUiState
    data object Loading : ChangePasswordUiState
    data object Success : ChangePasswordUiState
    data class Error(val message: String) : ChangePasswordUiState
}

class ChangePasswordViewModel(
    private val repo: Repository,
    private val username: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChangePasswordUiState>(ChangePasswordUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun changePassword(newPass: String, confirmPass: String) {
        if (newPass.isBlank() || confirmPass.isBlank()) {
            _uiState.update { ChangePasswordUiState.Error("Fields cannot be empty.") }
            return
        }

        if (newPass != confirmPass) {
            _uiState.update { ChangePasswordUiState.Error("Passwords do not match.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { ChangePasswordUiState.Loading }
            try {
                val response = repo.changePassword(username, newPass)
                if (response.isSuccessful) {
                    _uiState.update { ChangePasswordUiState.Success }
                } else {
                    _uiState.update { ChangePasswordUiState.Error("Error changing password. Please try again.") }
                }
            } catch (e: Exception) {
                _uiState.update { ChangePasswordUiState.Error("Network error: ${e.message}") }
            }
        }
    }

    fun resetState() {
        _uiState.update { ChangePasswordUiState.Idle }
    }

    companion object {
        fun factory(repo: Repository, username: String): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ChangePasswordViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return ChangePasswordViewModel(repo, username) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}
