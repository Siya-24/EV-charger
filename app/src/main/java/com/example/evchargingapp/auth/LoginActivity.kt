package com.example.evchargingapp.auth

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.evchargingapp.R
import com.example.evchargingapp.home.HomePageActivity
//import com.example.evchargingapp.auth.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    // 🔌 Check network connection before attempting login
    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetworkInfo
        return network != null && network.isConnected
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val email = findViewById<EditText>(R.id.loginEmail)
        val password = findViewById<EditText>(R.id.loginPassword)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val registerLink = findViewById<TextView>(R.id.registerLink)
        val forgotPasswordText = findViewById<TextView>(R.id.forgotPasswordText)

        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        forgotPasswordText.setOnClickListener {
            // You can add a ForgotPasswordActivity or Firebase password reset here
            Toast.makeText(this, "Forgot password clicked", Toast.LENGTH_SHORT).show()
        }

        loginBtn.setOnClickListener {
            val emailInput = email.text.toString().trim()
            val passInput = password.text.toString().trim()


            if (emailInput.isEmpty() || passInput.isEmpty()) {
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(emailInput, passInput)

            if (!isNetworkAvailable()) {
                Toast.makeText(this, "No internet connection. Please connect to Wi-Fi.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
        }

        viewModel.loginSuccess.observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                // // navigation to home page----------------------------------------
                val intent = Intent(this, HomePageActivity::class.java)
                startActivity(intent)
                finish() // Optional: close LoginActivity so user can't go back to it
            }
        })

        viewModel.error.observe(this, Observer { errorMsg ->
            Toast.makeText(this, "Login Failed: $errorMsg", Toast.LENGTH_LONG).show()
        })

        // To save the data in shared preferences so that once the user logs in they will not log out unless the user los out themselves
        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("isLoggedIn", false)) {
            startActivity(Intent(this, HomePageActivity::class.java))
            finish()
        }

    }
}


