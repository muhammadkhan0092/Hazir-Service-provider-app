package com.example.hazir.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserData(
    val id : String = "",
    val name : String = "",
    val username : String = "",
    val email : String = "",
    val phone : String = "",
    val city : String = "",
    val cnic : String = "",
    val image : String = "",
    val locationData : LocationData = LocationData()
) : Parcelable