package com.example.evchargingapp

data class VerifyOtpRequest(
    val email: String,
    val otp: String
)