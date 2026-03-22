package com.example.ticketsaplication.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketsaplication.data.remote.Repository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data class Success(val username: String, val userId: Int) : LoginUiState
    data class Error(val message: String) : LoginUiState
}


class LoginViewModel(private val repo: Repository = Repository()) : ViewModel() {
    private val _state = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val state: StateFlow<LoginUiState> = _state


    fun login(username: String, password: String) {
        _state.value = LoginUiState.Loading
        viewModelScope.launch {
            try {
                repo.login(username, password)
                
                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("LoginViewModel", "Fetching FCM registration token failed", task.exception)
                        return@OnCompleteListener
                    }

                    val token = task.result
                    Log.d("LoginViewModel", "Token obtained: $token")
                    
                    repo.userId?.let { uid ->
                        viewModelScope.launch {
                            try {
                                repo.updateFcmToken(uid, token)
                                Log.d("LoginViewModel", "Token sent to server successfully")
                            } catch (e: Exception) {
                                Log.e("LoginViewModel", "Error sending token to server: ${e.message}")
                            }
                        }
                    }
                })

                _state.value = LoginUiState.Success(
                    repo.username ?: username,
                    repo.userId ?: 0
                )
            } catch (e: Exception) {
                _state.value = LoginUiState.Error(e.message ?: "Login error")
            }
        }
    }

    fun repository() = repo
}
