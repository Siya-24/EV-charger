package com.example.evchargingapp.auth

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.evchargingapp.R
import com.example.evchargingapp.home.HomePageActivity

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(AuthRepository(applicationContext))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Auto-login if user already signed in
        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("isLoggedIn", false)) {
            startActivity(Intent(this, HomePageActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        val email = findViewById<EditText>(R.id.loginEmail)
        val password = findViewById<EditText>(R.id.loginPassword)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val registerLink = findViewById<TextView>(R.id.registerLink)
        val forgotPasswordText = findViewById<TextView>(R.id.forgotPasswordText)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        forgotPasswordText.setOnClickListener {
            Toast.makeText(this, "Forgot password clicked", Toast.LENGTH_SHORT).show()
        }

        loginBtn.setOnClickListener {
            val emailInput = email.text.toString().trim()
            val passInput = password.text.toString().trim()

            if (emailInput.isEmpty() || passInput.isEmpty()) {
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isNetworkAvailable()) {
                Toast.makeText(this, "No internet connection. Please connect to Wi-Fi.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            viewModel.login(emailInput, passInput)
        }

        // ✅ Handle login success
        viewModel.loginSuccess.observe(this) { success ->
            progressBar.visibility = View.GONE
            if (success) {
                // Save login state
                val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                prefs.edit().apply {
                    putBoolean("isLoggedIn", true)
                    apply()
                }

                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomePageActivity::class.java))
                finish()
            }
        }

        // ✅ Handle login failure
        viewModel.error.observe(this) { errorMsg ->
            progressBar.visibility = View.GONE
            Toast.makeText(this, "Login Failed: $errorMsg", Toast.LENGTH_LONG).show()
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetworkInfo
        return network != null && network.isConnected
    }
}
