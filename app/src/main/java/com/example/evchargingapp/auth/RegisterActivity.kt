package com.example.evchargingapp.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.evchargingapp.network.ApiResponse
import com.example.evchargingapp.data.MyDatabaseHelper
import com.example.evchargingapp.R
import com.example.evchargingapp.network.RetrofitClient
import com.example.evchargingapp.network.SendOtpRequest
import com.example.evchargingapp.data.User
import com.example.evchargingapp.network.VerifyOtpRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
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

        auth = FirebaseAuth.getInstance()

    //        val username = findViewById<EditText>(R.id.usernameEditText)
    //        val email = findViewById<EditText>(R.id.emailEditText)
    //        val password = findViewById<EditText>(R.id.passwordEditText)
    //        val registerButton = findViewById<Button>(R.id.registerButton)

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
        sendOtpButton = findViewById<Button>(R.id.sendOtpButton)
        sendOtpButton.setOnClickListener {
            val email = emailEditText.text.toString()

            RetrofitClient.instance.sendOtp(SendOtpRequest(email))
                .enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@RegisterActivity, "OTP Sent", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@RegisterActivity, "Failed to send OTP", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
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

            // Verify OTP before creating user
            RetrofitClient.instance.verifyOtp(VerifyOtpRequest(emailInput, otpInput))
                .enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(this@RegisterActivity, "OTP Verified", Toast.LENGTH_SHORT).show()


            auth.createUserWithEmailAndPassword(emailInput, passwordInput)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                        val user = User(uid, usernameInput, emailInput, otpInput)
                        val database = FirebaseDatabase.getInstance("https://evse-170a5-default-rtdb.asia-southeast1.firebasedatabase.app")

                        val usersRef = database.getReference("users")

                        usersRef.child(uid).setValue(user)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(this@RegisterActivity, "User data saved successfully", Toast.LENGTH_SHORT).show()

                                    // Save to SQLite----------------------------------------------
                                    val dbHelper = MyDatabaseHelper(this@RegisterActivity)
                                    val inserted = dbHelper.insertUser(usernameInput, emailInput, otpInput)
                                    if (inserted) {
                                        Toast.makeText(this@RegisterActivity, "User stored in SQLite", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(this@RegisterActivity, "SQLite insertion failed", Toast.LENGTH_SHORT).show()
                                    }
                                //---------------------------------------------------------------------------

                                    // SHOW success toast & NAVIGATE
                                    Toast.makeText(this@RegisterActivity, "Registration Successful", Toast.LENGTH_SHORT).show()

                                    val intent =
                                        Intent(this@RegisterActivity, LoginActivity::class.java)
                                    // to start new activity, it navigates to the loginActivity as it is mentioned here
                                    startActivity(intent)
                                    // closes the current activity (i.e registerActivity)
                                    finish()
                                } else {
                                    Toast.makeText(this@RegisterActivity, "Failed to save user to DB", Toast.LENGTH_SHORT).show()
                                    Log.e("FirebaseError", "DB Error", dbTask.exception)
                                }
                            }
                    } else {
                        Toast.makeText(this@RegisterActivity, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        Log.e("AuthError", "Register failed", task.exception)
                    }
                }

        }

        Log.d("LoginActivity", "LoginActivity started")

    }

                    override fun onFailure(
                        call: Call<ApiResponse?>,
                        t: Throwable
                    ) {
                        TODO("Not yet implemented")
                    }


                })}}}