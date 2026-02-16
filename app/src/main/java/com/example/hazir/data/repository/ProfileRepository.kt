package com.example.hazir.data.repository

import com.example.hazir.data.sources.FirebaseRemoteDataSource
import com.example.hazir.models.UserData
import com.example.hazir.utils.Result
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val firebaseSource: FirebaseRemoteDataSource
) {
    val collectionId = "users"
    suspend fun updateUser(data: UserData): Result<Unit> {
        return firebaseSource.addData(data.id,collectionId,data)
    }
}