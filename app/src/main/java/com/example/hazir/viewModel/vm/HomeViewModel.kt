package com.example.hazir.viewModel.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hazir.data.DataPost
import com.example.hazir.data.GigData
import com.example.hazir.data.MessageModel
import com.example.hazir.data.UserData
import com.example.hazir.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(val firebaseAuth: FirebaseAuth, val firestore: FirebaseFirestore) : ViewModel(){

    private val _postData = MutableStateFlow<Resource<List<DataPost>>>(Resource.Unspecified())
    val postData : StateFlow<Resource<List<DataPost>>>
        get() = _postData.asStateFlow()

    private val _message = MutableStateFlow<Resource<MessageModel>>(Resource.Unspecified())
    val message : StateFlow<Resource<MessageModel>>
        get() = _message.asStateFlow()

    fun getPosts(){
        Log.d("khan","getting data")
        firestore.collection("posts").get()
            .addOnSuccessListener {
                val data =it.toObjects(DataPost::class.java)
                Log.d("khan","data size is ${data.size}")
                if(data!=null){
                    viewModelScope.launch {
                        _postData.emit(Resource.Success(data))
                    }
                }
                else
                {
                    viewModelScope.launch {
                        _postData.emit(Resource.Error("No data found"))
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _postData.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun updatePosts(dataPost: DataPost){
        firestore.collection("posts").document(dataPost.id).set(dataPost)
            .addOnSuccessListener {
                Log.d("khan","updated successfully")
            }.addOnFailureListener {
                Log.d("khan","update failed")
            }
    }

    fun getGigs(dataPost: DataPost){
        viewModelScope.launch {
            _message.emit(Resource.Loading())
        }
        firestore.collection("gigs").whereEqualTo("uid",firebaseAuth.uid)
            .get()
            .addOnSuccessListener {
                val data = it.toObjects(GigData::class.java)
                Log.d("khan","gigs is ${data}")
                if(data==null || data.size==0){
                    Log.d("khan","please first create a gig")
                    viewModelScope.launch {
                        _message.emit(Resource.Error("Create a gig to message"))
                    }
                }
                else
                {
                    Log.d("khan","creating new chat")
                    createChatOrGetChat(dataPost)
                }
            }
            .addOnFailureListener {
                Log.d("khan","error ${it.message.toString()}")
            }
    }
    fun createChatOrGetChat(dataPost: DataPost){
        Log.d("khan","user id is ${dataPost.uuid}")
        val userQuery = firestore.collection("chats")
            .whereEqualTo("userId",firebaseAuth.uid)
            .get()
            .addOnSuccessListener {
                val data = it.toObjects(MessageModel::class.java)
                Log.d("khan","data is ${data}")
                if(data!=null && data.size!=0){
                    getServiceProvider(data,dataPost)
                }
                else
                {
                    createChatInstance(dataPost)
                }
            }
            .addOnFailureListener {
                Log.d("khan","Error getting user ${it.message.toString()}")
            }
    }

    private fun createChatInstance(dataPost: DataPost) {
        val userRef = firestore.collection("users").document(FirebaseAuth.getInstance().uid.toString())
        val providerRef = firestore.collection("users").document(dataPost.uuid)
        val chatRef = firestore.collection("chats").document()
        var messageModel :MessageModel = MessageModel()
        firestore.runTransaction {
            val user = it.get(userRef).toObject(UserData::class.java)
            val provider = it.get(providerRef).toObject(UserData::class.java)
            Log.d("khan","user is ${user}")
            Log.d("khan","provider is ${provider}")
            if(user!=null && provider!=null){
                messageModel = MessageModel(chatRef.id,"",user.id,provider.id,user.image,provider.image,user.name,provider.name,
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
                viewModelScope.launch {
                    _message.emit(Resource.Success(messageModel))
                }
                Log.d("khan","Created new chat successfully")
            }
            .addOnFailureListener {
                Log.d("khan","Error Creating chat ${it.message.toString()}")
            }
    }

    private fun getServiceProvider(data: MutableList<MessageModel>, dataPost: DataPost) {
        firestore.collection("chats")
            .whereEqualTo("providerId",dataPost.uuid)
            .get()
            .addOnSuccessListener {
                val pro = it.toObjects(MessageModel::class.java)
                if(pro!=null && pro.size!=0){
                    val userSet = data.toSet()
                    val proSet = pro.toSet()
                    Log.d("khan","user is ${userSet}")
                    Log.d("khan","pro is  is ${proSet}")
                    val commonList = userSet.intersect(proSet).toList()
                    Log.d("khan","commonList is ${commonList}")
                    if(commonList.isNullOrEmpty()){
                        createChatInstance(dataPost)
                    }
                    else
                    {
                        Log.d("khan","chat already created ${commonList}")
                        viewModelScope.launch {
                            _message.emit(Resource.Success(commonList[0]))
                        }
                    }
                }
                else
                {
                    createChatInstance(dataPost)
                }
            }
            .addOnFailureListener {
                Log.d("khan","Error getting provider ${it.message.toString()}")
            }
    }



}