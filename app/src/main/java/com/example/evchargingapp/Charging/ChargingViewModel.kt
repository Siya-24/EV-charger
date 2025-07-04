package com.example.evchargingapp.charging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.evchargingapp.charging.api.ChargingApiService
import com.example.evchargingapp.charging.api.ChargingRetrofitClient
import com.example.evchargingapp.charging.model.ChargingInfoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * ViewModel to manage charging info state and API interactions.
 */
class ChargingViewModel(private val pileId: String) : ViewModel() {

    private val apiService: ChargingApiService = ChargingRetrofitClient.instance

    private val _chargingInfo = MutableLiveData<ChargingInfoResponse>()
    val chargingInfo: LiveData<ChargingInfoResponse> get() = _chargingInfo

    /**
     * Fetch initial charger status from backend.
     */
    fun loadChargingInfo() {
        apiService.getChargingInfo(pileId).enqueue(object : Callback<ChargingInfoResponse> {
            override fun onResponse(call: Call<ChargingInfoResponse>, response: Response<ChargingInfoResponse>) {
                if (response.isSuccessful) {
                    _chargingInfo.value = response.body()
                }
            }

            override fun onFailure(call: Call<ChargingInfoResponse>, t: Throwable) {
                // Optionally log error
            }
        })
    }

    /**
     * Trigger the start charging API.
     */
    fun startCharging(callback: (Boolean, String?) -> Unit) {
        apiService.startCharging(pileId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                callback(response.isSuccessful, null)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                callback(false, t.message)
            }
        })
    }
}
