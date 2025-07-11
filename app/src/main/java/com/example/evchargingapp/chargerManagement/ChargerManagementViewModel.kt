package com.example.evchargingapp.chargerManagement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.evchargingapp.data.ChargingPile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChargerManagementViewModel : ViewModel() {

    private val _chargerList = MutableLiveData<List<ChargingPile>>()
    val chargerList: LiveData<List<ChargingPile>> = _chargerList

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userId = firebaseAuth.currentUser?.uid
    private val databaseRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("users").child(userId ?: "").child("chargers")

    init {
        fetchChargers()
    }

    private fun fetchChargers() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chargers = mutableListOf<ChargingPile>()
                for (child in snapshot.children) {
                    val charger = child.getValue(ChargingPile::class.java)
                    charger?.let { chargers.add(it) }
                }
                _chargerList.value = chargers
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    fun renameCharger(pile: ChargingPile, newName: String) {
        databaseRef.child(pile.id).child("name").setValue(newName)
    }

    fun deleteCharger(pile: ChargingPile) {
        databaseRef.child(pile.id).removeValue()
    }

    // ✅ NEW FUNCTION: Called from the AddChargingPileDialog
    fun addChargingPile(pile: ChargingPile) {
        databaseRef.child(pile.id).setValue(pile)
    }
}
