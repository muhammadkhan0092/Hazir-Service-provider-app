package com.example.hazir.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GigData(
    var id: String = "",
    val uid: String = "",
    val profileImage: String = "",
    val gigImages: List<String> = emptyList(),
    val totalOrders: Int = 0,
    val category: String = "",
    val description: String = "",
    val startingPrice: String = "",
    val services: List<String> = emptyList(),
    val reviews : MutableList<ReviewData> = mutableListOf(),
    val title:String = ""
) : Parcelable
