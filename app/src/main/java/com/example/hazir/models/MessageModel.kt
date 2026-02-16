package com.example.hazir.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

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
    var status : String = "",
    val userPhone : String = "",
    val providerPhone : String = "",
    val userAddress : String = "",
    val providerAddress : String = "",

) : Parcelable