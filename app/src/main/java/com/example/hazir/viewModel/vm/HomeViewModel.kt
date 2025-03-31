package com.example.hazir.viewModel.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hazir.data.DataPost
import com.example.hazir.data.GigData
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

    fun getPosts(){
        firestore.collection("posts").get()
            .addOnSuccessListener {
                val data =it.toObjects(DataPost::class.java)
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




}