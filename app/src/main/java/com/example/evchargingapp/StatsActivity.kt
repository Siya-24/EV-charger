package com.example.evchargingapp // <--- IMPORTANT: This MUST match your project's root package name!

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale // <--- ADD THIS IMPORT
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt
// 🔄 ADDED THESE FOR IDENTIFYING PERFORMANCE AND THREADING ISSUES:+++++++++++++++++++
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

// 🔧 Correct BuildConfig import
import com.example.evchargingapp.BuildConfig // ✅ correct


class StatsActivity : AppCompatActivity() {

    // Declare TextViews for displaying results
    private lateinit var durationTextView: TextView
    private lateinit var avgPowerTextView: TextView
    private lateinit var energyConsumedTextView: TextView
    private lateinit var costTextView: TextView
    private lateinit var batteryGainedTextView: TextView
    private lateinit var chargingSpeedTextView: TextView
    private lateinit var approxTimeTextView: TextView

    // --- Hardcoded Example Input Data (YOU SHOULD REPLACE THESE WITH REAL DATA FROM YOUR APP/CHARGER) ---
    // These values will be used for calculation demonstration.
    // In a real app, these would come from an Intent (if passed from another Activity)
    // or loaded from a database based on a selected charging session.

    // Session Timestamps (using milliseconds from epoch for simplicity in Android)
    // Example: June 17, 2024, 10:00:00 AM UTC
    private val sessionStartTimeMillis = 1718611200000L
    // Example: June 17, 2024, 11:30:00 AM UTC (1.5 hours later)
    private val sessionEndTimeMillis = 1718616600000L

    private val avgChargingCurrentA = 16.0 // Amperes

    private val startBatteryPercent = 20 // Percentage
    private val endBatteryPercent = 75 // Percentage

    // Global Constants (You should define these based on typical values or user settings)
    private val homeVoltageV = 230.0 // Volts (e.g., 230V for Europe/Asia, 240V for North America)
    private val chargerEfficiency = 0.92 // 92% efficiency (as a decimal)
    private val electricityRatePerKWh = 0.20 // Cost per kWh (e.g., $0.20 or Rs. 20)

    // EV Specific Constants (These would typically be user-configurable per vehicle)
    private val carUsableBatteryCapacityKWh = 70.0 // Usable battery capacity in kWh
    private val carEfficiencyWhPerUnit = 170.0 // Watt-hours per kilometer (Wh/km) or Wh/mile
    private val rangeUnit = "km" // Unit for range (e.g., "km" or "miles")
    private val targetBatteryPercent = 100 // Target percentage for "time to full" calculation


    override fun onCreate(savedInstanceState: Bundle?) {

        // 🔄 Enable StrictMode for debugging bad practices (only in debug builds)
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                ThreadPolicy.Builder()
                    .detectAll() // Log everything (network, disk, etc.)
                    .penaltyLog() // Print to Logcat
                    .build()
            )
            StrictMode.setVmPolicy(
                VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }

        super.onCreate(savedInstanceState)
        // Set content view to your new stats layout
        setContentView(R.layout.activity_charging_stats) // CRITICAL: Links to your XML layout

        // Initialize TextViews by finding their IDs from activity_charging_stats.xml
        durationTextView = findViewById(R.id.durationTextView)
        avgPowerTextView = findViewById(R.id.avgPowerTextView)
        energyConsumedTextView = findViewById(R.id.energyConsumedTextView)
        costTextView = findViewById(R.id.costTextView)
        batteryGainedTextView = findViewById(R.id.batteryGainedTextView)
        chargingSpeedTextView = findViewById(R.id.chargingSpeedTextView)
        approxTimeTextView = findViewById(R.id.approxTimeTextView)

        // Perform calculations and display results
        calculateAndDisplayStats()
    }

    private fun calculateAndDisplayStats() {
        // 1. Charging Duration
        val durationMillis = sessionEndTimeMillis - sessionStartTimeMillis
        val durationHours = durationMillis / (1000.0 * 60 * 60)
        val durationMinutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60
        val formattedDuration = String.format(Locale.getDefault(), "%.0f hr %.0f min", durationHours.toLong().toDouble(), durationMinutes.toDouble())
        durationTextView.text = formattedDuration

        // 2. Average Charging Power (kW) - this is the *input* power from grid
        val inputPowerWatts = homeVoltageV * avgChargingCurrentA
        val inputPowerKw = inputPowerWatts / 1000.0
        // Charger Output Power (effective power going into battery, after efficiency)
        val chargerOutputPowerKw = inputPowerKw * chargerEfficiency
        avgPowerTextView.text = String.format(Locale.getDefault(), "%.2f kW (avg)", chargerOutputPowerKw)

        // 3. Energy Consumed (kWh) - based on charger output power
        val energyConsumedKwh = chargerOutputPowerKw * durationHours
        energyConsumedTextView.text = String.format(Locale.getDefault(), "%.2f kWh", energyConsumedKwh)

        // 4. Cost of Charging
        val cost = energyConsumedKwh * electricityRatePerKWh
        costTextView.text = String.format(Locale.getDefault(), "$%.2f", cost) // Adjust currency symbol as needed

        // 5. Battery Percentage Gained
        val percentageGained = endBatteryPercent - startBatteryPercent
        batteryGainedTextView.text = String.format(Locale.getDefault(), "%d %%", percentageGained)

        // 6. Charging Speed (Range/Hour)
        // Ensure chargerOutputPowerKw is used here, as it's the power going into the car
        val rangeGainedPerUnit = (chargerOutputPowerKw * 1000.0) / carEfficiencyWhPerUnit
        chargingSpeedTextView.text = String.format(Locale.getDefault(), "%.1f %s/hr", rangeGainedPerUnit, rangeUnit)

        // 7. Approximate Time to Target Percentage
        val energyNeededKwh = ((targetBatteryPercent - endBatteryPercent) / 100.0) * carUsableBatteryCapacityKWh
        val approxTimeHours = if (chargerOutputPowerKw > 0) energyNeededKwh / chargerOutputPowerKw else 0.0

        val approxTimeMinutes = (approxTimeHours * 60).roundToInt()
        val hoursRemaining = approxTimeMinutes / 60
        val minutesRemaining = approxTimeMinutes % 60

        approxTimeTextView.text = String.format(Locale.getDefault(), "%d hr %d min (to %d%%)", hoursRemaining, minutesRemaining, targetBatteryPercent)
    }
}