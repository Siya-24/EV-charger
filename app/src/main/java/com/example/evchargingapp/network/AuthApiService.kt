package com.example.evchargingapp.network

import com.example.evchargingapp.data.LoginRequest
import com.example.evchargingapp.data.LoginResponse
import com.example.evchargingapp.data.SignupRequest
import com.example.evchargingapp.data.SignupResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/api/signup")
    fun registerUser(@Body request: SignupRequest): Call<SignupResponse>

    @POST("/api/login")  // Adjust endpoint path accordingly
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>
}
