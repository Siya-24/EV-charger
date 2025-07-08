package com.example.evchargingapp.charging

import ChargingApiService
import com.example.evchargingapp.charging.api.ChargingRetrofitClient
import com.example.evchargingapp.charging.model.ChargingInfoResponse
import okhttp3.ResponseBody
import retrofit2.Call

/**
 * Repository class to handle all network interactions related to EV charging.
 */
class ChargingRepository {

    private val apiService: ChargingApiService = ChargingRetrofitClient.instance

    fun getChargingInfo(pileId: String): Call<ChargingInfoResponse> {
        return apiService.getChargingInfo(pileId)
    }

    fun startCharging(pileId: String): Call<ResponseBody> {
        return apiService.startCharging(pileId)
    }

    fun stopCharging(pileId: String): Call<ResponseBody> {
        return apiService.stopCharging(pileId)
    }
}
