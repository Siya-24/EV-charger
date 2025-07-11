package com.example.evchargingapp.user

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun getUsername(): String? {
        return prefs.getString("username", null)
    }

    fun logout() {
        prefs.edit().clear().apply()
        FirebaseAuth.getInstance().signOut()
    }
}
