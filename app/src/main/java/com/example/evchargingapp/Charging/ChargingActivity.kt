package com.example.evchargingapp.charging

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.evchargingapp.R
import com.example.evchargingapp.databinding.ActivityChargingBinding
import com.example.evchargingapp.viewmodel.ChargingViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Activity to show charging details and trigger charging.
 */
class ChargingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChargingBinding

    // Inject pileId from intent and initialize ViewModel with factory
    private val viewModel: ChargingViewModel by viewModels {
        ChargingViewModelFactory(intent.getStringExtra("pileId") ?: "")
    }

    private var isCharging = false // Toggle state of charging

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChargingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
        viewModel.loadChargingInfo() // Initial API fetch
    }

    /**
     * Set up UI listeners and visual feedback
     */
    private fun setupUI() {
        binding.btnStartCharging.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE

            // Call API to start charging (fake API)
            viewModel.startCharging { success, message ->
                binding.progressBar.visibility = View.GONE

                if (success) {
                    isCharging = !isCharging

                    binding.btnStartCharging.text =
                        if (isCharging) "Stop Charging" else "Start Charging"

                    val color = if (isCharging) R.color.red else R.color.green
                    binding.btnStartCharging.setBackgroundColor(
                        ContextCompat.getColor(this, color)
                    )

                    binding.tvChargingStatus.text =
                        if (isCharging) "Status: Charging" else "Status: Not started"

                    Toast.makeText(this, "Charging started!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Observe charging data (voltage, current, etc.)
     */
    private fun observeViewModel() {
        viewModel.chargingInfo.observe(this) { info ->
            binding.tvTemperature.text = "${info.temperature} °C"
            binding.tvVoltage.text = "${info.voltage} V"
            binding.tvMeter.text = "${info.meterReading} kWh"
            binding.tvCurrent.text = "${info.current} A"
            binding.tvTime.text = "${info.duration} min"
        }
    }
}
