package com.example.hazir.viewModel.vmf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hazir.viewModel.vm.SignInViewModel
import com.example.hazir.viewModel.vm.SignUpViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpFactory(val firebaseAuth: FirebaseAuth,val firestore: FirebaseFirestore) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignUpViewModel(firebaseAuth,firestore) as T
    }
}