package com.example.evchargingapp.data

object MockChargingPileData {
    val pileList = listOf(
        ChargingPile("CP001", "Alpha Station", true),
        ChargingPile("CP002", "Beta Station", false),
        ChargingPile("CP003", "Gamma FastCharge", true)
    )
}
