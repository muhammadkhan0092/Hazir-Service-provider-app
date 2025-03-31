package com.example.hazir.data
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
)