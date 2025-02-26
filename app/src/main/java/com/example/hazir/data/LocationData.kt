package com.example.hazir.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationData(
    val longitude : Double = 0.0,
    val latitiude : Double = 0.0
) : Parcelable
