package com.example.movieapp.api.models

data class RegisterRequest(
    val username: String,
    val displayName: String,
    val password: String,
    val confirmPassword: String
)