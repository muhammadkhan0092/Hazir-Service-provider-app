package com.example.hazir.data.repository

import com.example.hazir.data.sources.FirebaseRemoteAuthSource
import com.example.hazir.data.sources.FirebaseRemoteDataSource
import com.example.hazir.domain.UserRepository
import com.example.hazir.models.UserData
import com.example.hazir.utils.Result
import com.example.hazir.utils.firebaseGetSafeCall
import com.example.hazir.utils.firebaseListSafeCall
import javax.inject.Inject

class FirebaseUserRepository @Inject constructor(
    private val authSource : FirebaseRemoteAuthSource,
    private val dataSource : FirebaseRemoteDataSource
)  : UserRepository{
    val collectionId = "users"
    override suspend fun getUser(): Result<UserData> {
        val isUserLoggedIn = authSource.isUerLoggedIn()
        return when(isUserLoggedIn){
            true -> {
                val userId = authSource.getUserId()
                firebaseGetSafeCall<UserData>(
                    action = {
                        dataSource.get<UserData>(
                            collectionPath = collectionId,
                            documentId =userId
                        )
                    }
                )
            }
            false -> Result.Error("")
        }
    }
}