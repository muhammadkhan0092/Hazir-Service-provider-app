package com.example.hazir.data.sources

import com.example.hazir.utils.Result
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRemoteDataSource @Inject constructor(
    val firestore: FirebaseFirestore
) {
    suspend fun <T : Any> addData(
        documentId : String,
        collectionId : String,
        data : T
    ) : Result<Unit>{
        val result = firestore.collection(collectionId).document(documentId).set(data).await()
        return Result.Success(Unit)
    }

    suspend inline fun <reified T : Any> queryCollection(
        collectionPath: String,
        crossinline queryBuilder: (CollectionReference) -> Query
    ): List<T> {
        val query = queryBuilder(firestore.collection(collectionPath))
        val snapshot = query.get().await()
        return snapshot.documents.mapNotNull {
            it.toObject(T::class.java)
        }
    }
}