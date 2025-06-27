package com.example.evchargingapp.data

data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val otp: String = "" // added this to store opt from the object instance of user class created in the registerActivity kotlin file
)