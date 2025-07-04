package com.example.evchargingapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OtpRetrofitClient {
    private const val BASE_URL = "https://otp-api.free.beeceptor.com"  //currently this is the mock api url

    val instance: OtpApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OtpApiService::class.java)
    }
}