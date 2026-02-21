package com.example.hazir.viewModel.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hazir.domain.ChatRepository
import com.example.hazir.models.MessageModel
import com.example.hazir.models.state.AllChatState
import com.example.hazir.utils.Resource
import com.example.hazir.utils.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {
    private val _state = MutableStateFlow(AllChatState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 1)
    val events = _events.asSharedFlow()
    fun getChats() {
        viewModelScope.launch(Dispatchers.IO){
            chatRepository.getChats().collectLatest {result->
                when(result){
                    is Result.Error -> {
                        withContext(Dispatchers.Main){
                            _events.emit(result.error)
                            _state.update {
                                it.copy(isLoading = false)
                            }
                        }
                    }
                    is Result.Success-> {
                        withContext(Dispatchers.Main){
                            _state.update {
                                it.copy(isLoading = false,result.data)
                            }
                        }
                    }
                }
            }
        }
    }

    init {
        getChats()
    }
}
