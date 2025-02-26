package com.example.hazir.viewModel.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hazir.data.GigData
import com.example.hazir.data.MessageModel
import com.example.hazir.data.SingleMessage
import com.example.hazir.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MessageDetailViewModel(
    val firebaseAuth: FirebaseAuth,
    val firestore: FirebaseFirestore
) : ViewModel() {

    private val _messageCreate = MutableStateFlow<Resource<List<SingleMessage>>>(Resource.Unspecified())
    val messageCreate: StateFlow<Resource<List<SingleMessage>>> = _messageCreate.asStateFlow()

    private val _sendMessage = MutableStateFlow<Resource<List<SingleMessage>>>(Resource.Unspecified())
    val sendMessage: StateFlow<Resource<List<SingleMessage>>> = _sendMessage.asStateFlow()

    private val _sendStatus = MutableStateFlow<Resource<MessageModel>>(Resource.Unspecified())
    val sendStatus: StateFlow<Resource<MessageModel>> = _sendStatus.asStateFlow()

    private val _getGigs = MutableStateFlow<Resource<List<GigData>>>(Resource.Unspecified())
    val getGigs: StateFlow<Resource<List<GigData>>> = _getGigs.asStateFlow()

    private val messagesCollection = firestore.collection("allchats")
    private var messageListenerRegistration: ListenerRegistration? = null
    fun retrieveMessages(model: MessageModel) {
        messageListenerRegistration = messagesCollection
            .document(model.id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _messageCreate.value = Resource.Error(error.localizedMessage)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val messages = snapshot.toObject(MessageModel::class.java)
                    if(messages!=null){
                        val msges = messages.messages
                        _messageCreate.value = Resource.Success(msges)
                    }

                } else {
                    _messageCreate.value = Resource.Error("No messages found.")
                }
            }
    }
    fun addNewMessage(messageModel : MessageModel) {
        viewModelScope.launch {
            _sendMessage.emit(Resource.Loading())
        }
        messagesCollection.document(messageModel.id).set(messageModel)
            .addOnSuccessListener {
                Log.d("khan","set message with content ${messageModel}")
                viewModelScope.launch {
                    _sendMessage.emit(Resource.Success(messageModel.messages))
                }
                Log.d("khan","message sent")
            }
            .addOnFailureListener { error ->
                viewModelScope.launch {
                    _sendMessage.emit(Resource.Error(error.message.toString()))
                }
                Log.d("khan","message not sent")
            }
    }
    override fun onCleared() {
        super.onCleared()
        messageListenerRegistration?.remove()
    }

    fun changeStatusToOrdered(messageModel: MessageModel) {
        viewModelScope.launch {
            _sendStatus.emit(Resource.Loading())
        }
        messagesCollection.document(messageModel.id).set(messageModel)
            .addOnSuccessListener {
                Log.d("khan","status changed to ${messageModel.status}")
                viewModelScope.launch {
                    _sendStatus.emit(Resource.Success(messageModel))
                }
            }
            .addOnFailureListener { error ->
                viewModelScope.launch {
                    _sendStatus.emit(Resource.Error(error.message.toString()))
                }
                Log.d("khan","STATUS NOT SEND")
            }
    }

    fun getGigs(id : String) {
        viewModelScope.launch {
            _getGigs.emit(Resource.Loading())
        }
        val list : MutableList<GigData>  = mutableListOf()
        firestore.collection("gigs").get()
            .addOnSuccessListener {
                it.forEach {document->
                    val obj = document.toObject(GigData::class.java)
                    if(obj.uid == id){
                        list.add(obj)
                    }
                }
                viewModelScope.launch {
                    _getGigs.emit(Resource.Success(list))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _getGigs.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}
