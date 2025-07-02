package com.example.evchargingapp.auth

import android.content.Context
import android.util.Log
import com.example.evchargingapp.data.ChargingPile
import com.example.evchargingapp.data.MyDatabaseHelper
import com.example.evchargingapp.data.User
import com.example.evchargingapp.network.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun sendOtp(email: String, callback: (Boolean, String?) -> Unit) {
        RetrofitClient.instance.sendOtp(SendOtpRequest(email))
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    callback(response.isSuccessful, null)
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    callback(false, t.message)
                }
            })
    }

    fun verifyOtp(email: String, otp: String, callback: (Boolean) -> Unit) {
        RetrofitClient.instance.verifyOtp(VerifyOtpRequest(email, otp))
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    callback(response.isSuccessful && response.body()?.success == true)
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    callback(false)
                }
            })
    }

    fun registerUser(
        username: String,
        email: String,
        password: String,
        otp: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val user = User(uid, username, email, otp)
                    val database = FirebaseDatabase.getInstance("https://evse-170a5-default-rtdb.asia-southeast1.firebasedatabase.app")

                    val usersRef = database.getReference("users")

                    usersRef.child(uid).setValue(user)
                        .addOnCompleteListener { dbTask ->
                            if (dbTask.isSuccessful) {
                                val dbHelper = MyDatabaseHelper(context)
                                val inserted = dbHelper.insertUser(username, email, otp)
                                if (inserted) {
                                    onComplete(true, null)
                                } else {
                                    onComplete(false, "SQLite insertion failed")
                                }
                            } else {
                                onComplete(false, dbTask.exception?.message)
                                Log.e("FirebaseError", "DB Error", dbTask.exception)
                            }
                        }
                } else {
                    onComplete(false, task.exception?.message)
                    Log.e("AuthError", "Register failed", task.exception)
                }
            }
    }

    fun loginUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    // ✅ ADD THIS METHOD TO FIX unresolved reference
    fun addChargingPile(pile: ChargingPile, callback: (Boolean, String?) -> Unit) {
        val database = FirebaseDatabase.getInstance("https://evse-170a5-default-rtdb.asia-southeast1.firebasedatabase.app")
        val pilesRef = database.getReference("piles")

        pilesRef.child(pile.id).setValue(pile)
            .addOnSuccessListener {
                val dbHelper = MyDatabaseHelper(context)
                val inserted = dbHelper.insertPile(pile.id, pile.name, pile.isOnline)

                if (inserted) {
                    callback(true, null)
                } else {
                    callback(false, "SQLite insert failed")
                }
            }
            .addOnFailureListener { e ->
                callback(false, e.message)
            }
    }
}
