package com.example.evchargingapp.charging.model

/**
 * Data model returned by the /charging/{pileId}/info API.
 */

/**
 * This file defines a Kotlin data class that represents the charging info response you expect from your backend.
 *
 * In plain English, it’s like saying:
 *
 * “When the app calls /charging/{pileId}/info, this (below code) is what we expect the server to send back
 * */
data class ChargingInfoResponse(
    val id: String,
    val temperature: String,
    val voltage: String,
    val current: String,
    val energy: String,
    val time: String,
    val isCharging: Boolean
)
// changed this currently to match the backend server response
