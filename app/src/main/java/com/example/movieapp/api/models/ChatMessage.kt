package com.example.movieapp.api.models

data class ChatMessage(
    var senderId: String = "",
    var senderName: String = "",
    var message: String = "",
    var timestamp: Long = System.currentTimeMillis()
)