package com.example.evchargingapp
//++++++++++++++++++++This code will be used for the sign up activity using firebase++++++++++++++++
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase // added this for the real-time database after adding the implementation in the build.gradle app level

import com.example.evchargingapp.User
// importing the user data class for creating a user in real time database....this is being used at line 53


class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var otpEditText: EditText
    private lateinit var resendOtpText: TextView
    private lateinit var registerButton: Button

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
    //-----------------------------------------------------

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

                                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
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


}