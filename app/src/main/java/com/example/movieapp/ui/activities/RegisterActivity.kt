package com.example.movieapp.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.movieapp.api.models.ErrorResponse
import com.example.movieapp.databinding.ActivityRegisterBinding
import com.example.movieapp.repositories.AuthRepository
import com.example.movieapp.ui.viewmodel.LoginViewModel
import com.example.movieapp.ui.viewmodel.LoginViewModelFactory
import com.example.movieapp.utils.AuthTokenHelper
import com.example.movieapp.utils.Resource
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    lateinit var viewModel : LoginViewModel
    val TAG = "Register Screen"
    lateinit var authTokenHelper: AuthTokenHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.setOnClickListener {
            hideKeyboard()
        }

        val repository = AuthRepository()
        val viewModelProviderFactory = LoginViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[LoginViewModel::class.java]
        authTokenHelper = AuthTokenHelper(this)

        binding.btnSignUp.setOnClickListener {
            clickRegister()
        }

        binding.tvSignIn.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }

        viewModel.userRegister.observe(this, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let {
                        Snackbar.make(binding.root, "Register successful", Snackbar.LENGTH_LONG).show()
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        val errorResponse = Gson().fromJson(message, ErrorResponse::class.java)
                        Snackbar.make(binding.root, errorResponse.message, Snackbar.LENGTH_LONG).show()
                        Log.d(TAG, errorResponse.message)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun hideKeyboard() {
        val view: View? = currentFocus
        if (view != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun clickRegister() {
        val username = binding.etUsername.text.toString().trim()
        val displayName = binding.etDisplayName.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if(username.isEmpty() || password.isEmpty() || displayName.isEmpty() || confirmPassword.isEmpty()) {
            Snackbar.make(binding.root, "Please enter into fields", Snackbar.LENGTH_LONG).show()
        } else if(password != confirmPassword){
            Snackbar.make(binding.root, "Confirm password not match", Snackbar.LENGTH_LONG).show()
        } else {
            viewModel.register(username, displayName, password, confirmPassword)
        }
    }

    var isLoading = false

    private fun hideProgressBar() {
        binding.loadingProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.loadingProgressBar.visibility = View.VISIBLE
        isLoading = true
    }
}