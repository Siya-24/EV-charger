package com.example.evchargingapp.repository

import com.example.evchargingapp.charging.api.ChargingApiService
import com.example.evchargingapp.charging.api.ChargingRetrofitClient
import com.example.evchargingapp.charging.model.ChargingInfoResponse
import retrofit2.Call

class ChargingRepository {
    private val apiService: ChargingApiService = ChargingRetrofitClient.instance

    fun getChargingInfo(pileId: String): Call<ChargingInfoResponse> {
        return apiService.getChargingInfo(pileId)
    }

    fun startCharging(pileId: String): Call<Void> {
        return apiService.startCharging(pileId)
    }
}
