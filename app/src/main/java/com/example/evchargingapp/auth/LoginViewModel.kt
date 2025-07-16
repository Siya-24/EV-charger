package com.example.evchargingapp.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _username = MutableLiveData<String?>()
    val username: LiveData<String?> = _username

    /**
     * Attempts to login user using provided email and password via your API only.
     */
    fun login(email: String, password: String) {
        repository.loginUserViaApi(email, password) { success, errorMsg ->
            if (success) {
                _loginSuccess.postValue(true)
                // OPTIONAL: If your API returns a username or token, you can store it here
            } else {
                _loginSuccess.postValue(false)
                _error.postValue(errorMsg ?: "Unknown error")
            }
        }
    }
}
