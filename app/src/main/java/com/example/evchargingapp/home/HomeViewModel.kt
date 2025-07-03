package com.example.evchargingapp.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.evchargingapp.data.ChargingPile
import com.example.evchargingapp.data.MyDatabaseHelper

/**
 * ViewModel responsible for managing Charging Pile data and applying filters.
 * Uses SQLite via MyDatabaseHelper to persist data.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    // Database helper instance
    private val dbHelper = MyDatabaseHelper(application.applicationContext)

    // LiveData that the UI observes
    private val _allPiles = MutableLiveData<List<ChargingPile>>()  // current filtered view
    val filteredPiles: LiveData<List<ChargingPile>> = _allPiles

    // Full list of piles stored in memory
    private var fullList: List<ChargingPile> = emptyList()

    init {
        // Load existing charging piles from SQLite when ViewModel starts
        loadPilesFromDatabase()
    }

    /**
     * Load all charging piles from local SQLite database and update LiveData.
     */
    private fun loadPilesFromDatabase() {
        fullList = dbHelper.getAllPiles()  // read from DB
        _allPiles.value = fullList         // display in UI
    }

    /**
     * Show all charging piles (no filter).
     */
    fun filterAll() {
        _allPiles.value = fullList
    }

    /**
     * Show only charging piles that are marked as "Online".
     */
    fun filterOnline() {
        _allPiles.value = fullList.filter { it.isOnline }
    }

    /**
     * Show only charging piles that are marked as "Offline".
     */
    fun filterOffline() {
        _allPiles.value = fullList.filter { !it.isOnline }
    }

    /**
     * Add a new charging pile to the in-memory list and update UI.
     * (Assumes DB insertion already succeeded.)
     */
    fun addChargingPile(pile: ChargingPile) {
        fullList = fullList + pile           // update memory list
        _allPiles.value = fullList           // refresh view
    }
}
