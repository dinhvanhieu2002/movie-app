package com.example.movieapp.api.models

import com.google.gson.annotations.SerializedName

data class PersonDetailResponse(
    val biography: String,
    val birthday: String,
    val id: Int,
    val name: String,
    @SerializedName("profile_path")
    val profilePath: String
)