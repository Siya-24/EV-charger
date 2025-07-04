package com.example.evchargingapp.data

/**
 * Represents the current status of a charging pile.
 */
data class ChargingStatus(
    val temperature: Int,     // Celsius temperature
    val voltage: Float,       // Voltage in volts
    val current: Float,       // Current in amperes
    val energy: Float,        // Energy consumed in kWh
    val time: Int             // Charging duration in minutes
)

/**
 * Response from start charging API.
 */
data class StartResponse(
    val ack: Boolean          // true if start acknowledged by server
)
