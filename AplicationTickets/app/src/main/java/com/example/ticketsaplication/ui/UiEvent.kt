package com.example.ticketsaplication.ui

sealed class UiEvent {
    data object Success: UiEvent()
    data object NavigateBack: UiEvent()
    data class ShowToast(val message: String) : UiEvent()
}
