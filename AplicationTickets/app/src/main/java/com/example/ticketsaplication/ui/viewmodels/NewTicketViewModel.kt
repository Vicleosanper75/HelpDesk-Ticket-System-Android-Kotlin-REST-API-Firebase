package com.example.ticketsaplication.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketsaplication.data.remote.Repository
import com.example.ticketsaplication.data.remote.RoleDto
import com.example.ticketsaplication.data.remote.UserDto
import com.example.ticketsaplication.ui.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class NewTicketViewModel(
    private val repo: Repository,
    private val currentUserId: Int
): ViewModel() {

    var state by mutableStateOf(NewTicketState())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                val roles = repo.getRoles()
                val users = repo.getAllUsers()
                state = state.copy(allRoles = roles, allUsers = users)
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.ShowToast(e.message ?: "Error loading data"))
            } finally {
                state = state.copy(isLoading = false)
            }
        }
    }

    fun onTitleChange(title: String) {
        state = state.copy(title = title)
    }

    fun onRoleSelected(role: RoleDto) {
        state = state.copy(
            selectedRole = role,
            filteredResponsibles = state.allUsers.filter { it.role == role.id },
            selectedResponsible = null
        )
    }

    fun onResponsibleSelected(user: UserDto) {
        state = state.copy(selectedResponsible = user)
    }
}

data class NewTicketState(
    val title: String = "",
    val isLoading: Boolean = false,
    val allRoles: List<RoleDto> = emptyList(),
    val allUsers: List<UserDto> = emptyList(),
    val selectedRole: RoleDto? = null,
    val filteredResponsibles: List<UserDto> = emptyList(),
    val selectedResponsible: UserDto? = null
)
