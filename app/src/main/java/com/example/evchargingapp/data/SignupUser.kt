package com.example.evchargingapp.data

data class SignupRequest(
    val username: String,
    val email: String,
    val password: String
)

data class SignupResponse(
    val success: Boolean,
    val message: String
)
