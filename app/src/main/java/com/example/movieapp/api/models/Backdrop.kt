package com.example.movieapp.api.models

import com.google.gson.annotations.SerializedName

data class Backdrop(
    @SerializedName("file_path")
    val filePath: String,
)