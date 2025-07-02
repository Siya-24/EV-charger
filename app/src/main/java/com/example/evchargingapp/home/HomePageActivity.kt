package com.example.evchargingapp.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.evchargingapp.R
import com.example.evchargingapp.auth.AuthRepository
import com.example.evchargingapp.data.ChargingPile
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.app.AlertDialog

class HomePageActivity : AppCompatActivity() {

    // UI components
    private lateinit var toolbar: Toolbar
    private lateinit var btnAll: Button
    private lateinit var btnOnline: Button
    private lateinit var btnOffline: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomNav: BottomNavigationView

    // ViewModel and Adapter
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: ChargingPileAdapter

    // onCreate: Sets up everything when the activity starts
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        setupViews()         // Link XML views
        setupToolbar()       // Setup toolbar at the top
        setupRecyclerView()  // Setup RecyclerView with adapter
        setupObservers()     // Observe data from ViewModel
        setupFilters()       // Filter buttons: All / Online / Offline
        setupBottomNav()     // Bottom navigation bar
    }

    // Connect XML views to code
    private fun setupViews() {
        toolbar = findViewById(R.id.topToolbar)
        btnAll = findViewById(R.id.btnAll)
        btnOnline = findViewById(R.id.btnOnline)
        btnOffline = findViewById(R.id.btnOffline)
        recyclerView = findViewById(R.id.chargerRecyclerView)
        bottomNav = findViewById(R.id.bottomNav)
    }

    // Use the custom Toolbar as the app's ActionBar
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Charging Pile List"
    }

    // Setup RecyclerView with our ChargingPileAdapter
    private fun setupRecyclerView() {
        adapter = ChargingPileAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    // Observe LiveData from ViewModel and update RecyclerView
    private fun setupObservers() {
        viewModel.filteredPiles.observe(this, Observer { list ->
            adapter.submitList(list)
        })
    }

    // Setup filter buttons to show All / Online / Offline piles
    private fun setupFilters() {
        btnAll.setOnClickListener { viewModel.filterAll() }
        btnOnline.setOnClickListener { viewModel.filterOnline() }
        btnOffline.setOnClickListener { viewModel.filterOffline() }
    }

    // Setup bottom navigation actions
    private fun setupBottomNav() {
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_user -> {
                    // You can navigate to User screen here
                    true
                }
                else -> false
            }
        }
    }

    // Show dialog to add a new charging pile
    @SuppressLint("SetTextI18n")
    private fun showAddChargingPileDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_charging_pile, null)

        val nameInput = dialogView.findViewById<EditText>(R.id.etPileName)
        val idInput = dialogView.findViewById<EditText>(R.id.etPileId)
        val statusSpinner = dialogView.findViewById<Spinner>(R.id.spinnerStatus)

        // ✅ ADD THIS BLOCK BELOW — configures the spinner
        val statusOptions = listOf("Online", "Offline")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statusSpinner.adapter = spinnerAdapter


        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Charging Pile")
            .setView(dialogView)
            .setPositiveButton("Add", null)  // ✅ Important: this makes the Add button appear
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            addButton.setOnClickListener {
                val id = idInput.text.toString().trim()
                val name = nameInput.text.toString().trim()
                val isOnline = statusSpinner.selectedItem.toString() == "Online"

                if (id.isNotEmpty() && name.isNotEmpty()) {
                    val newPile = ChargingPile(id, name, isOnline)
                    val repo = AuthRepository(this)

                    repo.addChargingPile(newPile) { success, message ->
                        if (success) {
                            viewModel.addChargingPile(newPile)
                            Toast.makeText(this, "Charging pile added!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        } else {
                            Toast.makeText(this, "Failed: $message", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.show()
    }

    // Inflate the toolbar menu (includes "+" button)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home_top, menu)
        return true
    }

    // Handle clicks on toolbar menu items
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_charger -> {
                showAddChargingPileDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
