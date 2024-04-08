package com.example.movieapp.api.models

data class ReviewRequest(
    val content: String,
    val mediaId: String,
    val mediaType: String,
    val mediaTitle: String,
    val mediaPoster: String,
)