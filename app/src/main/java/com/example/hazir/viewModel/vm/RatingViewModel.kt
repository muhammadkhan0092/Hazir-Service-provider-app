package com.example.hazir.viewModel.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hazir.domain.GigDetailRepository
import com.example.hazir.models.GigData
import com.example.hazir.models.HistoryData
import com.example.hazir.models.MessageModel
import com.example.hazir.models.RatingsState
import com.example.hazir.utils.Resource
import com.example.hazir.utils.Result
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RatingViewModel @Inject constructor(
    val firestore: FirebaseFirestore,
    private val gigDetailRepository: GigDetailRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RatingsState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<String>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val events = _events.asSharedFlow()

    private val _set = MutableStateFlow<Resource<String>>(Resource.Unspecified())
    val set: StateFlow<Resource<String>>
        get() = _set.asStateFlow()


    fun getGigDetail(
        gigId: String
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            val gigResult = gigDetailRepository.getGigFromGigId(gigId)
            when (gigResult) {
                is Result.Error -> _events.emit(gigResult.error)
                is Result.Success -> {
                    _state.update {
                        it.copy(isLoading = false, gigData = gigResult.data)
                    }
                }
            }
        }
    }


    fun updateMessageModelAndupdateGigAndUpdateHistory(
        messageModel: MessageModel,
        gigId: String,
        gigData: GigData,
        history: HistoryData
    ) {
        viewModelScope.launch {
            _set.emit(Resource.Loading())
        }
        val msgRef = firestore.collection("chats").document(messageModel.id)
        val gigRef = firestore.collection("gigs").document(gigId)
        val historyRef = firestore.collection("history").document(history.id)
        firestore.runBatch { batch ->
            batch.set(msgRef, messageModel)
            batch.set(gigRef, gigData)
            batch.set(historyRef, history)
        }
            .addOnSuccessListener {
                viewModelScope.launch {
                    _set.emit(Resource.Success("done"))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _set.emit(Resource.Error(it.message.toString()))
                }
            }
    }


}