package com.example.hazir.domain

import com.example.hazir.models.GigData
import com.example.hazir.utils.Result
import com.example.hazir.utils.firebaseGetSafeCall

interface GigDetailRepository {
    suspend fun createGig(
        gigData: GigData
    ): Result<Unit>
    suspend fun getGigs() : Result<List<GigData>>
    suspend fun getGigFromGigId(
        gigId : String
    ) : Result<GigData>
}