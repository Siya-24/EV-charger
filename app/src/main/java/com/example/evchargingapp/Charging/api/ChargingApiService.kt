package com.example.evchargingapp.charging.api

import com.example.evchargingapp.charging.model.ChargingInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit API interface for charger-related operations.
 */

//This file defines an interface for making network requests to your backend using Retrofit.
//
//In short, it tells your app:
//
//“Here’s how to call the charging API to check status and start charging.
interface ChargingApiService {

    // Fetch current charging status info
    @GET("charging/{pileId}/info")
    fun getChargingInfo(@Path("pileId") pileId: String): Call<ChargingInfoResponse>

    // Trigger start charging
    @POST("charging/{pileId}/start")
    fun startCharging(@Path("pileId") pileId: String): Call<Void>

    @POST("charging/{pileId}/stop")
    fun stopCharging(@Path("pileId") pileId: String): Call<Void>

}
