package com.example.hazir.models.sealed

sealed interface CreateGigEvents {
    data object GigSuccess : CreateGigEvents
    data class Toast(val message : String) : CreateGigEvents
}