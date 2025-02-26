package com.example.hazir.viewModel.vmf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hazir.viewModel.vm.CreateGigViewModel
import com.example.hazir.viewModel.vm.EditProfileViewModel
import com.example.hazir.viewModel.vm.SignInViewModel
import com.example.hazir.viewModel.vm.SignUpViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EditProfileFactory(val firestore: FirebaseFirestore, val firebaseStorage: FirebaseStorage) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EditProfileViewModel(firestore,firebaseStorage) as T
    }
}