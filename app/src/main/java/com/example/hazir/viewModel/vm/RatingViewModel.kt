package com.example.hazir.viewModel.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hazir.data.GigData
import com.example.hazir.data.MessageModel
import com.example.hazir.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RatingViewModel(val firestore: FirebaseFirestore, val firebaseStorage : FirebaseStorage) : ViewModel(){

    private val _getGig = MutableStateFlow<Resource<GigData>>(Resource.Unspecified())
    val getGig : StateFlow<Resource<GigData>>
        get() = _getGig.asStateFlow()

    private val _setModel = MutableStateFlow<Resource<String>>(Resource.Unspecified())
    val setModel : StateFlow<Resource<String>>
        get() = _setModel.asStateFlow()

    private val _setGig = MutableStateFlow<Resource<String>>(Resource.Unspecified())
    val setGig : StateFlow<Resource<String>>
        get() = _setGig.asStateFlow()


    fun getGigDetail(
        gigId : String
    ) {
       viewModelScope.launch {
           _getGig.emit(Resource.Loading())
       }
        Log.d("khan","gig id is ${gigId}")
        firestore.collection("gigs").document(gigId).get()
            .addOnSuccessListener {
                val document = it.toObject(GigData::class.java)
                Log.d("khan","mil gya ${document}")
                if(document!=null) {
                    viewModelScope.launch {
                        _getGig.emit(Resource.Success(document))
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _getGig.emit(Resource.Error(it.message.toString()))
                }
            }


    }

    fun setGigData(gigId: String, gigData: GigData) {
        viewModelScope.launch {
            _setGig.emit(Resource.Loading())
        }
        firestore.collection("gigs").document(gigId).set(gigData)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _setGig.emit(Resource.Success("DATA UPDATED"))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _setGig.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun updateMessageModel(messageModel: MessageModel) {
        viewModelScope.launch {
            _setModel.emit(Resource.Loading())
        }
        messageModel.status = "chat"
        firestore.collection("allchats").document(messageModel.id).set(messageModel)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _setModel.emit(Resource.Success("done"))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _setModel.emit(Resource.Error(it.message.toString()))
                }
            }
    }


}