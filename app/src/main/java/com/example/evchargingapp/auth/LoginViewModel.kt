package com.example.evchargingapp.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

/**
 * ViewModel responsible for login logic and exposing LiveData to LoginActivity.
 */
class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    // LiveData to indicate if login was successful
    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    // LiveData for any error messages during login
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // LiveData for fetched username after login success
    private val _username = MutableLiveData<String?>()
    val username: LiveData<String?> = _username

    /**
     * Attempts to login user using provided email and password.
     * On success, fetches the username from the database.
     */
    fun login(email: String, password: String) {
        repository.loginUser(email, password) { success, errorMsg ->
            if (success) {
                // Indicate login success
                _loginSuccess.postValue(true)

                // Get current Firebase user UID
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid != null) {
                    // Fetch username from Realtime Database
                    repository.fetchUsername(uid) { fetchedUsername ->
                        if (fetchedUsername != null) {
                            _username.postValue(fetchedUsername)
                        } else {
                            _error.postValue("Failed to fetch username")
                        }
                    }
                } else {
                    _error.postValue("User UID not found")
                }
            } else {
                // Login failed - post error and failure status
                _loginSuccess.postValue(false)
                _error.postValue(errorMsg ?: "Unknown error")
            }
        }
    }
}
