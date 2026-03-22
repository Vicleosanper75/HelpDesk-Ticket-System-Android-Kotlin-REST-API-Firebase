package com.example.ticketsaplication.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import retrofit2.Response

interface ApiService {
    @POST("/users/login")
    suspend fun login(@Body req: LoginRequest): LoginResponse

    @GET("users")
    suspend fun getAllUsers(@Header("Authorization") bearer: String): List<UserDto>

    @GET("users/{id}")
    suspend fun getUserById(
        @Header("Authorization") bearer: String,
        @Path("id") userId: Int
    ): UserDto

    @GET("messages/getChats/{userId}")
    suspend fun getChats(
        @Header("Authorization") bearer: String,
        @Path("userId") userId: Int
    ): List<ContactDto>

    @GET("/messages/getMessages")
    suspend fun getMessages(
        @Header("Authorization") bearer: String,
        @Query("user") userId: Int,
        @Query("contact") contactId: Int?,
        @Query("ticket") ticketId: Int?
    ): List<MessageDto>

    @GET("users/roles")
    suspend fun getRoles(@Header("Authorization") bearer: String): List<RoleDto>

    @GET("users")
    suspend fun getUsersByRole(
        @Header("Authorization") bearer: String,
        @Query("role") roleId: Int
    ): List<UserDto>


    //Creacion de tickets
    @POST("tickets/createTicket")
    suspend fun createTicket(
        @Header("Authorization") bearer: String,
        @Body body: CreateTicketRequest
    ): CreateTicketResponse // Esperamos una respuesta con el ID

    @GET("tickets/{id}")
    suspend fun getTicketById(
        @Header("Authorization") bearer: String,
        @Path("id") ticketId: Int
    ): TicketDto

    @PUT("tickets/concludeTicket")
    suspend fun concludeTicket(
        @Header("Authorization") bearer: String,
        @Body body: ConcludeTicketRequest
    ): Response<Unit>

    @PUT("tickets/reassignTicket")
    suspend fun reassignTicket(
        @Header("Authorization") bearer: String,
        @Body body: ReassignTicketRequest
    ): Response<Unit>

    @Multipart
    @POST("messages/sendMessage")
    suspend fun sendMessage(
        @Header("Authorization") bearer: String,
        @Part("ticket") ticket: RequestBody?,
        @Part("sender") sender: RequestBody,
        @Part("receiver") receiver: RequestBody?,
        @Part("content") content: RequestBody?,
        @Part image: MultipartBody.Part?
    )

    @PUT("messages/markAsRead")
    suspend fun markAsRead(
        @Header("Authorization") bearer: String,
        @Body body: MarkAsReadRequest
    ): Response<Unit>

    @POST("users/logout")
    suspend fun logout(
        @Header("Authorization") bearer: String,
        @Body req: LogoutRequest
    ): Response<Unit>

    @POST("users/updateToken")
    suspend fun updateToken(
        @Header("Authorization") bearer: String,
        @Body body: UpdateTokenRequest
    ): Response<Unit>

    @POST("users/reset-password")
    suspend fun changePassword(
        @Header("Authorization") bearer: String,
        @Body body: ChangePasswordRequest
    ): Response<Unit>

}

data class MarkAsReadRequest(
    val ticketId: Int,
    val userId: Int
)

data class ConcludeTicketRequest(val id: Int)

data class ReassignTicketRequest(
    val id: Int,
    val newResponsibleId: Int
)

data class ChangePasswordRequest(
    val username: String,
    val newPwd: String
)
