package com.example.hazir.data.repository

import com.example.hazir.data.sources.FirebaseRemoteAuthSource
import com.example.hazir.data.sources.FirebaseRemoteDataSource
import com.example.hazir.domain.GigDetailRepository
import com.example.hazir.models.GigData
import com.example.hazir.utils.Result
import com.example.hazir.utils.firebaseGetSafeCall
import com.example.hazir.utils.firebaseListSafeCall
import com.example.hazir.utils.firebaseUpsertSafeCall
import javax.inject.Inject

class FirebaseGigDetailRepository @Inject constructor(
    private val firebaseSource: FirebaseRemoteDataSource,
    private val firebaseAuthSource : FirebaseRemoteAuthSource
) : GigDetailRepository {
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
    override suspend fun getGigs() : Result<List<GigData>>{
        val isUserLoggedIn = firebaseAuthSource.isUerLoggedIn()
        return when(isUserLoggedIn){
            true -> {
                val userId = firebaseAuthSource.getUserId()
                return firebaseListSafeCall<GigData>(
                    action = {
                        firebaseSource.queryCollection<GigData>(
                            collectionPath = "collectionId",
                            {
                                it.whereEqualTo("uid",userId)
                            }
                        )
                    }
                )
            }
            false -> Result.Error("")
        }
    }
    override suspend fun getGigFromGigId(
        gigId : String
    ) : Result<GigData>{
        val isUserLoggedIn = firebaseAuthSource.isUerLoggedIn()
        return when(isUserLoggedIn){
            true -> {
                return firebaseGetSafeCall<GigData>(
                    action = {
                        firebaseSource.get<GigData>(
                            collectionPath = collectionId,
                            documentId = gigId
                        )
                    }
                )
            }
            false -> Result.Error("")
        }
    }
}