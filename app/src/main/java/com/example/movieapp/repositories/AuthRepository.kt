package com.example.movieapp.repositories

import com.example.movieapp.api.RetrofitInstance
import com.example.movieapp.api.models.LoginRequest
import com.example.movieapp.api.models.RegisterRequest
import retrofit2.http.Body

class AuthRepository {
    suspend fun login(@Body loginRequest: LoginRequest) = RetrofitInstance.api.login(loginRequest)

    suspend fun register(@Body registerRequest: RegisterRequest) = RetrofitInstance.api.register(registerRequest)
}