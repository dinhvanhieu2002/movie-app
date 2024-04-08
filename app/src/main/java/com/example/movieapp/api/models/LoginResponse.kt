package com.example.movieapp.api.models

data class LoginResponse(
    val token: String,
    val id: String,
    val displayName: String,
    val username: String
)