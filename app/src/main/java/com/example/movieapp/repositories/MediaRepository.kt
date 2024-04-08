package com.example.movieapp.repositories

import com.example.movieapp.api.RetrofitInstance
import com.example.movieapp.api.models.Media
import com.example.movieapp.api.models.ReviewRequest
import com.example.movieapp.db.MediaDatabase

class MediaRepository(val db: MediaDatabase) {
    suspend fun getGenres(mediaType: String) = RetrofitInstance.api.getGenres(mediaType)
    suspend fun getMediaList(mediaType: String, mediaCategory: String, page: Int) = RetrofitInstance.api.getMediaList(mediaType, mediaCategory, page)
    suspend fun getMediaSearch(mediaType: String, query: String, page: Int) = RetrofitInstance.api.getMediaSearch(mediaType, query, page)
    suspend fun getMediaDetail(mediaType: String, mediaId: String) = RetrofitInstance.api.getMediaDetail(mediaType, mediaId)

    suspend fun addReview(review: ReviewRequest, authorization: String) = RetrofitInstance.api.addReview(review, authorization)
    suspend fun removeReview(reviewId: String, authorization: String) = RetrofitInstance.api.removeReview(reviewId, authorization)
    suspend fun getAllReview(authorization: String) = RetrofitInstance.api.getListReview(authorization)
    suspend fun addFavorite(media: Media) = db.getMediaDao().upsert(media)
    suspend fun removeFavorite(media: Media) = db.getMediaDao().delete(media)
    suspend fun removeFavoriteById(mediaId: String) = db.getMediaDao().deleteById(mediaId)
    fun getAllFavorite() = db.getMediaDao().getAllFavorite()
    suspend fun getPersonDetail(personId: String) = RetrofitInstance.api.getPersonDetail(personId)
    suspend fun getPersonMedias(personId: String) = RetrofitInstance.api.getPersonMedias(personId)

}