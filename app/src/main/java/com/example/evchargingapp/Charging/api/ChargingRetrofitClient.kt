package com.example.evchargingapp.charging.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Separate Retrofit instance for charging-related endpoints.
 */
object ChargingRetrofitClient {
    private const val BASE_URL = "https://91e2-14-97-86-6.ngrok-free.app/"  // Replace with real server IP

    val instance: ChargingApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChargingApiService::class.java)
    }
}
