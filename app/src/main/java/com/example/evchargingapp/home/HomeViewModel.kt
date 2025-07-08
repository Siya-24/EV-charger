package com.example.evchargingapp.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.evchargingapp.data.ChargingPile
import com.example.evchargingapp.data.MyDatabaseHelper
import com.google.firebase.database.*

/**
 * ViewModel managing Charging Pile data and filters.
 * Syncs data between Firebase Realtime Database and local SQLite DB (user-scoped).
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    // SQLite DB helper instance
    private val dbHelper = MyDatabaseHelper(application.applicationContext)

    // LiveData to expose filtered piles list to UI
    private val _allPiles = MutableLiveData<List<ChargingPile>>()
    val filteredPiles: LiveData<List<ChargingPile>> = _allPiles

    // In-memory full list of charging piles (source for filters)
    private var fullList: List<ChargingPile> = emptyList()

    init {
        // Load from local DB and sync with Firebase on ViewModel creation
        loadPilesFromLocalDB()
        syncFromFirebase()
    }

    /**
     * Loads all piles from local SQLite DB for the current user.
     */
    private fun loadPilesFromLocalDB() {
        val uid = getCurrentUserId() ?: return

        fullList = dbHelper.getAllPiles(uid)
        _allPiles.value = fullList
    }

    /**
     * Syncs piles from Firebase Realtime Database under "users/{uid}/piles".
     * Updates local SQLite DB and LiveData to keep UI consistent.
     */
    private fun syncFromFirebase() {
        val uid = getCurrentUserId() ?: return

        val userPilesRef = FirebaseDatabase
            .getInstance("https://evse-170a5-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("users").child(uid).child("piles")

        userPilesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val firebaseList = mutableListOf<ChargingPile>()

                for (child in snapshot.children) {
                    val pile = child.getValue(ChargingPile::class.java)
                    if (pile != null) {
                        firebaseList.add(pile)

                        // Insert into local DB only if it does not exist for this user
                        if (!dbHelper.pileExists(uid, pile.id)) {
                            dbHelper.insertPile(uid, pile.id, pile.name, pile.isOnline)
                        }
                    }
                }

                // Update in-memory list and LiveData
                fullList = firebaseList
                _allPiles.value = fullList
            }

            override fun onCancelled(error: DatabaseError) {
                // Optional: Log error or notify user
            }
        })
    }

    /**
     * Helper to get currently logged in user's UID.
     */
    private fun getCurrentUserId(): String? {
        return com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
    }

    /**
     * Show all piles without filtering.
     */
    fun filterAll() {
        _allPiles.value = fullList
    }

    /**
     * Show only online piles.
     */
    fun filterOnline() {
        _allPiles.value = fullList.filter { it.isOnline }
    }

    /**
     * Show only offline piles.
     */
    fun filterOffline() {
        _allPiles.value = fullList.filter { !it.isOnline }
    }

    /**
     * Adds a new pile to in-memory list and updates UI.
     * Call this after successful insertion to Firebase and SQLite.
     */
    /**
     * Adds a new pile to Firebase only.
     * UI will automatically update via Firebase listener (onDataChange).
     */
    fun addChargingPile(pile: ChargingPile) {
        val uid = getCurrentUserId() ?: return

        val userPilesRef = FirebaseDatabase
            .getInstance("https://evse-170a5-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("users").child(uid).child("piles")

        userPilesRef.child(pile.id).setValue(pile)
    }

}
