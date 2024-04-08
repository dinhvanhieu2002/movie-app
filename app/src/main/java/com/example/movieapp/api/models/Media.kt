package com.example.movieapp.api.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medias")
data class Media(
    val mediaType: String,
    @PrimaryKey
    val mediaId: String,
    val mediaTitle: String,
    val mediaPoster: String,
    val mediaRate: String,
)