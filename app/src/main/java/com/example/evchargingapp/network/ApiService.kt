package com.example.evchargingapp.network

import com.example.evchargingapp.network.ApiResponse
import com.example.evchargingapp.network.SendOtpRequest
import com.example.evchargingapp.network.VerifyOtpRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("sendOtp")
    fun sendOtp(@Body request: SendOtpRequest): Call<ApiResponse>
    // I want to send a POST request to /sendOtp on the server, and I’ll send some data in the body (like the email)

    //    👉 Translated in plain English:
    //    @POST("sendOtp") → Send a POST request to this endpoint.
    //
    //    @Body request: SendOtpRequest → The body of the message will be a SendOtpRequest object (which holds the email).
    //
    //    Call<ApiResponse> → I expect the server to reply with a message like: "OTP sent" or "Error" — wrapped in an ApiResponse

    @POST("verifyOtp")
    fun verifyOtp(@Body request: VerifyOtpRequest): Call<ApiResponse>
    //    📮 This one says:
    //
    //    “Now I want to verify the OTP. So I’ll send another POST request to /verifyOtp, and I’ll include both email and OTP in the body.”
}