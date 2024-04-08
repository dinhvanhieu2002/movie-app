package com.example.movieapp.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ApiHelper {
    companion object {
        val mediaCategory = mapOf(
            "popular" to "popular",
            "top_rated" to "top_rated"
        )

        fun getBackdropPath(imgEndpoint: String) : String {
            return "https://image.tmdb.org/t/p/original${imgEndpoint}"
        }

        fun getPosterPath(imgEndpoint: String) : String {
            return "https://image.tmdb.org/t/p/w500${imgEndpoint}"
        }

        fun getYoutubePath(videoId: String) : String {
            return "https://www.youtube.com/embed/${videoId}"
        }
        fun getAuthorizationHeader(token: String) : String {
            return "Bearer $token"
        }

        fun convertDateTime(dateTime: String): String {
            val instant = Instant.parse(dateTime)
            val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            val outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")

            return localDateTime.format(outputFormatter)
        }
    }
}