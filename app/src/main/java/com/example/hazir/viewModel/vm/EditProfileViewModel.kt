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
import com.example.hazir.data.UserData
import com.example.hazir.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditProfileViewModel(val firestore: FirebaseFirestore, val firebaseStorage : FirebaseStorage) : ViewModel(){



    private val _sendProfile = MutableStateFlow<Resource<String>>(Resource.Unspecified())
    val sendProfile : StateFlow<Resource<String>>
        get() = _sendProfile.asStateFlow()

    private val _sendUser = MutableStateFlow<Resource<UserData>>(Resource.Unspecified())
    val sendUser : StateFlow<Resource<UserData>>
        get() = _sendUser.asStateFlow()

    private val _updateUser = MutableStateFlow<Resource<UserData>>(Resource.Unspecified())
    val updateUser : StateFlow<Resource<UserData>>
        get() = _updateUser.asStateFlow()

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


    fun uploadToCloudinary(filepath: String, context: Context, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _updateUser.emit(Resource.Loading())
        }
        MediaManager.get().upload(filepath).callback(object : UploadCallback {
            override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                val downloadUrl = resultData?.get("url") as? String
                if (downloadUrl != null) {
                    viewModelScope.launch {
                            onSuccess(downloadUrl)
                        }

                }
            }

            override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
            }

            override fun onReschedule(requestId: String?, error: ErrorInfo?) {
            }

            override fun onError(requestId: String?, error: ErrorInfo?) {
                Toast.makeText(context, "Task Not successful: $error", Toast.LENGTH_SHORT).show()
            }

            override fun onStart(requestId: String?) {
            }
        }).dispatch()
    }



    fun updateUser(data: UserData) {
        viewModelScope.launch {
            _updateUser.emit(Resource.Loading())
        }
        firestore.collection("users").document(data.id).set(data)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _updateUser.emit(Resource.Success(data))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _updateUser.emit(Resource.Error(it.message.toString()))
                }
            }
    }


}