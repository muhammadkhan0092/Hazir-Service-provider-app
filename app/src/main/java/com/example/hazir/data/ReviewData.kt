package com.example.hazir.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReviewData(val id : String, val image : String, val name : String, val content : String,val rating : String) : Parcelable