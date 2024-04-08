package com.example.movieapp.api.models

data class PushNotification(
    val data: NotificationData,
    val to: String
)