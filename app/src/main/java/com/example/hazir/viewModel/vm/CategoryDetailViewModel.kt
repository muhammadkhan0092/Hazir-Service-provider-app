package com.example.hazir.viewModel.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hazir.data.GigData
import com.example.hazir.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryDetailViewModel(val firebaseAuth: FirebaseAuth, val firestore: FirebaseFirestore) : ViewModel(){

    private val _gigData = MutableStateFlow<Resource<List<GigData>>>(Resource.Unspecified())
    val gigData : StateFlow<Resource<List<GigData>>>
        get() = _gigData.asStateFlow()

    fun getSpecificCategoryDetail(category : String) {
        firestore.collection("gigs")
            .whereEqualTo("category",category)
            .get()
            .addOnSuccessListener { result ->
                val allGigs = mutableListOf<GigData>()
                result.documents.forEach { document ->
                    val gig = document.toObject(GigData::class.java)
                    if (gig != null) {
                        allGigs.add(gig)
                    }
                }
                viewModelScope.launch {
                    _gigData.emit(Resource.Success(allGigs))
                }

            }
            .addOnFailureListener { exception ->
                viewModelScope.launch {
                    _gigData.emit(Resource.Error(exception.message.toString()))
                }
            }
    }


}