package com.example.evchargingapp.charging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.evchargingapp.charging.model.ChargingInfoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * ViewModel to interact with the ChargingRepository and expose data to the UI.
 */
class ChargingViewModel(
    private val pileId: String,
    private val repository: ChargingRepository
) : ViewModel() {

    private val _chargingInfo = MutableLiveData<ChargingInfoResponse>()
    val chargingInfo: LiveData<ChargingInfoResponse> get() = _chargingInfo

    /**
     * Loads current charging status.
     */
    fun loadChargingInfo() {
        repository.getChargingInfo(pileId).enqueue(object : Callback<ChargingInfoResponse> {
            override fun onResponse(call: Call<ChargingInfoResponse>, response: Response<ChargingInfoResponse>) {
                if (response.isSuccessful) {
                    _chargingInfo.value = response.body()
                }
            }

            override fun onFailure(call: Call<ChargingInfoResponse>, t: Throwable) {
                // Handle failure (optional)
            }
        })
    }

    /**
     * Starts the charging session.
     */
    fun startCharging(callback: (Boolean, String?) -> Unit) {
        repository.startCharging(pileId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                callback(response.isSuccessful, null)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                callback(false, t.message)
            }
        })
    }

    /**
     * Stops the charging session.
     */
    fun stopCharging(callback: (Boolean, String?) -> Unit) {
        repository.stopCharging(pileId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                callback(response.isSuccessful, null)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                callback(false, t.message)
            }
        })
    }
}
