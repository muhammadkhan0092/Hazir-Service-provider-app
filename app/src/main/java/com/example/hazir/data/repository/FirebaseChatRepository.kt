package com.example.hazir.data.repository

import com.example.hazir.data.sources.FirebaseRemoteAuthSource
import com.example.hazir.data.sources.FirebaseRemoteDataSource
import com.example.hazir.domain.ChatRepository
import com.example.hazir.models.GigData
import com.example.hazir.models.MessageModel
import com.example.hazir.utils.Result
import com.example.hazir.utils.firebaseListSafeCall
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FirebaseChatRepository @Inject constructor(
    private val firebaseSource: FirebaseRemoteDataSource,
    private val firebaseAuthSource : FirebaseRemoteAuthSource
) : ChatRepository {
    val collectionId = "chats"
    override suspend fun createChatOrGetChat(gig: GigData): Result<List<MessageModel>> {
        val isUserLoggedIn = firebaseAuthSource.isUerLoggedIn()
        return when(isUserLoggedIn){
            true -> {
                firebaseListSafeCall<MessageModel>(
                    action={
                        firebaseSource.queryCollection<MessageModel>(
                            collectionPath = collectionId,
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
    override fun getChats(): Flow<Result<List<MessageModel>>> {
        val uuid = firebaseAuthSource.getUserId()
        return firebaseSource.listenWhereEqualTo<MessageModel>(
            collection = collectionId,
            field = "userId",
            value = uuid
        )
    }
}