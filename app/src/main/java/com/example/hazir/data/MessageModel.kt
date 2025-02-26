package com.example.hazir.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class MessageModel(
    val id : String = "",
    var gigId : String ="",
    val userId : String = "",
    val providerId : String = "",
    val userImage : String = "",
    val providerImage : String = "",
    val userName : String = "",
    val providerName : String ="",
    var messages : List<SingleMessage> = mutableListOf(),
    var status : String = ""
) : Parcelable