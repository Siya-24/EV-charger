package com.example.evchargingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.evchargingapp.charging.ChargingViewModel
import com.example.evchargingapp.charging.ChargingRepository

/**
 * Factory to create ChargingViewModel with custom pileId and repository.
 */
class ChargingViewModelFactory(
    private val pileId: String,
    private val repository: ChargingRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChargingViewModel::class.java)) {
            return ChargingViewModel(pileId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



/**
 * Since Android’s default ViewModel system doesn’t support passing custom arguments directly, we build a factory that knows how to create your ChargingViewModel with that pileId
 * */

