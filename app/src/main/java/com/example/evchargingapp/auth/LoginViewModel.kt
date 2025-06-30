package com.example.evchargingapp.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
//import com.example.evchargingapp.auth.AuthRepository

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun login(email: String, password: String) {
        repository.loginUser(email, password) { success, errorMsg ->
            if (success) {
                _loginSuccess.postValue(true)
            } else {
                _error.postValue(errorMsg ?: "Login failed")
            }
        }
    }
}
