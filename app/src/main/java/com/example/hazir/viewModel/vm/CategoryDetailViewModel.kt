package com.example.hazir.viewModel.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hazir.domain.GigDetailRepository
import com.example.hazir.models.GigData
import com.example.hazir.models.state.CategoryDetailState
import com.example.hazir.utils.Resource
import com.example.hazir.utils.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CategoryDetailViewModel @Inject constructor(
    private val gigDetailRepository: GigDetailRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CategoryDetailState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<String>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val events = _events.asSharedFlow()

    fun getSpecificCategoryDetail(category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _state.update {
                    it.copy(isLoading = true)
                }
            }
            val result = gigDetailRepository.getAllGigsOfOneCategory(category)
            when (result) {
                is Result.Error<*> -> {
                    withContext(Dispatchers.Main) {
                        _state.update {
                            it.copy(isLoading = false)
                        }
                        _events.emit(result.error)
                    }
                }
                is Result.Success -> {
                    withContext(Dispatchers.Main) {
                        _state.update {
                            it.copy(isLoading = false, gigs = result.data)
                        }
                    }
                }
            }
        }
    }
}