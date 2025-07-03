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
import com.example.evchargingapp.data.MyDatabaseHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import com.example.evchargingapp.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class HomePageActivity : AppCompatActivity() {

    //bluetooth
    private val REQUIRED_PERMISSIONS = arrayOf(

        android.Manifest.permission.ACCESS_WIFI_STATE,
        android.Manifest.permission.CHANGE_WIFI_STATE,

    )

    private val PERMISSION_REQUEST_CODE = 1001

    private fun checkAndRequestPermissions() {
        val missingPermissions = REQUIRED_PERMISSIONS.filter {
            checkSelfPermission(it) != android.content.pm.PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            requestPermissions(missingPermissions.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }


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

    // Local database helper (for SQLite)
    private lateinit var dbHelper: MyDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        checkAndRequestPermissions() // 🔧 Request WiFi/Bluetooth permissions here

        dbHelper = MyDatabaseHelper(this)  // Initialize DB helper

        setupViews()         // Link XML views
        setupToolbar()       // Set up top toolbar
        setupRecyclerView()  // RecyclerView for pile list
        setupObservers()     // Observe ViewModel for list changes
        setupFilters()       // Buttons to filter piles
        setupBottomNav()     // Bottom nav bar
    }



    // Link views from XML layout
    private fun setupViews() {
        toolbar = findViewById(R.id.topToolbar)
        btnAll = findViewById(R.id.btnAll)
        btnOnline = findViewById(R.id.btnOnline)
        btnOffline = findViewById(R.id.btnOffline)
        recyclerView = findViewById(R.id.chargerRecyclerView)
        bottomNav = findViewById(R.id.bottomNav)
    }

    // Set toolbar as ActionBar
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Charging Pile List"
    }

    // RecyclerView setup with ChargingPileAdapter
    private fun setupRecyclerView() {
        adapter = ChargingPileAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    // ViewModel LiveData observer to update list UI
    private fun setupObservers() {
        viewModel.filteredPiles.observe(this, Observer { list ->
            adapter.submitList(list)
        })
    }

    // Filters for All / Online / Offline piles
    private fun setupFilters() {
        btnAll.setOnClickListener { viewModel.filterAll() }
        btnOnline.setOnClickListener { viewModel.filterOnline() }
        btnOffline.setOnClickListener { viewModel.filterOffline() }
    }

    // Bottom navigation (you can add screens later)
    private fun setupBottomNav() {
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_user -> {
                    // Add navigation to user screen here
                    true
                }
                else -> false
            }
        }
    }

    // Dialog for adding a new charging pile
    @SuppressLint("SetTextI18n")
    private fun showAddChargingPileDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_charging_pile, null)

        val nameInput = dialogView.findViewById<EditText>(R.id.etPileName)
        val idInput = dialogView.findViewById<EditText>(R.id.etPileId)
        val statusSpinner = dialogView.findViewById<Spinner>(R.id.spinnerStatus)

        // Spinner options
        val statusOptions = listOf("Online", "Offline")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statusSpinner.adapter = spinnerAdapter

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Charging Pile")
            .setView(dialogView)
            .setPositiveButton("Add", null)
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

                    // ✅ Check if pile already exists in SQLite
                    if (dbHelper.pileExists(id)) {
                        Toast.makeText(this, "Pile ID already exists!", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    // ✅ Insert into SQLite
                    val inserted = dbHelper.insertPile(id, name, isOnline)
                    if (!inserted) {
                        Toast.makeText(this, "SQLite insertion failed", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    // ✅ Push to Firebase & update ViewModel/UI
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

    // Inflate top-right menu (+ button)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home_top, menu)
        return true
    }

    // Handle top-right "+" button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                prefs.edit().clear().apply()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            R.id.action_add_charger -> {
                showAddChargingPileDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
