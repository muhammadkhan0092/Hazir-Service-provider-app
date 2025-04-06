package com.example.hazir.viewModel.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hazir.data.UserData
import com.example.hazir.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(val firestore: FirebaseFirestore, val firebaseAuth: FirebaseAuth ) : ViewModel(){



    private val _sendLogout = MutableStateFlow<Resource<String>>(Resource.Unspecified())
    val sendLogout : StateFlow<Resource<String>>
        get() = _sendLogout.asStateFlow()

    private val _getUser = MutableStateFlow<Resource<UserData>>(Resource.Unspecified())
    val getUser : StateFlow<Resource<UserData>>
        get() = _getUser.asStateFlow()

    init {
        getUser()
    }
    fun logout(){
        viewModelScope.launch {
            _sendLogout.emit(Resource.Loading())
        }
        firebaseAuth.signOut()
        viewModelScope.launch {
            _sendLogout.emit(Resource.Success(""))
        }
    }

    fun getUser(){
        viewModelScope.launch {
            _getUser.emit(Resource.Loading())
        }
        firestore.collection("users").whereEqualTo("id",firebaseAuth.uid)
            .get()
            .addOnSuccessListener {
                val data = it.toObjects(UserData::class.java)
                viewModelScope.launch {
                    _getUser.emit(Resource.Success(data.first()))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _getUser.emit(Resource.Error(it.message.toString()))
                }
            }
    }


}