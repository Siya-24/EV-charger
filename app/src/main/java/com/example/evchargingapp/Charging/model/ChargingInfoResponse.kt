package com.example.evchargingapp.charging.model

/**
 * Data model returned by the /charging/{pileId}/info API.
 */
data class ChargingInfoResponse(
    val temperature: Int,       // Celsius
    val voltage: Float,         // Volts
    val meterReading: Float,    // kWh
    val current: Float,         // Amps
    val duration: Int           // Minutes
)
