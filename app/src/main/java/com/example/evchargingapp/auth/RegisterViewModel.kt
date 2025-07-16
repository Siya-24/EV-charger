package com.example.evchargingapp.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

// 🟢 ViewModel – RegisterViewModel.kt (No OTP version)
// → Handles interaction between the UI and business logic. Calls repository methods and exposes LiveData.

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)

    private val _registrationSuccess = MutableLiveData<Boolean>()
    val registrationSuccess: LiveData<Boolean> = _registrationSuccess

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun registerUser(username: String, email: String, password: String) {
        repository.registerUserViaApi(username, email, password) { success, errorMsg ->
            if (success) {
                _registrationSuccess.postValue(true)
            } else {
                _error.postValue(errorMsg ?: "Registration failed")
            }
        }
    }
}
