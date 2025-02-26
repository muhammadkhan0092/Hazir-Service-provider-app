package com.example.hazir.viewModel.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hazir.data.MessageModel
import com.example.hazir.data.SingleMessage
import com.example.hazir.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MessageViewModel(
    val firebaseAuth: FirebaseAuth,
    val firestore: FirebaseFirestore
) : ViewModel() {

    private val _retreiveMessages = MutableStateFlow<Resource<List<MessageModel>>>(Resource.Unspecified())
    val retreiveMessages: StateFlow<Resource<List<MessageModel>>> = _retreiveMessages.asStateFlow()


    private val messagesCollection = firestore
    private var messageListenerRegistration: ListenerRegistration? = null

    fun getChatsId() {
        viewModelScope.launch {
            _retreiveMessages.emit(Resource.Loading())
        }
        messageListenerRegistration = messagesCollection
            .collection("myChats")
            .document(firebaseAuth.uid!!)
            .collection("cid")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _retreiveMessages.emit(Resource.Error("Error Fetching Messages"))
                    }
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val data = snapshot.documents.mapNotNull { document ->
                        val map = document.data as? Map<String, String>
                        map
                    }
                    val chatReferences : MutableList<String> = mutableListOf()
                    data.forEach { map ->
                        val chatReference = map["chatReference"]
                        if (chatReference != null) {
                            chatReferences.add(chatReference)
                            Log.d("khan", "Chat Reference: $chatReference")
                        } else {
                            Log.d("khan", "No chatReference found in the document.")
                        }
                    }
                    if(chatReferences.isEmpty()){
                        viewModelScope.launch {
                            _retreiveMessages.emit(Resource.Error("No Messages Found"))
                        }
                    }
                    else
                    {
                        getChatsFromId(chatReferences)
                    }
                } else {
                    viewModelScope.launch {
                        _retreiveMessages.emit(Resource.Error("No Messages Found"))
                    }
                }
            }
    }



    fun getChatsFromId(chatReferences: MutableList<String>) {
        firebaseAuth.uid?.let {
            messagesCollection
                .collection("allchats")
                .addSnapshotListener{snapshot,error->
                    if (snapshot != null) {
                        val list : MutableList<MessageModel> = mutableListOf()
                        val model = snapshot.documents
                        model.forEach {
                            val item = it.toObject(MessageModel::class.java)
                            if (item != null) {
                                if(item.id in chatReferences){
                                    list.add(item)
                                }
                            }
                        }
                        viewModelScope.launch {
                            _retreiveMessages.emit(Resource.Success(list))
                        }
                    }
                    if(error!=null){
                        viewModelScope.launch {
                            _retreiveMessages.emit(Resource.Error("Error Fetching data"))
                        }
                    }
                }
        }
    }
    override fun onCleared() {
        super.onCleared()
        messageListenerRegistration?.remove() // Unregister listener
    }
}
