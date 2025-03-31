package com.example.hazir.viewModel.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hazir.data.GigData
import com.example.hazir.data.HistoryData
import com.example.hazir.data.MessageModel
import com.example.hazir.utils.Resource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(val firestore: FirebaseFirestore, val firebaseStorage : FirebaseStorage) : ViewModel(){



    private val _get = MutableStateFlow<Resource<MutableList<HistoryData>>>(Resource.Unspecified())
    val get : StateFlow<Resource<MutableList<HistoryData>>>
        get() = _get.asStateFlow()






    fun updateMessageModelAndupdateGigAndUpdateHistory(
        uuid:String
    ){
        Log.d("khan","uuid is ${uuid}")
        viewModelScope.launch {
            _get.emit(Resource.Loading())
        }
        val sellerRef = firestore.collection("history")
            .whereEqualTo("sellerId",uuid)
        val buyerRef = firestore.collection("history")
            .whereEqualTo("buyerId",uuid)
        sellerRef.get()
            .addOnSuccessListener {sellerSnap->
                Log.d("khan","seller snap sucess")
            buyerRef.get()
                .addOnSuccessListener {buyerSnap->
                    Log.d("khan","buyer snap sucess")
                   val sellerData = sellerSnap.toObjects(HistoryData::class.java)
                    val buyerData = buyerSnap.toObjects(HistoryData::class.java)
                    val combined = sellerData+buyerData
                    Log.d("khan","combined is ${combined}")
                    if(combined!=null){
                        viewModelScope.launch {
                            _get.emit(Resource.Success(combined.toMutableList()))
                        }
                    }
            }
                .addOnFailureListener {
                    Log.d("khan","buyer snap failure ${it.message}")
                }
        }.addOnFailureListener {
                Log.d("khan","seller snap failure ${it.message}")
                viewModelScope.launch {
                    _get.emit(Resource.Error(it.message.toString()))
                }
        }
    }


}