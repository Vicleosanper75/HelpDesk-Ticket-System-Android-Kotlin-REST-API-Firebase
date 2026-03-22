package com.example.ticketsaplication.ui

data class ContactUi(
    val id: String,
    val contactId: Int,
    val name: String,
    val lastMessage: String = "",
    val sendedAt: String = ""
)
