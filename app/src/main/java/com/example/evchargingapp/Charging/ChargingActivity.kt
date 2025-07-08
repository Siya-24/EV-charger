package com.example.evchargingapp.charging

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.evchargingapp.R
import com.example.evchargingapp.databinding.ActivityChargingBinding
import com.example.evchargingapp.viewmodel.ChargingViewModelFactory

/**
 * Activity to show charging details, control charging process,
 * and display live charging data.
 */
class ChargingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChargingBinding

    // Get pileId and initialize ViewModel using factory with repository injection
    private val viewModel: ChargingViewModel by viewModels {
        ChargingViewModelFactory(
            intent.getStringExtra("pileId") ?: "",
            ChargingRepository()
        )
    }

    private var isCharging = false // Track charging state

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChargingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Display the pile name dynamically from intent
        val pileName = intent.getStringExtra("pileName") ?: "Unknown"
        binding.tvPileId.text = "Pile: $pileName"

        setupUI()
        observeViewModel()
        viewModel.loadChargingInfo() // Fetch charging info on load
        viewModel.startPollingChargingInfo() // ✅ Start live updates
    }
    /**
     * Then override onDestroy() to stop polling:
     * */
    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopPollingChargingInfo() // ❌ Stop updates when screen closes
    }


    /**
     * Sets up UI interactions and button click behavior.
     */
    private fun setupUI() {
        binding.btnStartCharging.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE

            if (!isCharging) {
                // 🔌 Start charging if currently not charging
                viewModel.startCharging { success, message ->
                    binding.progressBar.visibility = View.GONE

                    if (success) {
                        isCharging = true
                        updateChargingUI()
                        Toast.makeText(this, "Charging started!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Start failed: $message", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // 🛑 Stop charging if currently charging
                viewModel.stopCharging { success, message ->
                    binding.progressBar.visibility = View.GONE

                    if (success) {
                        isCharging = false
                        updateChargingUI()
                        Toast.makeText(this, "Charging stopped!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Stop failed: $message", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    /**
     * Updates UI based on current charging status.
     */
    private fun updateChargingUI() {
        binding.btnStartCharging.text = if (isCharging) "Stop Charging" else "Start Charging"
        val color = if (isCharging) R.color.red else R.color.green
        binding.btnStartCharging.setBackgroundColor(ContextCompat.getColor(this, color))
        binding.tvChargingStatus.text = if (isCharging) "Status: Charging" else "Status: Not Charging"
    }

    /**
     * Observes LiveData from ViewModel and updates the UI with real-time values.
     */
    private fun observeViewModel() {
        viewModel.chargingInfo.observe(this) { info ->
            binding.tvTemperature.text = "${info.temperature} °C"
            binding.tvVoltage.text = info.voltage
            binding.tvMeter.text = info.energy
            binding.tvCurrent.text = info.current
            binding.tvTime.text = info.time
        }
    }
}
