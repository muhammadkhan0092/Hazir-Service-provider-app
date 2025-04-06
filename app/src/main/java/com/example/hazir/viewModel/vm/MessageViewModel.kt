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
            .collection("chats")
            .whereEqualTo("userId",firebaseAuth.uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _retreiveMessages.emit(Resource.Error("Error Fetching Messages"))
                    }
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val data = snapshot.toObjects(MessageModel::class.java)
                    if(data==null || data.size==0){
                        viewModelScope.launch {
                            _retreiveMessages.emit(Resource.Error("No Messages Found"))
                        }
                    }
                    else
                    {
                        viewModelScope.launch {
                            _retreiveMessages.emit(Resource.Success(data))
                        }
                    }
                }else {
                    viewModelScope.launch {
                        _retreiveMessages.emit(Resource.Error("No Messages Found"))
                    }
                }
            }
    }




    override fun onCleared() {
        super.onCleared()
        messageListenerRegistration?.remove()
    }
}
