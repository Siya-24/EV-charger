package com.example.evchargingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.evchargingapp.charging.ChargingViewModel

/**
 * Factory to create ChargingViewModel with custom pileId argument.
 */
class ChargingViewModelFactory(private val pileId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChargingViewModel::class.java)) {
            return ChargingViewModel(pileId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
