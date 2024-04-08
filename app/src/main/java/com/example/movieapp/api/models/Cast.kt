package com.example.movieapp.api.models

import com.google.gson.annotations.SerializedName

data class Cast(
    @SerializedName("profile_path")
    val profilePath: String?,
    val id: Int,
    val name: String,
)