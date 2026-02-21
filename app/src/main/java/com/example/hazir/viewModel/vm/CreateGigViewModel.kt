package com.example.hazir.viewModel.vm

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.hazir.domain.GigDetailRepository
import com.example.hazir.models.GigData
import com.example.hazir.models.sealed.CreateGigEvents
import com.example.hazir.models.state.CreateGigState
import com.example.hazir.utils.Resource
import com.example.hazir.utils.Result
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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
class CreateGigViewModel @Inject constructor(
    val firestore: FirebaseFirestore,
    val gigDetailRepository: GigDetailRepository
) : ViewModel(){
    private val _sendProfile = MutableStateFlow<Resource<String>>(Resource.Unspecified())
    val sendProfile : StateFlow<Resource<String>>
        get() = _sendProfile.asStateFlow()
     var downloadUrls : MutableList<String>
    init {
        downloadUrls = mutableListOf()
    }
    private val _events = MutableSharedFlow<CreateGigEvents>(replay = 0, extraBufferCapacity = 1)
    val events = _events.asSharedFlow()
    private val _state = MutableStateFlow(CreateGigState())
    val state = _state.asStateFlow()
    fun createGig(
        gigData: GigData
    ) {
        viewModelScope.launch(Dispatchers.IO){
            withContext(Dispatchers.IO){
                _state.update {
                    it.copy(isLoading = true)
                }
            }
            val result = gigDetailRepository.createGig(gigData)
            withContext(Dispatchers.Main){
                _state.update {
                    it.copy(isLoading = false)
                }
            }
            when(result){
                is Result.Error<*> -> {
                    withContext(Dispatchers.Main){
                        _events.emit(CreateGigEvents.Toast(result.error))
                    }
                }
                is Result.Success<*> -> {
                    withContext(Dispatchers.Main){
                        _events.emit(CreateGigEvents.GigSuccess)
                    }
                }
            }
        }
    }

     fun getRealPathFromUri(imageUri: Uri?, activity: Activity): String? {
        val cursor: Cursor? = activity.contentResolver.query(imageUri!!, null, null, null, null)
        return if (cursor == null) {
            imageUri.path
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(idx)
        }
    }


    fun uploadToCloudinary(filepath: String, context: Context, onComplete: () -> Unit,isProfle : Boolean) {
        MediaManager.get().upload(filepath).callback(object : UploadCallback {
            override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                val downloadUrl = resultData?.get("url") as? String
                if (downloadUrl != null) {
                    if(isProfle){
                        viewModelScope.launch {
                            _sendProfile.emit(Resource.Success(downloadUrl))
                        }
                    }
                    downloadUrls.add(downloadUrl)
                    Log.d("Cloudinary", "Download URL: $downloadUrl")
                } else {
                }
                onComplete()
            }

            override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
            }

            override fun onReschedule(requestId: String?, error: ErrorInfo?) {
            }

            override fun onError(requestId: String?, error: ErrorInfo?) {
                Toast.makeText(context, "Task Not successful: $error", Toast.LENGTH_SHORT).show()
                onComplete()
            }

            override fun onStart(requestId: String?) {
            }
        }).dispatch()
    }



}