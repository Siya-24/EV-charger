package com.example.evchargingapp.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory to create LoginViewModel instances with a constructor parameter.
 */
class LoginViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the requested ViewModel class matches LoginViewModel
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        // Throw if ViewModel class is unknown
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
