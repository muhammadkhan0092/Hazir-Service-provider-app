package com.example.hazir.data.sources

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.hazir.utils.Resource
import com.example.hazir.utils.Result
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.text.get

class FirebaseRemoteDataSource @Inject constructor(
    val firestore: FirebaseFirestore
) {
    suspend fun <T : Any> addData(
        documentId : String,
        collectionId : String,
        data : T
    ) : Result<Unit>{
        firestore.collection(collectionId).document(documentId).set(data).await()
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
    suspend inline fun <reified T : Any> get(
        collectionPath: String,
        documentId: String
    ): T{
        val result = firestore
            .collection(collectionPath)
            .document(documentId)
            .get()
            .await()
        return result.toObject(T::class.java)?:throw Exception("User Null")
    }

    suspend fun getString(collectionId: String,documentId: String): List<String> {
        val result = firestore.collection(collectionId)
            .get()
            .await()
        return result.documents.mapNotNull {document->
            document.getString(documentId)
        }
    }
}