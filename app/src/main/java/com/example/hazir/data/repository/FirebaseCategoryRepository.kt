package com.example.hazir.data.repository

import com.example.hazir.data.sources.FirebaseRemoteDataSource
import com.example.hazir.domain.CategoryRepository
import com.example.hazir.models.GigData
import com.example.hazir.utils.Result
import com.example.hazir.utils.firebaseListSafeCall
import javax.inject.Inject

class FirebaseCategoryRepository @Inject constructor(
    private val firebaseDataSource : FirebaseRemoteDataSource
) : CategoryRepository {
    val collectionId = "gigs"
    override suspend fun getSpecificCategoryDetail(category: String): Result<List<GigData>> {
        return firebaseListSafeCall<GigData>(
            action ={
                firebaseDataSource.queryCollection<GigData>(
                    collectionPath = collectionId,
                    { it.whereEqualTo("category", category)}
                )
            }
        )
    }
    override suspend fun getDistinctCategories(): Result<List<String>> {
        val documentId = "category"
        return firebaseListSafeCall<String>(
            action = {firebaseDataSource.getString(collectionId,documentId)}
        )
    }
}