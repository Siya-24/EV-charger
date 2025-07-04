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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class HomePageActivity : AppCompatActivity() {

    // 🔗 UI components
    private lateinit var toolbar: Toolbar
    private lateinit var btnAll: Button
    private lateinit var btnOnline: Button
    private lateinit var btnOffline: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomNav: BottomNavigationView

    // 🧠 ViewModel and Adapter
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: ChargingPileAdapter

    // 💾 Local database helper (SQLite)
    private lateinit var dbHelper: MyDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        // 🧹 Clear login state (temporary if you're logging out immediately)
        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        dbHelper = MyDatabaseHelper(this)  // Initialize DB helper

        setupViews()
        setupToolbar()
        setupRecyclerView()   // ⚠️ updated to support pile click
        setupObservers()
        setupFilters()
        setupBottomNav()
    }

    // 🔗 Link views to layout
    private fun setupViews() {
        toolbar = findViewById(R.id.topToolbar)
        btnAll = findViewById(R.id.btnAll)
        btnOnline = findViewById(R.id.btnOnline)
        btnOffline = findViewById(R.id.btnOffline)
        recyclerView = findViewById(R.id.chargerRecyclerView)
        bottomNav = findViewById(R.id.bottomNav)
    }

    // 🧭 Toolbar setup
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Charging Pile List"
    }

    // 🧱 Set up RecyclerView and handle pile click
    private fun setupRecyclerView() {
        adapter = ChargingPileAdapter { selectedPile ->
            Toast.makeText(this, "Clicked: ${selectedPile.id}", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, ChargingActivity::class.java)
            intent.putExtra("pileId", selectedPile.id)
            intent.putExtra("pileName", selectedPile.name)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

    }

    // 🔁 Observe changes from ViewModel
    private fun setupObservers() {
        viewModel.filteredPiles.observe(this, Observer { list ->
            adapter.submitList(list)
        })
    }

    // 🔘 Filter buttons
    private fun setupFilters() {
        btnAll.setOnClickListener { viewModel.filterAll() }
        btnOnline.setOnClickListener { viewModel.filterOnline() }
        btnOffline.setOnClickListener { viewModel.filterOffline() }
    }

    // ⛴ Bottom navigation setup (extendable)
    private fun setupBottomNav() {
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_user -> {
                    // Navigate to user page (future)
                    true
                }
                else -> false
            }
        }
    }

    // ➕ Dialog for adding a new charging pile
    @SuppressLint("SetTextI18n")
    private fun showAddChargingPileDialog() {
        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_add_charging_pile, null)

        val nameInput = dialogView.findViewById<EditText>(R.id.etPileName)
        val idInput = dialogView.findViewById<EditText>(R.id.etPileId)
        val statusSpinner = dialogView.findViewById<Spinner>(R.id.spinnerStatus)

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

                    if (dbHelper.pileExists(id)) {
                        Toast.makeText(this, "Pile ID already exists!", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val inserted = dbHelper.insertPile(id, name, isOnline)
                    if (!inserted) {
                        Toast.makeText(this, "SQLite insertion failed", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

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

    // Top-right "+" and logout menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home_top, menu)
        return true
    }

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
