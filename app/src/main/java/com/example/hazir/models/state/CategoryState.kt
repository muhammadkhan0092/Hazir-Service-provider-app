package com.example.hazir.models.state

data class CategoryState(
    val categories : List<String> = emptyList(),
    val isLoading : Boolean = true
)
