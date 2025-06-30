package com.example.evchargingapp.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

// 🟢 ViewModel – RegisterViewModel.kt
// → Handles interaction between the UI and business logic. Calls repository methods and exposes LiveData.

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)

    private val _otpSent = MutableLiveData<Boolean>()
    val otpSent: LiveData<Boolean> = _otpSent

    private val _otpVerified = MutableLiveData<Boolean>()
    val otpVerified: LiveData<Boolean> = _otpVerified

    private val _registrationSuccess = MutableLiveData<Boolean>()
    val registrationSuccess: LiveData<Boolean> = _registrationSuccess

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun sendOtp(email: String) {
        repository.sendOtp(email) { success, errorMsg ->
            if (success) _otpSent.postValue(true)
            else _error.postValue(errorMsg ?: "Failed to send OTP")
        }
    }

    fun verifyOtp(email: String, otp: String) {
        repository.verifyOtp(email, otp) { success ->
            _otpVerified.postValue(success)
        }
    }

    fun registerUser(username: String, email: String, password: String, otp: String) {
        repository.registerUser(username, email, password, otp) { success, errorMsg ->
            if (success) _registrationSuccess.postValue(true)
            else _error.postValue(errorMsg ?: "Registration failed")
        }
    }
}
