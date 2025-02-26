package com.example.hazir.viewModel.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hazir.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignInViewModel(val firebaseAuth: FirebaseAuth) : ViewModel(){

    private val _login = MutableStateFlow<Resource<String>>(Resource.Unspecified())
    val login : StateFlow<Resource<String>>
        get() = _login.asStateFlow()
    fun loginWithEmailAndPass(
        email: String,
        password : String
    ) {
       viewModelScope.launch {
           _login.emit(Resource.Loading())
       }
        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                viewModelScope.launch {
                    it.user?.let {
                        _login.emit(Resource.Success(it.uid))
                    }
                }

            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _login.emit(Resource.Error(it.message.toString()))
                }

            }
    }

}