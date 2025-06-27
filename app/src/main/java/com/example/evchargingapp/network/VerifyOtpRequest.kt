package com.example.evchargingapp.network

data class VerifyOtpRequest(
    val email: String,
    val otp: String
)