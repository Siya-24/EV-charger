package com.example.evchargingapp.chargerManagement

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.evchargingapp.R
import com.example.evchargingapp.common.dialogs.AddChargingPileDialog
import com.example.evchargingapp.data.ChargingPile
import com.example.evchargingapp.databinding.ActivityChargerManagementBinding
import com.example.evchargingapp.home.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ChargerManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChargerManagementBinding

    // ✅ Reusing HomeViewModel instead of a separate one
    private lateinit var viewModel: HomeViewModel

    // Adapter for displaying and managing charger items
    private lateinit var adapter: ChargerItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChargerManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Initialize shared ViewModel
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Charger Management"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Setup UI components
        setupRecyclerView()
        observeChargers()
    }

    // ✅ Set up RecyclerView with actions
    private fun setupRecyclerView() {
        adapter = ChargerItemAdapter(
            chargerList = emptyList(),
            onRename = { pile ->
                showRenameDialog(pile)
            },
            onDelete = { pile ->
                deletePile(pile)
                Toast.makeText(this, "Deleted ${pile.name}", Toast.LENGTH_SHORT).show()
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    // ✅ Observe shared LiveData from HomeViewModel
    private fun observeChargers() {
        viewModel.filteredPiles.observe(this) { list ->
            adapter.updateData(list)
        }
    }


    // These below functions are currently in the Activity (e.g., ChargerManagementActivity)
    // But ideally, they SHOULD be in the ViewModel if you want clean MVVM separation.
    // However, your logic is still valid if the rename/delete are *only used here*.

    // ✅ Show rename input dialog and update Firebase
    private fun showRenameDialog(pile: ChargingPile) {
        val input = EditText(this)
        input.setText(pile.name)

        AlertDialog.Builder(this)
            .setTitle("Rename Charger")
            .setView(input)
            .setPositiveButton("Rename") { _, _ ->
                val newName = input.text.toString()
                if (newName.isNotBlank()) {
                    val updatedPile = pile.copy(name = newName)
                    viewModel.addChargingPile(updatedPile) // Overwrites by ID
                    Toast.makeText(this, "Renamed to $newName", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ✅ Delete charger from Firebase
    private fun deletePile(pile: ChargingPile) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val ref = FirebaseDatabase
            .getInstance("https://evse-170a5-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("users").child(uid).child("piles").child(pile.id)

        ref.removeValue()
    }

    // Add icon in top menu (Add Charger)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_charger_management, menu)
        return true
    }

    // Handle top menu actions (Add Charger)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_charger -> {
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
