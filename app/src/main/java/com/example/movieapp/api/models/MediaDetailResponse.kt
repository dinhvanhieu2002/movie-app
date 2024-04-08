package com.example.movieapp.api.models

import com.google.gson.annotations.SerializedName

data class MediaDetailResponse(
    @SerializedName("backdrop_path")
    val backdropPath: String,
    val credits: Credits,
    val genres: List<Genre>,
    val id: Int,
    val images: Images,
    val overview: String,
    @SerializedName("poster_path")
    val posterPath: String,
    val recommend: List<Any>,
    @SerializedName("release_date")
    val releaseDate: String,
    val reviews: List<Review>,
    val title: String?,
    val name: String?,
    val videos: Videos,
    @SerializedName("vote_average")
    val voteAverage: Double,
)