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
            val alreadyExists = checkAlreadyCreated(userId, providerId)
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


    fun createChatOrGetChat(gig: GigData){
        val userQuery = firestore.collection("chats")
            .whereEqualTo("userId",firebaseAuth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener {
                val data = it.toObjects(MessageModel::class.java)
                if(data!=null){
                    getServiceProvider(data,gig)
                }
                else
                {
                   createChatInstance(gig)
                }
            }
            .addOnFailureListener {
                Log.d("khan","Error getting user ${it.message.toString()}")
            }
        val providerQuery = firestore.collection("chats")
            .whereEqualTo("providerId",gig.uid)

    }

    private fun createChatInstance(gig: GigData) {
        val userRef = firestore.collection("users").document(FirebaseAuth.getInstance().uid.toString())
        val providerRef = firestore.collection("users").document(gig.uid)
        val chatRef = firestore.collection("chats").document()
        firestore.runTransaction {
            val user = it.get(userRef).toObject(UserData::class.java)
            val provider = it.get(providerRef).toObject(UserData::class.java)
            Log.d("khan","user is ${user}")
            Log.d("khan","provider is ${provider}")
            if(user!=null && provider!=null){
                val messageModel = MessageModel(chatRef.id,"",user.id,provider.id,user.image,provider.image,user.name,provider.name,
                    emptyList(),"chat"
                )
                it.set(chatRef,messageModel)
            }
            else
            {
                Log.d("khan","something is null")
            }
        }
            .addOnSuccessListener {
                Log.d("khan","Created new chat successfully")
            }
            .addOnFailureListener {
                Log.d("khan","Error Creating chat ${it.message.toString()}")
            }
    }

    private fun getServiceProvider(data: MutableList<MessageModel>, gig: GigData) {
        firestore.collection("chats")
            .whereEqualTo("providerId",gig.uid)
            .get()
            .addOnSuccessListener {
                val pro = it.toObjects(MessageModel::class.java)
                if(pro!=null){
                    val userSet = data.toSet()
                    val proSet = pro.toSet()
                    val commonList = userSet.union(proSet).toList()
                    Log.d("khan","commonList is ${commonList}")
                    if(commonList.isNullOrEmpty()){
                        createChatInstance(gig)
                    }
                    else
                    {
                       Log.d("khan","already created")
                    }
                }
                else
                {
                    createChatInstance(gig)
                }
            }
            .addOnFailureListener {
                Log.d("khan","Error getting provider ${it.message.toString()}")
            }
    }



}