package com.example.hazir.viewModel.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hazir.domain.CategoryRepository
import com.example.hazir.models.state.CategoryState
import com.example.hazir.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CategoryState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 1)
    val events = _events.asSharedFlow()
    init {
        getDistinctCategories()
    }

    fun getDistinctCategories() {
        viewModelScope.launch(Dispatchers.IO){
            val result = categoryRepository.getDistinctCategories()
            when(result){
                is Result.Error -> {
                    withContext(Dispatchers.Main){
                        _events.emit(result.error)
                    }
                }
                is Result.Success -> {
                    withContext(Dispatchers.Main){
                        _state.update {
                            it.copy(isLoading = false, categories = result.data)
                        }
                    }
                }
            }
        }
    }
}