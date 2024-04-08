package com.example.movieapp.api.models

data class Review(
    val content: String,
    val createdAt: String,
    val id: String,
    val mediaId: String,
    val mediaPoster: String,
    val mediaTitle: String,
    val mediaType: String,
    val updatedAt: String,
    val user: User
)
