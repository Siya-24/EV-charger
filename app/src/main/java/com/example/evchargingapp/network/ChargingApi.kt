package com.example.evchargingapp.network

import com.example.evchargingapp.data.ChargingStatus
import com.example.evchargingapp.data.StartResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChargingApi {

    /**
     * Get real-time charging data for a specific pile.
     */
    @GET("charging/status/{pileId}")
    fun getStatus(@Path("pileId") pileId: String): Call<ChargingStatus>

    /**
     * Start charging request for the specified pile.
     */
    @POST("charging/start/{pileId}")
    fun startCharging(@Path("pileId") pileId: String): Call<StartResponse>
}
