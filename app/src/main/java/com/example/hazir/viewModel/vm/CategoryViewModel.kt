package com.example.hazir.viewModel.vm

import android.util.Log
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

class CategoryViewModel(val firebaseAuth: FirebaseAuth,val firestore: FirebaseFirestore) : ViewModel(){

    private val _categoryData = MutableStateFlow<Resource<List<String>>>(Resource.Unspecified())
    val categoryData : StateFlow<Resource<List<String>>>
        get() = _categoryData.asStateFlow()

    init {
        getDistinctCategories()
    }

    fun getDistinctCategories() {
        firestore.collection("gigs")
            .get()
            .addOnSuccessListener { result ->
                val allCategories = mutableListOf<String>()
                result.documents.forEach { document ->
                    val category = document.getString("category")
                    if (category != null) {
                        allCategories.add(category)
                    }
                }
                val distinctCategories = allCategories.distinct()
                Log.d("khan", "Categories: $distinctCategories")
                viewModelScope.launch {
                    _categoryData.emit(Resource.Success(distinctCategories))
                }
            }
            .addOnFailureListener { exception ->
                viewModelScope.launch {
                    _categoryData.emit(Resource.Error(exception.message.toString()))
                }
            }
    }


}