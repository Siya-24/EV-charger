package com.example.evchargingapp.charging.api

import com.example.evchargingapp.charging.model.ChargingInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit API interface for charger-related operations.
 */
interface ChargingApiService {

    // Fetch current charging status info
    @GET("charging/{pileId}/info")
    fun getChargingInfo(@Path("pileId") pileId: String): Call<ChargingInfoResponse>

    // Trigger start charging
    @POST("charging/{pileId}/start")
    fun startCharging(@Path("pileId") pileId: String): Call<Void>
}
