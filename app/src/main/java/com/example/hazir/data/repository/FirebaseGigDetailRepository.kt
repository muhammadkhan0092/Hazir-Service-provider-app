package com.example.hazir.data.repository

import com.example.hazir.data.sources.FirebaseRemoteAuthSource
import com.example.hazir.data.sources.FirebaseRemoteDataSource
import com.example.hazir.domain.GitDetailRepository
import com.example.hazir.models.GigData
import com.example.hazir.utils.Result
import com.example.hazir.utils.firebaseUpsertSafeCall
import javax.inject.Inject

class FirebaseGigDetailRepository @Inject constructor(
    private val firebaseSource: FirebaseRemoteDataSource
) : GitDetailRepository {
    val collectionId = "gigs"
    override suspend fun createGig(
        gigData: GigData
    ): Result<Unit> {
        return firebaseUpsertSafeCall(
            action = {
                firebaseSource.addData(
                    documentId = gigData.id,
                    collectionId = collectionId,
                    data = gigData
                )
            }
        )
    }
}