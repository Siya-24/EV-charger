package com.example.evchargingapp.auth

import android.content.Context
import android.util.Log
import com.example.evchargingapp.data.ChargingPile
import com.example.evchargingapp.data.LoginRequest
import com.example.evchargingapp.data.LoginResponse
import com.example.evchargingapp.data.MyDatabaseHelper
import com.example.evchargingapp.data.SignupRequest
import com.example.evchargingapp.data.SignupResponse
import com.example.evchargingapp.data.User  // ✅ should point here
import com.example.evchargingapp.network.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

//    fun registerUser(
//        username: String,
//        email: String,
//        password: String,
//        onComplete: (Boolean, String?) -> Unit
//    ) {
//        auth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
//                    val user = User(uid, username, email)
//                    val database = FirebaseDatabase.getInstance("https://evse-170a5-default-rtdb.asia-southeast1.firebasedatabase.app")
//
//                    val usersRef = database.getReference("users")
//
//                    usersRef.child(uid).setValue(user)
//                        .addOnCompleteListener { dbTask ->
//                            if (dbTask.isSuccessful) {
//                                val dbHelper = MyDatabaseHelper(context)
//                                val inserted = dbHelper.insertUser(username, email)
//                                if (inserted) {
//                                    onComplete(true, null)
//                                } else {
//                                    onComplete(false, "SQLite insertion failed")
//                                }
//                            } else {
//                                onComplete(false, dbTask.exception?.message)
//                                Log.e("FirebaseError", "DB Error", dbTask.exception)
//                            }
//                        }
//                } else {
//                    onComplete(false, task.exception?.message)
//                    Log.e("AuthError", "Register failed", task.exception)
//                }
//            }
//    }

//    fun loginUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
//        auth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    onComplete(true, null)
//                } else {
//                    onComplete(false, task.exception?.message)
//                }
//            }
//    }

    /**
     * Fetches the username for a given user ID (uid) from Firebase Realtime Database.
     *
     * @param uid The Firebase user ID.
     * @param callback Lambda to receive username string or null if failed.
     */
    fun fetchUsername(uid: String, callback: (String?) -> Unit) {
        val database = FirebaseDatabase.getInstance("https://evse-170a5-default-rtdb.asia-southeast1.firebasedatabase.app")
        val userRef = database.getReference("users").child(uid).child("username")

        // Attempt to get username once
        userRef.get().addOnSuccessListener { snapshot ->
            Log.d("DEBUG_USERNAME", "Raw snapshot: ${snapshot.value}")
            val username = snapshot.getValue(String::class.java)
            callback(username)
        }.addOnFailureListener {
            // Return null if failed to fetch username
            callback(null)
        }
    }



    // ✅ ADD THIS METHOD TO FIX unresolved reference
    /**
     * Adds a new charging pile to Firebase under the current user's node
     * and also stores it locally in SQLite for offline access.
     *
     * @param pile The ChargingPile object to be added
     * @param callback Called with (true, null) if successful, or (false, errorMessage) if failed
     */
    fun addChargingPile(pile: ChargingPile, callback: (Boolean, String?) -> Unit) {
        val database = FirebaseDatabase.getInstance("https://evse-170a5-default-rtdb.asia-southeast1.firebasedatabase.app")
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid == null) {
            callback(false, "User not logged in")
            return
        }

        val pilesRef = database.getReference("users").child(uid).child("piles")

        pilesRef.child(pile.id).setValue(pile)
            .addOnSuccessListener {
                val dbHelper = MyDatabaseHelper(context)
                // Pass userId here to scope the pile correctly in SQLite
                val inserted = dbHelper.insertPile(uid, pile.id, pile.name, pile.isOnline)

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


    fun registerUserViaApi(username: String, email: String, password: String, callback: (Boolean, String?) -> Unit) {
        val request = SignupRequest(username, email, password)
        val call = AuthRetrofitClient.apiService.registerUser(request)

        call.enqueue(object : retrofit2.Callback<SignupResponse> {
            override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    callback(true, null)
                } else {
                    callback(false, response.body()?.message ?: "Server error")
                }
            }

            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                callback(false, t.message ?: "Network failure")
            }
        })
    }

    fun loginUserViaApi(
        email: String,
        password: String,
        callback: (Boolean, String?, String?) -> Unit // (success, errorMsg, username)
    ) {
        val request = LoginRequest(email, password)
        val call = AuthRetrofitClient.apiService.loginUser(request)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val token = response.body()?.token
                    val username = response.body()?.username // ✅ Extract username

                    val sharedPref = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                    sharedPref.edit().apply {
                        putString("token", token)
                        putString("username", username) // ✅ Save username
                        apply()
                    }

                    callback(true, null, username) // ✅ Pass username to ViewModel
                } else {
                    callback(false, response.body()?.message ?: "Server error", null)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                callback(false, t.message ?: "Network error", null)
            }
        })
    }









}