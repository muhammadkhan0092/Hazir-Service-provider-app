package com.example.hazir.data.repository

import com.example.hazir.data.sources.FirebaseRemoteDataSource
import com.example.hazir.domain.PostRepository
import com.example.hazir.models.DataPost
import com.example.hazir.utils.Result
import com.example.hazir.utils.firebaseUpsertSafeCall
import javax.inject.Inject

class FirebasePostRepository @Inject constructor(
    private val dataSource: FirebaseRemoteDataSource
) : PostRepository {
    val collectionId = "posts"
    override suspend fun updatePost(post: DataPost): Result<Unit> {
        return firebaseUpsertSafeCall(
            action = {
                dataSource.addData<DataPost>(
                    documentId = post.id,
                    collectionId = collectionId,
                    data = post
                )
            }
        )
    }
}