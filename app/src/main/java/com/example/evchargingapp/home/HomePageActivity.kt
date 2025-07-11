package com.example.evchargingapp.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.evchargingapp.R
import com.example.evchargingapp.charging.ChargingActivity
import com.example.evchargingapp.common.dialogs.AddChargingPileDialog
import com.example.evchargingapp.data.MyDatabaseHelper
import com.example.evchargingapp.user.UserActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomePageActivity : AppCompatActivity() {

    // UI elements
    private lateinit var toolbar: Toolbar
    private lateinit var btnAll: Button
    private lateinit var btnOnline: Button
    private lateinit var btnOffline: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomNav: BottomNavigationView

    // ViewModel for managing data
    private val viewModel: HomeViewModel by viewModels()

    // Adapter for RecyclerView
    private lateinit var adapter: ChargingPileAdapter

    // SQLite database helper
    private lateinit var dbHelper: MyDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        // Initialize local DB helper
        dbHelper = MyDatabaseHelper(this)

        // Setup all UI and functionality
        setupViews()
        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupFilters()
        setupBottomNav()
    }

    // Link layout views to variables
    private fun setupViews() {
        toolbar = findViewById(R.id.topToolbar)
        btnAll = findViewById(R.id.btnAll)
        btnOnline = findViewById(R.id.btnOnline)
        btnOffline = findViewById(R.id.btnOffline)
        recyclerView = findViewById(R.id.chargerRecyclerView)
        bottomNav = findViewById(R.id.bottomNav)
    }

    // Configure toolbar as app bar
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Charging Pile List"
    }

    // Setup RecyclerView and its adapter
    private fun setupRecyclerView() {
        adapter = ChargingPileAdapter { selectedPile ->
            // On item click, navigate to ChargingActivity with pile info
            Toast.makeText(this, "Clicked: ${selectedPile.id}", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ChargingActivity::class.java)
            intent.putExtra("pileId", selectedPile.id)
            intent.putExtra("pileName", selectedPile.name)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    // Observe changes from ViewModel and update list
    private fun setupObservers() {
        viewModel.filteredPiles.observe(this, Observer { list ->
            adapter.submitList(list)
        })
    }

    // Handle filter button clicks
    private fun setupFilters() {
        btnAll.setOnClickListener { viewModel.filterAll() }
        btnOnline.setOnClickListener { viewModel.filterOnline() }
        btnOffline.setOnClickListener { viewModel.filterOffline() }
    }

    // Handle bottom navigation clicks
    private fun setupBottomNav() {
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true // Already here
                R.id.nav_user -> {
                    startActivity(Intent(this, UserActivity::class.java))
                    finish() // Optional: close Home
                    true
                }
                else -> false
            }
        }
    }

    // Inflate toolbar menu (add charger, logout, etc.)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home_top, menu)
        return true
    }

    // Handle toolbar item clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_charger -> {
                // Use reusable dialog with lambda
                AddChargingPileDialog.show(this) { newPile ->
                    viewModel.addChargingPile(newPile)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}
