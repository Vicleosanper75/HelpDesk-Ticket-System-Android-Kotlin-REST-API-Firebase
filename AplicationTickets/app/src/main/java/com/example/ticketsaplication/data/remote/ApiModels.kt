package com.example.ticketsaplication.data.remote

import com.squareup.moshi.Json

data class LoginRequest(
    @Json(name = "username") val username: String,
    @Json(name = "pwd") val pss: String,
)
data class LoginResponse(
    @Json(name = "userId") val userId: Int?,
    @Json(name = "user") val user: UserDto?,
    @Json(name = "accessToken") val accessToken: String,
    @Json(name = "refreshToken") val refreshToken: String
)

data class GetMessagesRequest(
    @Json(name = "user") val user: Int,
    @Json(name = "contact") val contact: Int,
    @Json(name = "ticket") val ticket: Int
)

data class UserDto(
    @Json(name = "ID") val id: Int?,
    @Json(name = "USER_NAME") val userName: String?,
    @Json(name = "FULL_NAME") val fullName: String?,
    @Json(name = "ROLE") val role: Int?,
    @Json(name = "ROLE_NAME") val roleName: String?,
    @Json(name = "ACTIVE") val active: Int?
)
data class ContactDto(
    @Json(name = "ConversationId") val conversationId: String,
    @Json(name = "ContactId") val contactId: Int?,
    @Json(name = "Contact") val contactName: String?,
    @Json(name = "LastMessage") val lastMessage: String?,
    @Json(name = "SendedBy") val sendedBy: String?,
    @Json(name = "SendedAt") val sendedAt: String?,
    @Json(name = "TicketId") val ticketId: Int?,
    @Json(name = "TITLE") val title: String?,
    @Json(name = "STATE") val state: Int?,
    @Json(name = "CREATED_BY") val createdById: Int?,
    @Json(name = "RESPONSIBLE") val responsibleId: Int?,
    @Json(name = "UNREAD_COUNT") val unreadCount: Int? = 0
)

data class MessageDto(
    @Json(name = "SENDER_ID") val senderId: Int,
    @Json(name = "RECEIVER_ID") val receiverId: Int,
    @Json(name = "From") val from: String,
    @Json(name = "To") val to: String,
    @Json(name = "CONTENT") val content: String,
    @Json(name = "CREATED_AT") val createdAt: String,
    @Json(name = "READ") val read: Int? = 0
)

data class TicketDto(
    @Json(name = "ID") val id: Int,
    @Json(name = "TITLE") val title: String?,
    @Json(name = "RESPONSIBLE") val responsible: String?,
    @Json(name = "STATE") val state: Int,
    @Json(name = "CREATED_BY") val createdBy: String?,
    @Json(name = "CREATED_AT") val createdAt: String?,
)

data class CreateTicketRequest(
    @Json(name = "createdBy") val createdBy: Int,
    @Json(name = "title") val title: String,
    @Json(name = "responsible") val responsible: Int?
)

data class CreateTicketResponse(
    @Json(name = "ID") val id: Int
)

data class SendMessageRequest(
    @Json(name = "ticket") val ticketId: Int?,
    @Json (name = "sender") val senderId: Int,
    @Json(name = "receiver") val receiverId: Int?,
    @Json(name = "content") val content: String
)

data class ChatDto(
    @Json(name = "TICKET_ID") val ticketId: Int,
    @Json(name = "TICKET_TITLE") val ticketTitle: String,
    @Json(name = "CONTACT_ID") val contactId: Int,
    @Json(name = "CONTACT_NAME") val contactName: String,
    @Json(name = "last_message") val lastMessage: String?,
    @Json(name = "last_message_timestamp") val lastMessageTimestamp: String?
)

data class RoleDto(
    @Json(name = "ID") val id: Int,
    @Json(name = "ROLE_NAME") val roleName: String
)


data class LogoutRequest(
    @Json(name = "token") val token: String
)

data class UpdateTokenRequest(
    @Json(name = "userId") val userId: Int,
    @Json(name = "fcmToken") val token: String
)
