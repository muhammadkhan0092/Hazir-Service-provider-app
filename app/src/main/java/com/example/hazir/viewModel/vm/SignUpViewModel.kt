package com.example.hazir.viewModel.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hazir.data.LocationData
import com.example.hazir.utils.Resource
import com.example.hazir.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(val firebaseAuth : FirebaseAuth,val firestore: FirebaseFirestore) : ViewModel() {
    private val _register = MutableStateFlow<Resource<String>>(Resource.Unspecified())
    val register : StateFlow<Resource<String>>
        get() = _register

    fun registerUser(
        name: String,
        username: String,
        email: String,
        phone: String,
        cnic: String,
        city : String,
        password : String,
        locationData: LocationData
    ) {
        viewModelScope.launch {
            _register.emit(Resource.Loading())
        }
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    val uiid = firebaseAuth.currentUser?.uid
                    firestore.runBatch {
                        val user =
                            uiid?.let { it1 ->
                                UserData(it1,name,username,email,phone,city,cnic, locationData = locationData)
                            }
                        if (uiid != null) {
                            if (user != null) {
                                firestore.collection("users").document(uiid).set(user)
                            }
                        }
                    }.addOnSuccessListener {
                        viewModelScope.launch {
                            _register.emit(Resource.Success("success"))
                        }

                    }
                        .addOnFailureListener {
                            if (uiid != null) {
                                rollbackUser(uiid)
                            }
                        }
                } else {
                    viewModelScope.launch {
                        _register.emit(Resource.Error("Sign Up Failed"))
                    }
                }
            }
    }

    private fun rollbackUser(uid: String?) {
        if (uid != null) {
            firebaseAuth.currentUser?.delete()
                ?.addOnSuccessListener {
                    viewModelScope.launch {
                        _register.emit(Resource.Error("User not created"))
                    }
                }
                ?.addOnFailureListener { e ->
                    viewModelScope.launch {
                        _register.emit(Resource.Error("User Created But data is empty"))
                    }
                }
        } else {

        }
    }


    fun signOut() {
        firebaseAuth.signOut()
    }

    fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null
}