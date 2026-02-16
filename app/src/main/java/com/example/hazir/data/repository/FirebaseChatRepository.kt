package com.example.hazir.data.repository

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.hazir.data.sources.FirebaseRemoteAuthSource
import com.example.hazir.data.sources.FirebaseRemoteDataSource
import com.example.hazir.domain.ChatRepository
import com.example.hazir.domain.GitDetailRepository
import com.example.hazir.models.GigData
import com.example.hazir.models.MessageModel
import com.example.hazir.models.UserData
import com.example.hazir.utils.Resource
import com.example.hazir.utils.Result
import com.example.hazir.utils.firebaseSafeCall
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.toSet

class FirebaseChatRepository @Inject constructor(
    private val firebaseSource: FirebaseRemoteDataSource,
    private val firebaseAuthSource : FirebaseRemoteAuthSource
) : ChatRepository {
    val documentId = "chats"
    override suspend fun createChatOrGetChat(gig: GigData): Result<List<MessageModel>> {
        val isUserLoggedIn = firebaseAuthSource.isUerLoggedIn()
        return when(isUserLoggedIn){
            true -> {
                firebaseSafeCall<MessageModel>(
                    action = {
                        firebaseSource.queryCollection<MessageModel>(
                            collectionPath = documentId,
                            {
                                it.document(firebaseAuthSource.getUserId())
                                it
                            }
                        )
                    }
                )
            }
            false -> Result.Error("")
        }
    }

    override suspend fun createChatInstance(gig: GigData) {

    }
}