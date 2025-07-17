package com.example.evchargingapp.charging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.evchargingapp.charging.model.ChargingInfoResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log

/**
 * ViewModel to interact with the ChargingRepository and expose data to the UI.
 */
class ChargingViewModel(
    private val pileId: String,
    private val repository: ChargingRepository
) : ViewModel() {

    private val _chargingInfo = MutableLiveData<ChargingInfoResponse>()
    val chargingInfo: LiveData<ChargingInfoResponse> get() = _chargingInfo

    private var pollingJob: Job? = null

    /**
     * Loads current charging status once.
     */
    fun loadChargingInfo() {
        repository.getChargingInfo(pileId).enqueue(object : Callback<ChargingInfoResponse> {
            override fun onResponse(
                call: Call<ChargingInfoResponse>,
                response: Response<ChargingInfoResponse>
            ) {
                if (response.isSuccessful) {
                    val info = response.body()
                    if (info != null) {
                        _chargingInfo.postValue(
                            ChargingInfoResponse(
                                id = info.id, // ✅ Required field
                                temperature = info.temperature,
                                voltage = info.voltage,
                                current = info.current,
                                energy = info.energy,
                                time = info.time,
                                isCharging = info.isCharging
                            )
                        )
                    }
                } else {
                    Log.e("ChargingViewModel", "loadChargingInfo failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ChargingInfoResponse>, t: Throwable) {
                Log.e("ChargingViewModel", "loadChargingInfo network error: ${t.localizedMessage}")
            }
        })
    }

    /**
     * 🔁 Start periodic polling every 5 seconds.
     */
    fun startPollingChargingInfo() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                loadChargingInfo()
                delay(5000)
            }
        }
    }

    /**
     * ❌ Stop polling when not needed.
     */
    fun stopPollingChargingInfo() {
        pollingJob?.cancel()
    }

    /**
     * Starts the charging session by hitting the backend.
     */
    fun startCharging(callback: (Boolean, String?) -> Unit) {
        repository.startCharging(pileId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseStr = response.body()?.string()
                Log.d("ChargingViewModel", "Start Charging Response: $responseStr")

                if (response.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, "Response code: ${response.code()}, error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("ChargingViewModel", "startCharging error: ${t.localizedMessage}")
                callback(false, "Network error: ${t.localizedMessage}")
            }
        })
    }

    /**
     * Stops the charging session by hitting the backend.
     */
    fun stopCharging(callback: (Boolean, String?) -> Unit) {
        repository.stopCharging(pileId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseStr = response.body()?.string()
                Log.d("ChargingViewModel", "Stop Charging Response: $responseStr")

                if (response.isSuccessful) {
                    stopPollingChargingInfo()
                    callback(true, null)
                } else {
                    callback(false, "Response code: ${response.code()}, error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("ChargingViewModel", "stopCharging error: ${t.localizedMessage}")
                callback(false, "Network error: ${t.localizedMessage}")
            }
        })
    }
}
