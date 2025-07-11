package com.example.evchargingapp.chargerManagement

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.evchargingapp.R
import com.example.evchargingapp.common.dialogs.AddChargingPileDialog
import com.example.evchargingapp.databinding.ActivityChargerManagementBinding
import com.example.evchargingapp.data.ChargingPile

class ChargerManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChargerManagementBinding

    // Use ChargerManagementViewModel for this screen
    private val viewModel: ChargerManagementViewModel by viewModels()

    // Adapter to show list of chargers with Rename/Delete actions
    private lateinit var adapter: ChargerItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChargerManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Charger Management"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Setup UI
        setupRecyclerView()
        observeChargers()
    }

    // Setup RecyclerView and Adapter with actions
    private fun setupRecyclerView() {
        adapter = ChargerItemAdapter(
            chargerList = emptyList(),
            onRename = { charger ->
                viewModel.renameCharger(charger, "Renamed Charger")
                Toast.makeText(this, "Renamed ${charger.name}", Toast.LENGTH_SHORT).show()
            },
            onDelete = { charger ->
                viewModel.deleteCharger(charger)
                Toast.makeText(this, "Deleted ${charger.name}", Toast.LENGTH_SHORT).show()
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    // Observe charger list from ViewModel
    private fun observeChargers() {
        viewModel.chargerList.observe(this) { list ->
            adapter.updateData(list)
        }
    }

    // Inflate top menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_charger_management, menu)
        return true
    }

    // Handle top menu actions
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_charger -> {
                // Corrected: Show dialog with lambda, not viewModel
                AddChargingPileDialog.show(this) { newPile ->
                    viewModel.addChargingPile(newPile)
                }
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
