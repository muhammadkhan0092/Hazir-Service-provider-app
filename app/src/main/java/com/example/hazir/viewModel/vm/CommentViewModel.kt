package com.example.hazir.viewModel.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hazir.data.DataPost
import com.example.hazir.data.GigData
import com.example.hazir.data.UserData
import com.example.hazir.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CommentViewModel(val firebaseAuth: FirebaseAuth, val firestore: FirebaseFirestore) : ViewModel(){

    private val _postData = MutableStateFlow<Resource<DataPost>>(Resource.Unspecified())
    val postData : StateFlow<Resource<DataPost>>
        get() = _postData.asStateFlow()

    fun updatePost(post: DataPost) {
        val userRef = firestore.collection("users").document(firebaseAuth.uid.toString())
        val postRef = firestore.collection("posts").document(post.id)
        firestore.runTransaction {
            val data = it.get(userRef).toObject(UserData::class.java)
            if (data == null) {
                viewModelScope.launch {
                    _postData.emit(Resource.Error("Error Retreiving user data"))
                }
            } else {
                it.set(postRef, post)
            }
        }
            .addOnSuccessListener {
                viewModelScope.launch {
                    _postData.emit(Resource.Success(post))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _postData.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun getGigs(post: DataPost){
        firestore.collection("gigs").whereEqualTo("uid",firebaseAuth.currentUser?.uid)
            .get()
            .addOnSuccessListener {
                val data = it.toObjects(GigData::class.java)
                if(data.size==0){
                    viewModelScope.launch {
                        _postData.emit(Resource.Error("Create atleast one gig to comment"))
                    }
                }
                else
                {
                    updatePost(post)
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _postData.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}