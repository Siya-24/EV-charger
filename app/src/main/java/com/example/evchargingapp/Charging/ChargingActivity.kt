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
import com.example.evchargingapp.charging.ChargingRepository
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
        ChargingViewModelFactory(
            intent.getStringExtra("pileId") ?: "",
            ChargingRepository() // Inject the repository
        )
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

            if (!isCharging) {
                // Start charging
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
                // Stop charging
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

    private fun updateChargingUI() {
        binding.btnStartCharging.text = if (isCharging) "Stop Charging" else "Start Charging"
        val color = if (isCharging) R.color.red else R.color.green
        binding.btnStartCharging.setBackgroundColor(ContextCompat.getColor(this, color))
        binding.tvChargingStatus.text = if (isCharging) "Status: Charging" else "Status: Not Charging"
    }


    /**
     * Observe charging data (voltage, current, etc.)
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
