package com.example.evchargingapp.charging.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Separate Retrofit instance for charging-related endpoints.
 */

//This file sets up your Retrofit client — it's like saying:
//
//“Here’s how to connect to our charging server using HTTP.”
//
//It provides one place in your app where you configure how to talk to your backend.
object ChargingRetrofitClient {
    private const val BASE_URL = "https://8ae3-203-115-104-172.ngrok-free.app/"  // Replace with real server IP

    val instance: ChargingApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // it tells retrofit that : “When you see JSON, use Gson to turn it into Kotlin data classes (and vice versa).
            .build()
            .create(ChargingApiService::class.java) // Use that interface we made (ChargingApiService) to know what endpoints exist
            // so after this file the above  creates chargingapiservice So you can easily call startCharging() or getChargingInfo() anywhere
    }

}
