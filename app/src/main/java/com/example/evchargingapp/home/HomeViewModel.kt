package com.example.evchargingapp.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.evchargingapp.data.ChargingPile
import com.example.evchargingapp.data.MyDatabaseHelper
import com.google.firebase.database.*

/**
 * ViewModel responsible for managing Charging Pile data and applying filters.
 * Uses both SQLite and Firebase Realtime Database.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    // Database helper for local SQLite operations
    private val dbHelper = MyDatabaseHelper(application.applicationContext)

    // LiveData exposed to UI to observe filtered charging piles
    private val _allPiles = MutableLiveData<List<ChargingPile>>()
    val filteredPiles: LiveData<List<ChargingPile>> = _allPiles

    // In-memory full list of charging piles (source of truth)
    private var fullList: List<ChargingPile> = emptyList()

    // Firebase Realtime Database reference
    private val dbRef: DatabaseReference = FirebaseDatabase
        .getInstance("https://evse-170a5-default-rtdb.asia-southeast1.firebasedatabase.app")
        .getReference("piles")

    init {
        // Load from SQLite and Firebase when ViewModel is initialized
        loadPilesFromLocalDB()
        syncFromFirebase()  // keeps Firebase as source of truth
    }

    /**
     * Load all charging piles from local SQLite database.
     */
    private fun loadPilesFromLocalDB() {
        fullList = dbHelper.getAllPiles()
        _allPiles.value = fullList
    }

    /**
     * Fetch charging piles from Firebase and update both memory and SQLite DB.
     */
    private fun syncFromFirebase() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val firebaseList = mutableListOf<ChargingPile>()

                for (child in snapshot.children) {
                    val pile = child.getValue(ChargingPile::class.java)
                    if (pile != null) {
                        firebaseList.add(pile)
                        dbHelper.insertPile(pile.id, pile.name, pile.isOnline) // sync to SQLite
                    }
                }

                fullList = firebaseList
                _allPiles.value = fullList
            }

            override fun onCancelled(error: DatabaseError) {
                // Log error or show feedback (optional)
            }
        })
    }

    /**
     * Show all charging piles (no filters applied).
     */
    fun filterAll() {
        _allPiles.value = fullList
    }

    /**
     * Filter and show only "Online" charging piles.
     */
    fun filterOnline() {
        _allPiles.value = fullList.filter { it.isOnline }
    }

    /**
     * Filter and show only "Offline" charging piles.
     */
    fun filterOffline() {
        _allPiles.value = fullList.filter { !it.isOnline }
    }

    /**
     * Adds a new charging pile to in-memory list and refreshes UI.
     * This is called after adding to Firebase/SQLite.
     */
    fun addChargingPile(pile: ChargingPile) {
        fullList = fullList + pile
        _allPiles.value = fullList
    }
}
