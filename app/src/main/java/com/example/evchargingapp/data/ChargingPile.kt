package com.example.evchargingapp.data

/**
 * Data class representing a Charging Pile.
 * Firebase requires:
 *  - A public no-argument constructor (provided via default values).
 *  - All properties to be mutable (var).
 */
data class ChargingPile(
    var id: String = "",         // Unique identifier for the charging pile
    var name: String = "",       // Name or label for the charging pile
    var isOnline: Boolean = false // Status: true = online, false = offline
)
