package com.example.hazir.models.state

import com.example.hazir.models.GigData

data class CategoryDetailState(
    val isLoading : Boolean = false,
    val gigs : List<GigData>? = null
)
