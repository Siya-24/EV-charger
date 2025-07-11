package com.example.evchargingapp.auth

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.evchargingapp.R
import com.example.evchargingapp.home.HomePageActivity

/**
 * Activity that handles user login UI and interactions.
 */
class LoginActivity : AppCompatActivity() {

    // Initialize LoginViewModel using a factory to pass AuthRepository
    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(AuthRepository(applicationContext))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is already logged in using SharedPreferences
        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("isLoggedIn", false)) {
            // If logged in, directly navigate to HomePage and finish this Activity
            startActivity(Intent(this, HomePageActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        // Get references to UI elements
        val email = findViewById<EditText>(R.id.loginEmail)
        val password = findViewById<EditText>(R.id.loginPassword)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val registerLink = findViewById<TextView>(R.id.registerLink)
        val forgotPasswordText = findViewById<TextView>(R.id.forgotPasswordText)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        // Navigate to registration screen if user clicks register link
        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // For demonstration: handle forgot password click
        forgotPasswordText.setOnClickListener {
            Toast.makeText(this, "Forgot password clicked", Toast.LENGTH_SHORT).show()
        }

        // Handle login button click
        loginBtn.setOnClickListener {
            val emailInput = email.text.toString().trim()
            val passInput = password.text.toString().trim()

            // Validate inputs are not empty
            if (emailInput.isEmpty() || passInput.isEmpty()) {
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check for network connection before login attempt
            if (!isNetworkAvailable()) {
                Toast.makeText(this, "No internet connection. Please connect to Wi-Fi.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Show progress spinner during login process
            progressBar.visibility = View.VISIBLE

            // Trigger login through ViewModel
            viewModel.login(emailInput, passInput)
        }

        // Observe login success state
        viewModel.loginSuccess.observe(this) { success ->
            if (!success) {
                // Hide progress if login failed
                progressBar.visibility = View.GONE
            }
        }

        // Observe username LiveData once fetched after successful login
        viewModel.username.observe(this) { username ->
            if (username != null) {
                // Save username and login status in SharedPreferences
                val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                prefs.edit().apply {
                    putString("username", username)
                    putBoolean("isLoggedIn", true)
                    apply()
                }

                // Hide progress and notify user of success
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                // Navigate to HomePage and finish login screen
                startActivity(Intent(this, HomePageActivity::class.java))
                finish()
            }
        }

        // Observe errors and show error message if login fails
        viewModel.error.observe(this) { errorMsg ->
            progressBar.visibility = View.GONE
            Toast.makeText(this, "Login Failed: $errorMsg", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Checks if device currently has an active network connection.
     */
    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetworkInfo
        return network != null && network.isConnected
    }
}
