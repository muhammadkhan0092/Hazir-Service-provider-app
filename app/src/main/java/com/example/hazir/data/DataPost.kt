package com.example.hazir.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataPost(
    val id: String = "",
    val name : String="",
    val uuid:String="",
    val likes : List<String> = emptyList(),
    val comments : List<DataComments> = emptyList(),
    val userImage : String = "",
    val thumbnail : String="",
    val content : String =""
) : Parcelable
