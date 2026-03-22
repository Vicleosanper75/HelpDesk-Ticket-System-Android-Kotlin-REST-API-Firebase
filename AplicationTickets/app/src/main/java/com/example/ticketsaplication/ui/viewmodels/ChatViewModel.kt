package com.example.ticketsaplication.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ticketsaplication.data.remote.Repository
import com.example.ticketsaplication.data.remote.RoleDto
import com.example.ticketsaplication.data.remote.UserDto
import com.example.ticketsaplication.service.NotificationEvents
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

data class UiMessage(
    val id: String,
    val content: String,
    val date: String,
    val isSentByMe: Boolean,
    val senderName: String
)

sealed interface ChatUiState {
    data object Loading : ChatUiState
    data class Ready(val messages: List<UiMessage>) : ChatUiState
    data class Error(val message: String) : ChatUiState
}

data class ReassignmentState(
    val showDialog: Boolean = false,
    val isLoading: Boolean = false,
    val roles: List<RoleDto> = emptyList(),
    val allUsers: List<UserDto> = emptyList(),
    val usersInSelectedRole: List<UserDto> = emptyList(),
    val selectedRole: RoleDto? = null,
    val selectedUser: UserDto? = null,
    val isDifferentArea: Boolean = false,
    val error: String? = null
)

class ChatViewModel(
    private val repo: Repository,
    private val conversationId: String,
    private val contactId: Int
) : ViewModel() {

    private val _state = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val state: StateFlow<ChatUiState> = _state.asStateFlow()

    private val _isCurrentUserResponsible = MutableStateFlow(false)
    val isCurrentUserResponsible: StateFlow<Boolean> = _isCurrentUserResponsible.asStateFlow()

    private val _isActuallyResponsible = MutableStateFlow(false)
    val isActuallyResponsible: StateFlow<Boolean> = _isActuallyResponsible.asStateFlow()

    private val _isTechnicalUser = MutableStateFlow(false)
    val isTechnicalUser: StateFlow<Boolean> = _isTechnicalUser.asStateFlow()

    private val _isTicketConcluded = MutableStateFlow(false)
    val isTicketConcluded: StateFlow<Boolean> = _isTicketConcluded.asStateFlow()

    private val _isCreator = MutableStateFlow(false)
    val isCreator: StateFlow<Boolean> = _isCreator.asStateFlow()

    private val _responsibleName = MutableStateFlow<String?>(null)
    val responsibleName: StateFlow<String?> = _responsibleName.asStateFlow()

    private val _reassignmentState = MutableStateFlow(ReassignmentState())
    val reassignmentState: StateFlow<ReassignmentState> = _reassignmentState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private var ticketId: Int? = null
    private var currentTicketRoleId: Int? = null

    private val roleBossMap = mapOf(
        3 to 21,
        2 to 29
    )

    init {
        if (conversationId.startsWith("ticket_")) {
            ticketId = try {
                conversationId.removePrefix("ticket_").toInt()
            } catch (e: NumberFormatException) {
                null
            }
        }
        listenForNotifications()
        refresh()
    }

    private fun listenForNotifications() {
        viewModelScope.launch {
            NotificationEvents.messages.collect { messageData ->
                val notifTicketId = messageData["ticketId"]?.takeIf { it != "null" }?.toIntOrNull()
                val notifSenderId = messageData["senderId"]?.toIntOrNull()

                if (notifSenderId == repo.userId) return@collect

                val isForThisChat = if (ticketId != null) {
                    notifTicketId == ticketId
                } else {
                    notifSenderId == contactId
                }

                if (isForThisChat) {
                    refresh()
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                val currentUserId = repo.userId

                if (ticketId != null && currentUserId != null) {
                    val ticketDetails = repo.getTicketById(ticketId!!)
                    val responsibleId = ticketDetails.responsible?.toIntOrNull()
                    val createdById = ticketDetails.createdBy?.toIntOrNull()

                    // Solo marcar como leído si:
                    // 1. El ticket está abierto (state == 0)
                    // 2. Soy el responsable asignado O soy el creador del ticket
                    if (ticketDetails.state == 0 && (responsibleId == currentUserId || createdById == currentUserId)) {
                        repo.markAsRead(ticketId!!, currentUserId)
                    }
                    
                    _isCurrentUserResponsible.value = (responsibleId == currentUserId || createdById == currentUserId)
                    _isActuallyResponsible.value = (responsibleId == currentUserId)
                    _isTicketConcluded.value = ticketDetails.state == 1
                    _isCreator.value = (createdById == currentUserId)

                    val currentUser = repo.getUserById(currentUserId)
                    _isTechnicalUser.value = currentUser.role == 2 || currentUser.role == 3

                    if (responsibleId != null) {
                        val responsibleUser = repo.getUserById(responsibleId)
                        currentTicketRoleId = responsibleUser.role
                        _responsibleName.value = responsibleUser.fullName
                    } else {
                        currentTicketRoleId = null
                        _responsibleName.value = "Unassigned"
                    }
                } else {
                    _isCurrentUserResponsible.value = true
                    _isActuallyResponsible.value = false
                    _isTicketConcluded.value = false
                    _isCreator.value = false
                    _responsibleName.value = null
                    _isTechnicalUser.value = false
                }

                val messages = repo.getMessages(ticketId = ticketId, contactId = if (ticketId == null) contactId else null)
                val uiMessages = messages.map { msg ->
                    UiMessage(
                        id = msg.from + msg.createdAt,
                        content = msg.content,
                        date = msg.createdAt,
                        isSentByMe = msg.senderId == currentUserId,
                        senderName = msg.from
                    )
                }
                _state.value = ChatUiState.Ready(uiMessages.sortedBy { it.date })

            } catch (e: Exception) {
                if (_state.value is ChatUiState.Loading) {
                    _state.value = ChatUiState.Error(e.message ?: "Error loading messages")
                }
            }
        }
    }

    fun sendMessage(text: String, images: List<File> = emptyList()) {
        if (text.isBlank() && images.isEmpty()) return
        if (_isTicketConcluded.value || !_isCurrentUserResponsible.value) return 

        val currentUserId = repo.userId ?: return
        viewModelScope.launch {
            try {
                if (text.isNotBlank()) repo.sendMessage(ticketId, currentUserId, contactId, text, null)
                images.forEach { repo.sendMessage(ticketId, currentUserId, contactId, null, it) }
                delay(300)
                refresh()
            } catch (e: Exception) {
                _state.value = ChatUiState.Error(e.message ?: "Sending error")
            }
        }
    }

    fun concludeTicket() {
        val currentTicketId = ticketId ?: return
        if (!_isActuallyResponsible.value) return

        viewModelScope.launch {
            try {
                repo.concludeTicket(currentTicketId)
                refresh()
            } catch (e: Exception) {
                _state.value = ChatUiState.Error(e.message ?: "Closing error")
            }
        }
    }

    fun takeTicket() {
        val currentTicketId = ticketId ?: return
        val currentUserId = repo.userId ?: return
        val p1Name = repo.username ?: "Someone"

        viewModelScope.launch {
            try {
                repo.reassignTicket(currentTicketId, currentUserId)
                
                // Mensaje: "Persona 1 reasigno el ticket # a Persona 1"
                val msg = "$p1Name reassigned ticket #$currentTicketId to $p1Name"
                repo.sendMessage(currentTicketId, currentUserId, currentUserId, msg, null)

                delay(500)
                refresh()
            } catch (e: Exception) {
                _state.value = ChatUiState.Error(e.message ?: "Error taking ticket")
            }
        }
    }

    fun startReassignment() {
        if (!_isActuallyResponsible.value) return

        _reassignmentState.value = ReassignmentState(isLoading = true, showDialog = true)
        viewModelScope.launch {
            try {
                val roles = repo.getRoles().filter { role ->
                    role.roleName.equals("Soporte", ignoreCase = true) || 
                    role.roleName.equals("Sistemas", ignoreCase = true) ||
                    role.roleName.equals("Support", ignoreCase = true) ||
                    role.roleName.equals("Systems", ignoreCase = true)
                }
                val allUsers = repo.getAllUsers()
                _reassignmentState.update {
                    it.copy(isLoading = false, roles = roles, allUsers = allUsers)
                }
            } catch (e: Exception) {
                _reassignmentState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onRoleSelected(role: RoleDto) {
        val isDifferent = role.id != currentTicketRoleId
        val usersForRole = if (!isDifferent) {
            _reassignmentState.value.allUsers.filter { it.role == role.id && it.active == 1 }
        } else {
            emptyList()
        }
        _reassignmentState.update {
            it.copy(selectedRole = role, selectedUser = null, isDifferentArea = isDifferent, usersInSelectedRole = usersForRole)
        }
    }

    fun onUserSelected(user: UserDto) {
        _reassignmentState.update { it.copy(selectedUser = user) }
    }

    fun cancelReassignment() {
        _reassignmentState.value = ReassignmentState()
    }

    fun confirmReassignment() {
        val currentTicketId = ticketId ?: return
        val reassignData = _reassignmentState.value
        val selectedRole = reassignData.selectedRole ?: return
        val currentUserId = repo.userId ?: return

        viewModelScope.launch {
            _reassignmentState.update { it.copy(isLoading = true, error = null) }
            try {
                val newResponsibleId = if (reassignData.isDifferentArea) {
                    roleBossMap[selectedRole.id] ?: throw IllegalStateException("Area without configured manager")
                } else {
                    reassignData.selectedUser?.id ?: throw IllegalStateException("Select a user")
                }

                val newResponsibleUser = repo.getUserById(newResponsibleId)
                val p1Name = repo.username ?: "Someone"
                val p2Name = newResponsibleUser.fullName ?: "a new responsible"
                
                repo.reassignTicket(currentTicketId, newResponsibleId)
                
                val msg = "$p1Name reassigned ticket #$currentTicketId to $p2Name"
                repo.sendMessage(currentTicketId, currentUserId, newResponsibleId, msg, null)

                delay(500)
                _reassignmentState.value = ReassignmentState(showDialog = false)
                _navigationEvent.emit(Unit)
            } catch (e: Exception) {
                _reassignmentState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    class Factory(
        private val repo: Repository,
        private val conversationId: String,
        private val contactId: Int
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChatViewModel(repo, conversationId, contactId) as T
        }
    }
}
