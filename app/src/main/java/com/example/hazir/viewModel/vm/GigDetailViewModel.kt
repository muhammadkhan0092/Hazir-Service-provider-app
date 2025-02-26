package com.example.hazir.viewModel.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hazir.data.GigData
import com.example.hazir.data.MessageModel
import com.example.hazir.data.UserData
import com.example.hazir.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class GigDetailViewModel(val firebaseAuth: FirebaseAuth, val firestore: FirebaseFirestore) : ViewModel() {

    private lateinit var alreadyModel : MessageModel
    private val _messageCreate = MutableStateFlow<Resource<MessageModel>>(Resource.Unspecified())
    val messageCreate: StateFlow<Resource<MessageModel>>
        get() = _messageCreate.asStateFlow()

    suspend fun createNewChat(gig: GigData) {
        viewModelScope.launch {
            _messageCreate.emit(Resource.Loading())
        }
        val userId = firebaseAuth.currentUser?.uid
        val providerId = gig.uid
        val ref = firestore.collection("allchats").document()

        if (userId != null && providerId != null) {
            val alreadyExists = checkAlreadyCreated(userId, providerId) // now returns a boolean
            if (!alreadyExists) {
                Log.d("khan", "Creating New Chat")
                val refId = ref.id
                firestore
                    .collection("myChats")
                    .document(userId)
                    .collection("cid")
                    .document(refId)
                    .set(mapOf("chatReference" to refId))
                    .addOnSuccessListener { result ->
                        firestore
                            .collection("myChats")
                            .document(providerId)
                            .collection("cid")
                            .document(refId)
                            .set(mapOf("chatReference" to refId))
                            .addOnSuccessListener {
                                getProviderDetail(ref,userId,providerId,gig.id)
                            }
                            .addOnFailureListener {e->
                                Log.d("khan", "Error while referencing chat: ${e.message}")
                                viewModelScope.launch {
                                    _messageCreate.emit(Resource.Error("Error while referencing chat"))
                                }
                                firestore.collection("allchats").document(refId).delete()
                            }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("khan", "Error while referencing chat: ${exception.message}")
                        viewModelScope.launch {
                            _messageCreate.emit(Resource.Error("Error while referencing chat"))
                        }
                        firestore.collection("allchats").document(refId).delete()
                    }
            } else {
                Log.d("khan", "Already Created Chat")
                viewModelScope.launch {
                    _messageCreate.emit(Resource.Success(alreadyModel))
                }
            }
        } else {
            viewModelScope.launch {
                _messageCreate.emit(Resource.Error("User Id or Provider Id is null"))
            }
            Log.d("khan", "User Id or Provider Id is null")
        }
    }

    private fun getProviderDetail(
        ref: DocumentReference,
        userId: String,
        providerId: String,
        uid: String
    ) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener {
                val data = it.toObject(UserData::class.java)
                getUserDetail(ref,data,providerId,uid)
            }
            .addOnFailureListener {

            }
    }

    private fun getUserDetail(
        ref: DocumentReference,
        userData: UserData?,
        providerId: String,
        uid: String
    ) {
        firestore.collection("users").document(providerId).get()
            .addOnSuccessListener {
                val providerData = it.toObject(UserData::class.java)
                createChat(ref,userData,providerData,uid)
            }
            .addOnFailureListener {

            }
    }

    suspend fun checkAlreadyCreated(userId: String, providerId: String): Boolean {
        var alreadyExist = false
        try {
            val snapshot = firestore.collection("allchats").get().await()
            val data = snapshot.toObjects(MessageModel::class.java)

            data.forEach { currentItem ->
                Log.d("khan", "Current Item $currentItem")
                if (currentItem.userId == userId && currentItem.providerId == providerId) {
                    alreadyExist = true
                    alreadyModel = currentItem
                }
            }
        } catch (e: Exception) {
            Log.d("khan", "Verification Failed: ${e.message}")
        }
        return alreadyExist
    }

    private fun createChat(
        ref: DocumentReference,
        user: UserData?,
        provider: UserData?,
        uid: String
    ) {
        if (user != null && provider != null) {
        val messageModel = MessageModel(ref.id, "",user.id,provider.id,user.image,provider.image,user.name,provider.name,
            emptyList(),"chat"
        )
        ref.set(messageModel)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _messageCreate.emit(Resource.Success(messageModel))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _messageCreate.emit(Resource.Error("Error Occured While Creating MessageModel"))
                    Log.d("khan", "Error Occured While Creating MessageModel")
                }
            }
    }
    }


}