package com.example.hazir.viewModel.vm

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.hazir.data.GigData
import com.example.hazir.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateGigViewModel(val firestore: FirebaseFirestore,val firebaseStorage : FirebaseStorage) : ViewModel(){

    private val _createGig = MutableStateFlow<Resource<GigData>>(Resource.Unspecified())
    val createGig : StateFlow<Resource<GigData>>
        get() = _createGig.asStateFlow()

    private val _sendProfile = MutableStateFlow<Resource<String>>(Resource.Unspecified())
    val sendProfile : StateFlow<Resource<String>>
        get() = _sendProfile.asStateFlow()
     var downloadUrls : MutableList<String>
    init {
        downloadUrls = mutableListOf()
    }

    fun createGig(
        gigData: GigData
    ) {
       viewModelScope.launch {
           _createGig.emit(Resource.Loading())
       }
        firestore.collection("gigs").document(gigData.id).set(gigData)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _createGig.emit(Resource.Success(gigData))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _createGig.emit(Resource.Error(it.message.toString()))
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