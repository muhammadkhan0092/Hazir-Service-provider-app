package com.example.hazir.viewModel.vm

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.hazir.data.DataPost
import com.example.hazir.data.UserData
import com.example.hazir.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CreatePostViewModel(val firebaseAuth: FirebaseAuth, val firestore: FirebaseFirestore) : ViewModel(){

    private val _sendPost = MutableStateFlow<Resource<DataPost>>(Resource.Unspecified())
    val sendPost : StateFlow<Resource<DataPost>>
        get() = _sendPost.asStateFlow()

    private val _sendProfile = MutableStateFlow<Resource<String>>(Resource.Unspecified())
    val sendProfile : StateFlow<Resource<String>>
        get() = _sendProfile.asStateFlow()

    fun setPost(url: String, content: String) {
        val uuid = UUID.randomUUID().toString()
        val userRef = firestore.collection("users").document(firebaseAuth.uid.toString())
        val postRef = firestore.collection("posts").document(uuid)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val user = snapshot.toObject(UserData::class.java)

            if (user == null) {
                viewModelScope.launch {
                    _sendPost.emit(Resource.Error("Could not post data"))
                }
                return@runTransaction null // Ensure transaction returns a value
            }

            val pD =DataPost(uuid,user.name,user.id, emptyList(),
                emptyList(),user.image,url,content
            )

            transaction.set(postRef, pD)
            return@runTransaction pD // Ensure a valid return value
        }.addOnSuccessListener { pD ->
            if (pD != null) {
                viewModelScope.launch {
                    _sendPost.emit(Resource.Success(pD))
                }
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _sendPost.emit(Resource.Error(it.message.toString()))
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


    fun uploadToCloudinary(filepath: String, context: Context) {
        viewModelScope.launch {
            _sendProfile.emit(Resource.Loading())
        }
        MediaManager.get().upload(filepath).callback(object : UploadCallback {
            override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                val downloadUrl = resultData?.get("url") as? String
                if (downloadUrl != null) {
                    viewModelScope.launch {
                            _sendProfile.emit(Resource.Success(downloadUrl))
                    }
                }
                else {
                }

            }

            override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
            }

            override fun onReschedule(requestId: String?, error: ErrorInfo?) {
            }

            override fun onError(requestId: String?, error: ErrorInfo?) {
                viewModelScope.launch {
                    _sendProfile.emit(Resource.Error(error.toString()))
                }
                Toast.makeText(context, "Task Not successful: $error", Toast.LENGTH_SHORT).show()
            }

            override fun onStart(requestId: String?) {
            }
        }).dispatch()
    }





}