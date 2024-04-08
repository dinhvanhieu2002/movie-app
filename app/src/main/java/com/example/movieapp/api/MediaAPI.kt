package com.example.movieapp.api

import com.example.movieapp.api.models.GenresResponse
import com.example.movieapp.api.models.LoginRequest
import com.example.movieapp.api.models.LoginResponse
import com.example.movieapp.api.models.MediaDetailResponse
import com.example.movieapp.api.models.MediaListResponse
import com.example.movieapp.api.models.PersonDetailResponse
import com.example.movieapp.api.models.PersonMediasResponse
import com.example.movieapp.api.models.RegisterRequest
import com.example.movieapp.api.models.RegisterResponse
import com.example.movieapp.api.models.Review
import com.example.movieapp.api.models.ReviewRequest
import com.example.movieapp.api.models.ReviewX
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MediaAPI {
    //media
    @GET("{mediaType}/{mediaCategory}")
    suspend fun getMediaList(
        @Path("mediaType") mediaType: String,
        @Path("mediaCategory") mediaCategory: String,
        @Query("page") page: Int = 1,
    ) : Response<MediaListResponse>

    @GET("{mediaType}/detail/{mediaId}")
    suspend fun getMediaDetail(
        @Path("mediaType") mediaType: String,
        @Path("mediaId") mediaId: String,
    ) : Response<MediaDetailResponse>

    @GET("{mediaType}/search")
    suspend fun getMediaSearch(
        @Path("mediaType") mediaType: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
    ) : Response<MediaListResponse>

    @GET("{mediaType}/genres")
    suspend fun getGenres(
        @Path("mediaType") mediaType: String,
    ) : Response<GenresResponse>

    @GET("person/{personId}")
    suspend fun getPersonDetail(
        @Path("personId") personId : String
    ) : Response<PersonDetailResponse>

    @GET("person/{personId}/medias")
    suspend fun getPersonMedias(
        @Path("personId") personId : String
    ) : Response<PersonMediasResponse>

    //auth
    @POST("user/signin")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ) : Response<LoginResponse>

//    @GET("user/info")
//    suspend fun getInfo()

    @POST("user/signup")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ) : Response<RegisterResponse>

    //review
    @POST("reviews")
    suspend fun addReview(
        @Body reviewRequest: ReviewRequest,
        @Header("Authorization") authorization: String
    ) : Response<Review>

    @GET("reviews")
    suspend fun getListReview(@Header("Authorization") authorization: String) : Response<List<ReviewX>>

    @DELETE("reviews/{reviewId}")
    suspend fun removeReview(
        @Path("reviewId") reviewId: String,
        @Header("Authorization") authorization: String
    ) : Response<Unit>
}