package com.example.movieapp.api.models

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("genre_ids")
    val genreIds: List<Int>,
    val id: Int,
    val overview: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("profile_path")
    val profilePath: String?,
    @SerializedName("known_for_department")
    val knownForDepartment: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    @SerializedName("first_air_date")
    val firstAirDate: String?,
    val title: String?,
    val name: String?,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("media_type")
    val mediaType: String?
)