package com.example.evchargingapp.data
// LoginRequest.kt
data class LoginRequest(
    val email: String,
    val password: String
)

// LoginResponse.kt
data class LoginResponse(
    val success: Boolean,
    val message: String?,
    val username: String?,
    val token: String?  // or other fields your backend returns
)
