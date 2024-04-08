package com.example.movieapp.api.models

data class MediaListResponse(
    val page: Int,
    val results: MutableList<Result>,
)