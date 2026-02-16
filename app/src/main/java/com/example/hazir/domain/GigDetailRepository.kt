package com.example.hazir.domain

import com.example.hazir.models.GigData
import com.example.hazir.utils.Result

interface GigDetailRepository {
    suspend fun createGig(
        gigData: GigData
    ): Result<Unit>
    suspend fun getGigs() : Result<List<GigData>>
}