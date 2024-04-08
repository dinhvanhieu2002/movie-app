package com.example.movieapp.api.models

data class NotificationData(
    val title: String,
    val message: String,
    val poster: String,
    val mediaId: String,
    val mediaType: String
)