package com.example.evchargingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.evchargingapp.charging.ChargingViewModel

/**
 * Factory to create ChargingViewModel with custom pileId argument.
 */

/**
 * Since Android’s default ViewModel system doesn’t support passing custom arguments directly, we build a factory that knows how to create your ChargingViewModel with that pileId
 * */
class ChargingViewModelFactory(private val pileId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChargingViewModel::class.java)) {
            return ChargingViewModel(pileId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
