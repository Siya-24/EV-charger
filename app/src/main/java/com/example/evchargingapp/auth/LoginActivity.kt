package com.example.evchargingapp.auth

import android.content.Intent
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
    }
}
