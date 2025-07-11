package com.example.evchargingapp.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.evchargingapp.R
import com.example.evchargingapp.auth.AuthRepository
import com.example.evchargingapp.auth.LoginActivity
import com.example.evchargingapp.charging.ChargingActivity
import com.example.evchargingapp.data.ChargingPile
import com.example.evchargingapp.data.MyDatabaseHelper
import com.example.evchargingapp.user.UserActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class HomePageActivity : AppCompatActivity() {

    // UI components linked to layout views
    private lateinit var toolbar: Toolbar
    private lateinit var btnAll: Button
    private lateinit var btnOnline: Button
    private lateinit var btnOffline: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomNav: BottomNavigationView

    // ViewModel for managing piles list and filtering
    private val viewModel: HomeViewModel by viewModels()

    // Adapter for RecyclerView to display charging piles
    private lateinit var adapter: ChargingPileAdapter

    // Local SQLite database helper instance
    private lateinit var dbHelper: MyDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)


        // Initialize database helper
        dbHelper = MyDatabaseHelper(this)

        // Set up UI components and behavior
        setupViews()
        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupFilters()
        setupBottomNav()
    }

    // Link UI elements with XML layout views
    private fun setupViews() {
        toolbar = findViewById(R.id.topToolbar)
        btnAll = findViewById(R.id.btnAll)
        btnOnline = findViewById(R.id.btnOnline)
        btnOffline = findViewById(R.id.btnOffline)
        recyclerView = findViewById(R.id.chargerRecyclerView)
        bottomNav = findViewById(R.id.bottomNav)
    }

    // Setup toolbar title and action bar
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Charging Pile List"
    }

    // Setup RecyclerView with adapter and item click listener
    private fun setupRecyclerView() {
        adapter = ChargingPileAdapter { selectedPile ->
            Toast.makeText(this, "Clicked: ${selectedPile.id}", Toast.LENGTH_SHORT).show()

            // Open ChargingActivity on pile click, passing pile info via Intent
            val intent = Intent(this, ChargingActivity::class.java)
            intent.putExtra("pileId", selectedPile.id)
            intent.putExtra("pileName", selectedPile.name)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    // Observe filtered piles LiveData from ViewModel and update adapter
    private fun setupObservers() {
        viewModel.filteredPiles.observe(this, Observer { list ->
            adapter.submitList(list)
        })
    }

    // Set click listeners on filter buttons to update displayed list
    private fun setupFilters() {
        btnAll.setOnClickListener { viewModel.filterAll() }
        btnOnline.setOnClickListener { viewModel.filterOnline() }
        btnOffline.setOnClickListener { viewModel.filterOffline() }
    }

    // Setup bottom navigation view (can be expanded with more functionality)
    private fun setupBottomNav() {
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true  // Stay on home
                R.id.nav_user -> {
                    // Switch to User tab (start UserActivity)
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                    finish() // Optional: close HomePageActivity if you want only one active
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Shows a dialog to add a new charging pile.
     * Validates input, checks for duplicate pile ID scoped to user,
     * inserts into SQLite, then adds to Firebase using AuthRepository.
     */
    @SuppressLint("SetTextI18n")
    private fun showAddChargingPileDialog() {
        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_add_charging_pile, null)

        val nameInput = dialogView.findViewById<EditText>(R.id.etPileName)
        val idInput = dialogView.findViewById<EditText>(R.id.etPileId)
        val statusSpinner = dialogView.findViewById<Spinner>(R.id.spinnerStatus)

        // Setup spinner with "Online" and "Offline" options
        val statusOptions = listOf("Online", "Offline")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statusSpinner.adapter = spinnerAdapter

        val dialog = AlertDialog.Builder(this, R.style.CustomDialogTheme)
            .setTitle("Add Charging Pile")
            .setView(dialogView)
            .create()


        dialog.setOnShowListener {
            val addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            addButton.setOnClickListener {
                val id = idInput.text.toString().trim()
                val name = nameInput.text.toString().trim()
                val isOnline = statusSpinner.selectedItem.toString() == "Online"

                // Check required fields
                if (id.isEmpty() || name.isEmpty()) {
                    Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Get current user UID to scope piles per user
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid == null) {
                    Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Check if pile ID already exists for this user
                if (dbHelper.pileExists(uid, id)) {
                    Toast.makeText(this, "Pile ID already exists!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Insert pile into local SQLite DB scoped by user
                val inserted = dbHelper.insertPile(uid, id, name, isOnline)
                if (!inserted) {
                    Toast.makeText(this, "SQLite insertion failed", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Create ChargingPile object
                val newPile = ChargingPile(id, name, isOnline)

                // Add pile to Firebase under user's node
                val repo = AuthRepository(this)
                repo.addChargingPile(newPile) { success, message ->
                    if (success) {
                        // Update ViewModel and UI
                        viewModel.addChargingPile(newPile)
                        Toast.makeText(this, "Charging pile added!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this, "Failed: $message", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        dialog.show()

        dialog.show()

        val addButton = dialogView.findViewById<Button>(R.id.buttonAdd)
        val cancelButton = dialogView.findViewById<Button>(R.id.buttonCancel)

        addButton.setOnClickListener {
            val id = idInput.text.toString().trim()
            val name = nameInput.text.toString().trim()
            val isOnline = statusSpinner.selectedItem.toString() == "Online"

            if (id.isEmpty() || name.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dbHelper.pileExists(uid, id)) {
                Toast.makeText(this, "Pile ID already exists!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val inserted = dbHelper.insertPile(uid, id, name, isOnline)
            if (!inserted) {
                Toast.makeText(this, "SQLite insertion failed", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

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
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

    }

    // Inflate menu with logout and add pile options
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home_top, menu)
        return true
    }

    // Handle toolbar menu item clicks for logout and add pile
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
