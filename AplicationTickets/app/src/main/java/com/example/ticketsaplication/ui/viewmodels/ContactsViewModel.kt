package com.example.ticketsaplication.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ticketsaplication.data.remote.ContactDto
import com.example.ticketsaplication.data.remote.Repository
import com.example.ticketsaplication.service.NotificationEvents
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class ContactUi(
    val id: String, // conversationId
    val ticketId: Int,
    val contactId: Int?,
    val contactName: String,
    val responsibleName: String,
    val title: String,
    var lastMessage: String,
    var sendedAt: String,
    val state: Int,
    val createdById: Int?,
    val responsibleId: Int?,
    val unreadCount: Int
)

sealed interface ContactsUiState {
    data object Loading : ContactsUiState
    data class Error(val message: String) : ContactsUiState
    data class ForbiddenError(val message: String) : ContactsUiState
    data class NetworkError(val message: String) : ContactsUiState
    data class Ready(
        val assignedToMeDirectly: List<ContactUi>,
        val assignedToMyArea: List<ContactUi>,
        val createdByMe: List<ContactUi>,
        val concluded: List<ContactUi>,
        val isSupportOrSystems: Boolean,
        val isRefreshing: Boolean = false
    ) : ContactsUiState
}

class ContactsViewModel(
    private val repo: Repository
) : ViewModel() {

    private val _state = MutableStateFlow<ContactsUiState>(ContactsUiState.Loading)
    val state: StateFlow<ContactsUiState> = _state.asStateFlow()

    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent: SharedFlow<Unit> = _logoutEvent.asSharedFlow()

    init {
        listenForNotifications()
    }

    private fun listenForNotifications() {
        viewModelScope.launch {
            NotificationEvents.messages.collect { messageData ->
                val currentUserId = repo.userId ?: return@collect
                loadContacts(currentUserId, showLoading = false)
            }
        }
    }

    fun loadContacts(currentUserId: Int, showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) {
                _state.value = ContactsUiState.Loading
            } else if (_state.value is ContactsUiState.Ready) {
                _state.update { (it as ContactsUiState.Ready).copy(isRefreshing = true) }
            }
            
            try {
                val currentUser = repo.getUserById(currentUserId)
                val myRole = currentUser.role
                val allUsers = repo.getAllUsers()
                val allRoles = repo.getRoles()
                
                val userMap = allUsers.associateBy { it.id }

                val myRoleName = allRoles.find { it.id == myRole }?.roleName ?: ""
                val isSupportOrSystems = myRoleName.equals("Soporte", ignoreCase = true) || 
                                        myRoleName.equals("Sistemas", ignoreCase = true) ||
                                        myRoleName.equals("Support", ignoreCase = true) ||
                                        myRoleName.equals("Systems", ignoreCase = true)

                val userRoleMap = allUsers.filter { it.id != null }.associate { it.id!! to it.role }

                val myAreaUserIds = if (isSupportOrSystems) {
                    allUsers.filter { it.role == myRole && it.id != null }.map { it.id!! }
                } else {
                    emptyList()
                }
                
                val idsToFetch = (myAreaUserIds + currentUserId).distinct()
                val allChatsDto = mutableListOf<ContactDto>()
                val seenTicketIds = mutableSetOf<Int>()

                for (uid in idsToFetch) {
                    try {
                        val userChats = repo.getChats(uid)
                        for (chat in userChats) {
                            if (chat.ticketId != null && !seenTicketIds.contains(chat.ticketId)) {
                                allChatsDto.add(chat)
                                seenTicketIds.add(chat.ticketId)
                            }
                        }
                    } catch (e: Exception) {}
                }

                val allTickets = allChatsDto.mapNotNull { dto ->
                    if (dto.ticketId == null || dto.state == null || dto.conversationId == null) null
                    else {
                        val creator = userMap[dto.createdById]
                        val responsible = userMap[dto.responsibleId]
                        
                        val displayContactName = if (dto.createdById == currentUserId) {
                            responsible?.fullName ?: "No responsible"
                        } else {
                            creator?.fullName ?: dto.contactName ?: "Unknown"
                        }

                        ContactUi(
                            id = dto.conversationId,
                            ticketId = dto.ticketId,
                            contactId = if (dto.createdById == currentUserId) dto.responsibleId else dto.createdById,
                            contactName = displayContactName,
                            responsibleName = responsible?.fullName ?: "Unassigned",
                            title = dto.title ?: "No title",
                            lastMessage = dto.lastMessage ?: "No messages",
                            sendedAt = dto.sendedAt ?: "",
                            state = dto.state,
                            createdById = dto.createdById,
                            responsibleId = dto.responsibleId,
                            unreadCount = dto.unreadCount ?: 0
                        )
                    }
                }

                val concluded = allTickets.filter { it.state == 1 }.sortedByDescending { it.sendedAt }
                val activeTickets = allTickets.filter { it.state != 1 }

                val createdByMe = activeTickets.filter { it.createdById == currentUserId }
                    .sortedByDescending { it.sendedAt }

                val assignedToMeDirectly = activeTickets.filter { 
                    it.responsibleId == currentUserId && it.createdById != currentUserId 
                }.sortedByDescending { it.sendedAt }
                
                val assignedToMyArea = if (isSupportOrSystems) {
                    activeTickets.filter { ticket ->
                        val respId = ticket.responsibleId
                        respId != currentUserId && 
                        ticket.createdById != currentUserId && 
                        userRoleMap[respId] == myRole
                    }.sortedByDescending { it.sendedAt }
                } else {
                    emptyList()
                }

                _state.update {
                    ContactsUiState.Ready(
                        assignedToMeDirectly = assignedToMeDirectly,
                        assignedToMyArea = assignedToMyArea,
                        createdByMe = createdByMe,
                        concluded = concluded,
                        isSupportOrSystems = isSupportOrSystems,
                        isRefreshing = false
                    )
                }

            } catch (e: HttpException) {
                if (e.code() == 403) _state.update { ContactsUiState.ForbiddenError("Session expired") }
                else _state.update { ContactsUiState.NetworkError("Network error") }
            } catch (e: IOException) {
                _state.update { ContactsUiState.NetworkError("Connection error") }
            } catch (e: Exception) {
                _state.update { ContactsUiState.Error("Error: ${e.message}") }
            }
        }
    }

    fun refresh(currentUserId: Int) {
        loadContacts(currentUserId, showLoading = false)
    }

    fun logout() {
        viewModelScope.launch {
            repo.logout()
            _logoutEvent.emit(Unit)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val repo: Repository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ContactsViewModel(repo) as T
        }
    }
}
