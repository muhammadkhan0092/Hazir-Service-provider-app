package com.example.hazir.viewModel.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.hazir.data.GigData
import com.example.hazir.data.MessageModel
import com.example.hazir.data.UserData
import com.example.hazir.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GigDetailViewModel(val firebaseAuth: FirebaseAuth, val firestore: FirebaseFirestore) : ViewModel() {

    private lateinit var alreadyModel : MessageModel
    private val _messageCreate = MutableStateFlow<Resource<MessageModel>>(Resource.Unspecified())
    val messageCreate: StateFlow<Resource<MessageModel>>
        get() = _messageCreate.asStateFlow()


    fun createChatOrGetChat(gig: GigData){
        val userQuery = firestore.collection("chats")
            .whereEqualTo("userId",firebaseAuth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener {
                val data = it.toObjects(MessageModel::class.java)
                if(data!=null && data.size!=0){
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
                if(pro!=null && pro.size!=0){
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