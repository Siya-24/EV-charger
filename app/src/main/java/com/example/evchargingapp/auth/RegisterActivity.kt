package com.example.evchargingapp.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.evchargingapp.R

class RegisterActivity : AppCompatActivity() {

    private val viewModel: RegisterViewModel by viewModels()

    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var otpEditText: EditText
    private lateinit var resendOtpText: TextView
    private lateinit var registerButton: Button
    private lateinit var sendOtpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //-------- After designing the vars as private:

        // Bind views to IDs
        usernameEditText = findViewById(R.id.usernameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        otpEditText = findViewById(R.id.otpEditText)
        resendOtpText = findViewById(R.id.resendOtpText)
        registerButton = findViewById(R.id.registerButton)
        sendOtpButton = findViewById(R.id.sendOtpButton)
        //-----------------------------------------------------

        // send otp button ------------------------------------------------
        sendOtpButton.setOnClickListener {
            val email = emailEditText.text.toString()
            viewModel.sendOtp(email)
        }

        // Handle "Resend code" click
        resendOtpText.setOnClickListener {
            Toast.makeText(this, "OTP resent", Toast.LENGTH_SHORT).show()
            // Optionally trigger resend logic here
        }

        registerButton.setOnClickListener {
            val usernameInput = usernameEditText.text.toString().trim()
            val emailInput = emailEditText.text.toString().trim()
            val passwordInput = passwordEditText.text.toString()
            val otpInput = otpEditText.text.toString()

            //++++++++++++++++++++ NOTE: currently leaving the otp field empty ++++++++++++++++++++++++++
            if (usernameInput.isEmpty() || emailInput.isEmpty() || passwordInput.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.verifyOtp(emailInput, otpInput)
        }

        // LiveData observers
        viewModel.otpSent.observe(this) {
            Toast.makeText(this, "OTP Sent", Toast.LENGTH_SHORT).show()
        }

        viewModel.otpVerified.observe(this) { verified ->
            if (verified) {
                val username = usernameEditText.text.toString().trim()
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString()
                val otp = otpEditText.text.toString()

                Toast.makeText(this, "OTP Verified", Toast.LENGTH_SHORT).show()
                viewModel.registerUser(username, email, password, otp)
            } else {
                Toast.makeText(this, "OTP verification failed", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.registrationSuccess.observe(this) {
            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent) // start of new activity
            finish() // finishing the existing activity
        }

        viewModel.error.observe(this) {
            Toast.makeText(this, "Error: $it", Toast.LENGTH_SHORT).show()
        }

        Log.d("LoginActivity", "LoginActivity started")
    }
}
