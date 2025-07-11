package com.example.evchargingapp.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.evchargingapp.R
import com.example.evchargingapp.auth.LoginActivity
import com.example.evchargingapp.chargerManagement.ChargerManagementActivity
import com.example.evchargingapp.home.HomePageActivity
import com.google.android.material.bottomnavigation.BottomNavigationView



class UserActivity : AppCompatActivity() {

    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val username = viewModel.getUsername()
        tvUsername.text = if (username != null) {
            "Welcome, $username"
        } else {
            "Not logged in"
        }

        // Navigation buttons

        findViewById<Button>(R.id.btnChargerManagement).setOnClickListener {
            val intent = Intent(this, ChargerManagementActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnNetworking).setOnClickListener {
            // TODO: Navigate to ChargingNetworkingActivity
        }

        findViewById<Button>(R.id.btnAccountSettings).setOnClickListener {
            // TODO: Navigate to AccountSettingsActivity
        }

        findViewById<Button>(R.id.btnChangeLanguage).setOnClickListener {
            // TODO: Show language change dialog
        }

        // Logout
        btnLogout.setOnClickListener {
            viewModel.logout()

            // Navigate to Login screen
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }


        // bottom navigation to switch to home tab:
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Navigate back to HomeActivity
                    val intent = Intent(this, HomePageActivity::class.java)
                    startActivity(intent)
                    finish()  // Close UserActivity to avoid stacking
                    true
                }
                R.id.nav_user -> {
                    // Current screen, do nothing
                    true
                }
                // Add other tabs if applicable
                else -> false
            }
        }

    }
}
