package com.example.movieapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.movieapp.repositories.AuthRepository

@Suppress("UNCHECKED_CAST")
class LoginViewModelFactory(val authRepository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(authRepository) as T
    }
}