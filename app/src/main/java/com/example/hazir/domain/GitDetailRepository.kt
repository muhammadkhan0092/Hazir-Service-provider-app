package com.example.hazir.domain

import com.example.hazir.models.GigData
import com.example.hazir.utils.Result

interface GitDetailRepository {
    suspend fun createGig(
        gigData: GigData
    ): Result<Unit>
}