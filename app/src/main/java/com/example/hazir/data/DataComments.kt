package com.example.hazir.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataComments(
    val id: String = "",
    val content : String ="",
    val uuid:String = ""

) : Parcelable
