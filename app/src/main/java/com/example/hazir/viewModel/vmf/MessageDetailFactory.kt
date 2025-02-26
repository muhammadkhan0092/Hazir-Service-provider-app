package com.example.hazir.viewModel.vmf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hazir.viewModel.vm.CategoryDetailViewModel
import com.example.hazir.viewModel.vm.GigDetailViewModel
import com.example.hazir.viewModel.vm.MessageDetailViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MessageDetailFactory(val firebaseAuth: FirebaseAuth, val firebaseFirestore: FirebaseFirestore) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MessageDetailViewModel(firebaseAuth,firebaseFirestore) as T
    }
}