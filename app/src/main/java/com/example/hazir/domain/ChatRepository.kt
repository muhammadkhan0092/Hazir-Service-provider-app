package com.example.hazir.domain

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.hazir.models.GigData
import com.example.hazir.models.MessageModel
import com.example.hazir.models.UserData
import com.example.hazir.utils.Resource
import com.example.hazir.utils.Result
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.collections.toSet

interface ChatRepository {

    suspend fun createChatOrGetChat(gig: GigData) : Result<List<MessageModel>>
    suspend fun createChatInstance(gig: GigData)
    fun getChats(): Flow<Result<List<MessageModel>>>

}