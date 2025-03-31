package com.example.hazir.viewModel.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hazir.data.GigData
import com.example.hazir.data.HistoryData
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

    private val _set = MutableStateFlow<Resource<String>>(Resource.Unspecified())
    val set : StateFlow<Resource<String>>
        get() = _set.asStateFlow()


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




    fun updateMessageModelAndupdateGigAndUpdateHistory(
        messageModel: MessageModel,
        gigId: String,
        gigData: GigData,
        history: HistoryData
    ){
        viewModelScope.launch {
            _set.emit(Resource.Loading())
        }
        val msgRef = firestore.collection("allchats").document(messageModel.id)
        val gigRef = firestore.collection("gigs").document(gigId)
        val historyRef = firestore.collection("history").document(history.id)
        firestore.runBatch {batch->
            batch.set(msgRef,messageModel)
            batch.set(gigRef,gigData)
            batch.set(historyRef,history)
        }
            .addOnSuccessListener {
                viewModelScope.launch {
                    _set.emit(Resource.Success("done"))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _set.emit(Resource.Error(it.message.toString()))
                }
            }
    }


}