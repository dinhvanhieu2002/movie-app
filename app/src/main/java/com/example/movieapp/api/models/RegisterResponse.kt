package com.example.movieapp.api.models

data class RegisterResponse(
    val createdAt: String,
    val displayName: String,
    val id: String,
    val password: String,
    val salt: String,
    val token: String,
    val updatedAt: String,
    val username: String
)