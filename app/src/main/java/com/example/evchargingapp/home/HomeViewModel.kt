package com.example.evchargingapp.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.evchargingapp.data.ChargingPile
import com.example.evchargingapp.data.MockChargingPileData

class HomeViewModel : ViewModel() {

    private val _allPiles = MutableLiveData<List<ChargingPile>>(MockChargingPileData.pileList)
    val filteredPiles: LiveData<List<ChargingPile>> = _allPiles

    fun filterAll() {
        _allPiles.value = MockChargingPileData.pileList
    }

    fun filterOnline() {
        _allPiles.value = MockChargingPileData.pileList.filter { it.isOnline }
    }

    fun filterOffline() {
        _allPiles.value = MockChargingPileData.pileList.filter { !it.isOnline }
    }

    fun addChargingPile(pile: ChargingPile) {
        val current = _allPiles.value?.toMutableList() ?: mutableListOf()
        current.add(pile)
        _allPiles.value = current

    }

}
