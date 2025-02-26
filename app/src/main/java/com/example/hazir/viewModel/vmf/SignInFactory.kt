package com.example.hazir.viewModel.vmf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hazir.viewModel.vm.SignInViewModel
import com.google.firebase.auth.FirebaseAuth

class SignInFactory(val firebaseAuth: FirebaseAuth) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignInViewModel(firebaseAuth) as T
    }
}