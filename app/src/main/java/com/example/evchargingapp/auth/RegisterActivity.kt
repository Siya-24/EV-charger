package com.example.evchargingapp.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.evchargingapp.R

class RegisterActivity : AppCompatActivity() {

    private val viewModel: RegisterViewModel by viewModels()

    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var loginRedirectText: android.widget.TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Bind views to IDs
        usernameEditText = findViewById(R.id.usernameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        registerButton = findViewById(R.id.registerButton)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        loginRedirectText = findViewById(R.id.loginRedirectText)



        registerButton.setOnClickListener {
            val usernameInput = usernameEditText.text.toString().trim()
            val emailInput = emailEditText.text.toString().trim()
            val passwordInput = passwordEditText.text.toString()
            val confirmPasswordInput = confirmPasswordEditText.text.toString()

            if(usernameInput.isEmpty() || emailInput.isEmpty() || passwordInput.isEmpty() || confirmPasswordInput.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (passwordInput != confirmPasswordInput) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.registerUser(usernameInput, emailInput, passwordInput)
        }

        loginRedirectText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }


        viewModel.registrationSuccess.observe(this) {
            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        viewModel.error.observe(this) {
            Toast.makeText(this, "Error: $it", Toast.LENGTH_SHORT).show()
        }

        Log.d("RegisterActivity", "RegisterActivity started")
    }
}
