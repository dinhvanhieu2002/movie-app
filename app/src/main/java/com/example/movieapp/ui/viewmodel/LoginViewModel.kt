package com.example.movieapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.api.models.LoginRequest
import com.example.movieapp.api.models.LoginResponse
import com.example.movieapp.api.models.RegisterRequest
import com.example.movieapp.api.models.RegisterResponse
import com.example.movieapp.repositories.AuthRepository
import com.example.movieapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel(val authRepository: AuthRepository) : ViewModel() {

    val userLogin: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val userRegister: MutableLiveData<Resource<RegisterResponse>> = MutableLiveData()

    fun login(username: String, password: String) = viewModelScope.launch {
        val loginRequest = LoginRequest(username, password)
        userLogin.postValue(Resource.Loading())
        val response = authRepository.login(loginRequest)
        userLogin.postValue(handleLogin(response))
    }

    private fun handleLogin(response: Response<LoginResponse>) : Resource<LoginResponse> {
        if(response.isSuccessful) {
            response.body()?.let {  resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.errorBody()!!.string())
    }

    fun register(username: String, displayName: String, password: String, confirmPassword: String) = viewModelScope.launch {
        val registerRequest = RegisterRequest(username, displayName, password, confirmPassword)
        userRegister.postValue(Resource.Loading())
        val response = authRepository.register(registerRequest)
        userRegister.postValue(handleRegister(response))
    }

    private fun handleRegister(response: Response<RegisterResponse>) : Resource<RegisterResponse> {
        if(response.isSuccessful) {
            response.body()?.let {  resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.errorBody()!!.string())
    }
}