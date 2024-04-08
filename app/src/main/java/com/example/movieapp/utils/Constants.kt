package com.example.movieapp.utils

import com.example.movieapp.api.models.Genre

class Constants {
    companion object {
        const val BASE_URL = "https://nixflet-movie-api.vercel.app/api/v1/"
        const val SEARCH_MEDIA_TIME_DELAY = 500L

        const val BASE_MESSAGE_CLOUD_URL = "https://fcm.googleapis.com/"
        const val SERVER_KEY = "AAAAu5fnFMY:APA91bGrPdW5_g2_uegI6luAXM530ZHcVeW2vOPeUq4HX_7hwg6mu4IF1eW0UHGnJ27zklpklij8TKG6ys0rHZqEzAj_ZoG7F2nFrRv52Teg57qS7TjUpn0OQjVj62VMEOT9-hUxFF4o"
        const val CONTENT_TYPE = "application/json"

        fun getGenres(mediaType: String) : List<Genre> {
            return if(mediaType == "movie") {
                listOf(
                    Genre(28, "Action"),
                    Genre(12, "Adventure"),
                    Genre(16, "Animation"),
                    Genre(35, "Comedy"),
                    Genre(80, "Crime"),
                    Genre(99, "Documentary"),
                    Genre(18, "Drama"),
                    Genre(10751, "Family"),
                    Genre(14, "Fantasy"),
                    Genre(36, "History"),
                    Genre(27, "Horror"),
                    Genre(10402, "Music"),
                    Genre(9648, "Mystery"),
                    Genre(10749, "Romance"),
                    Genre(878, "Science Fiction"),
                    Genre(10770, "TV Movie"),
                    Genre(53, "Thriller"),
                    Genre(10752, "War"),
                    Genre(37, "Western"),

                )
            } else {
                listOf(
                    Genre(10759, "Action & Adventure"),
                    Genre(16, "Animation"),
                    Genre(35, "Comedy"),
                    Genre(80, "Crime"),
                    Genre(99, "Documentary"),
                    Genre(18, "Drama"),
                    Genre(10751, "Family"),
                    Genre(10762, "Kids"),
                    Genre(9648, "Mystery"),
                    Genre(10763, "News"),
                    Genre(10764, "Reality"),
                    Genre(10765, "Sci-Fi & Fantasy"),
                    Genre(10766, "Soap"),
                    Genre(10767, "Talk"),
                    Genre(10768, "War & Politics"),
                    Genre(37, "Western"),
                )
            }
        }
    }
}