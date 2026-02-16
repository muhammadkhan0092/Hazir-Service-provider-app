package com.example.hazir.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataCategories(
   val image :Int = 0,
   val color : String = "",
   val name : String = ""
) : Parcelable
