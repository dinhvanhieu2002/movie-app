package com.example.movieapp.utils

import android.content.Context
import android.content.SharedPreferences

class AuthTokenHelper(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "MyAppPrefs"
        private const val KEY_TOKEN = "token"
        private const val KEY_DISPLAY_NAME = "displayName"
        private const val KEY_USER_ID = "id"
    }

    fun saveDisplayName(displayName: String) {
        sharedPreferences.edit().putString(KEY_DISPLAY_NAME, displayName).apply()
    }

    fun getDisplayName(): String? {
        return sharedPreferences.getString(KEY_DISPLAY_NAME, null)
    }

    fun saveUserId(displayName: String) {
        sharedPreferences.edit().putString(KEY_USER_ID, displayName).apply()
    }

    fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }

    fun saveToken(token: String) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun clearToken() {
        sharedPreferences.edit().remove(KEY_TOKEN).apply()
    }
}

