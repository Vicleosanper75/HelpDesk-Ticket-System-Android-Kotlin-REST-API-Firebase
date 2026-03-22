package com.example.ticketsaplication.data.remote

import java.io.File
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class Repository(private val api: ApiService = RetrofitProvider.api) {
    var token: String? = null;        private set
    var username: String? = null;     private set
    var isAdmin: Boolean = false;     private set
    var userId: Int? = null;         private set

    //Restaurar sesión guardada ---
    fun restoreSession(token: String, userId: Int, username: String) {
        this.token = token
        this.userId = userId
        this.username = username
        // Por defecto false al restaurar, o podrías guardarlo también en Preferences si es crítico
        this.isAdmin = false
    }

    suspend fun login(username: String, password: String) {
        val r = api.login(LoginRequest(username, password))

        val t = r.accessToken
        require(!t.isNullOrBlank()) { "Login without token: check response fields" }

        token = t
        val allUsers = getAllUsers()
        val currentUser = allUsers.find { it.userName == username }

        this.username = currentUser?.fullName ?: username
        isAdmin = false
        userId = currentUser?.id ?: error("Could not find user ID after login.")
    }

    suspend fun logout() {
        val currentToken = token ?: return

        try {
            api.logout("Bearer $currentToken", LogoutRequest(token = currentToken))
        } catch (e: Exception) {
            println("Logout API call failed: ${e.message}")
        } finally {
            token = null
            username = null
            isAdmin = false
            userId = null
        }
    }

    suspend fun getChats(userId: Int): List<ContactDto> {
        val t = token ?: error("Not authenticated")
        return api.getChats("Bearer $t", userId)
    }

    suspend fun getAllUsers(): List<UserDto> {
        val t = token ?: error("Not authenticated")
        return api.getAllUsers("Bearer $t")
    }

    suspend fun getUserById(userId: Int): UserDto {
        val t = token ?: error("Not authenticated")
        return api.getUserById("Bearer $t", userId)
    }

    suspend fun getMessages(ticketId: Int?, contactId: Int?): List<MessageDto> {
        val t = token ?: error("Not authenticated")
        val uid = userId ?: error("userId not available")

        return api.getMessages(
            bearer = "Bearer $t",
            userId = uid,
            contactId = contactId,
            ticketId = ticketId
        )
    }

    suspend fun getRoles(): List<RoleDto> {
        val t = token ?: error("Not authenticated")
        return api.getRoles("Bearer $t")
    }

    suspend fun getUsersByRole(roleId: Int): List<UserDto> {
        val t = token ?: error("Not authenticated")
        return api.getUsersByRole("Bearer $t", roleId)
    }


    suspend fun createTicket(createdBy: Int, title: String, responsible: Int?): CreateTicketResponse {
        val t = token ?: throw IllegalStateException("Token not found")
        val request = CreateTicketRequest(createdBy, title, responsible)
        return api.createTicket("Bearer $t", request)
    }

    suspend fun getTicketById(ticketId: Int): TicketDto {
        val t = token ?: error("Not authenticated")
        return api.getTicketById("Bearer $t", ticketId)
    }

    suspend fun concludeTicket(ticketId: Int) {
        val t = token ?: throw IllegalStateException("Token not found")
        val request = ConcludeTicketRequest(id = ticketId)
        api.concludeTicket("Bearer $t", request)
    }

    suspend fun reassignTicket(ticketId: Int, newResponsibleId: Int) {
        val t = token ?: throw IllegalStateException("Token not found")
        val request = ReassignTicketRequest(id = ticketId, newResponsibleId = newResponsibleId)
        api.reassignTicket("Bearer $t", request)
    }

    suspend fun sendMessage(ticketId: Int?, senderId: Int, receiverId: Int?, content: String?, imageFile: File? = null) {
        val t = token ?: throw IllegalStateException("Token not found")

        val ticketPart = ticketId?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
        val senderPart = senderId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val receiverPart = receiverId?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
        val contentPart = content?.toRequestBody("text/plain".toMediaTypeOrNull())

        val imagePart = imageFile?.let { file ->
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", file.name, requestFile)
        }

        api.sendMessage(
            bearer = "Bearer $t",
            ticket = ticketPart,
            sender = senderPart,
            receiver = receiverPart,
            content = contentPart,
            image = imagePart
        )
    }

    suspend fun markAsRead(ticketId: Int, userId: Int) {
        val t = token ?: throw IllegalStateException("Token not found")
        val request = MarkAsReadRequest(ticketId = ticketId, userId = userId)
        api.markAsRead("Bearer $t", request)
    }

    suspend fun updateFcmToken(userId: Int, fcmToken: String) {
        val t = token ?: error("Not authenticated")
        val request = UpdateTokenRequest(userId, fcmToken)
        api.updateToken("Bearer $t", request)
    }

    suspend fun changePassword(username: String, newPwd: String): Response<Unit> {
        val t = token ?: error("Not authenticated")
        val request = ChangePasswordRequest(username = username, newPwd = newPwd)
        return api.changePassword("Bearer $t", request)
    }

}
