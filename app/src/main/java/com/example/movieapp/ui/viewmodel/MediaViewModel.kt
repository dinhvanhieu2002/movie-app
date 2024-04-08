package com.example.movieapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.api.models.Media
import com.example.movieapp.api.models.PersonDetailResponse
import com.example.movieapp.api.models.PersonMediasResponse
import com.example.movieapp.api.models.Review
import com.example.movieapp.api.models.ReviewRequest
import com.example.movieapp.api.models.ReviewX
import com.example.movieapp.repositories.MediaRepository
import com.example.movieapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class MediaViewModel(val mediaRepository: MediaRepository) : ViewModel() {

    var listReview : MutableLiveData<Resource<List<ReviewX>>> = MutableLiveData()
    var reviewResponse : MutableLiveData<Review> = MutableLiveData()
    var checkResponse : MutableLiveData<Boolean> = MutableLiveData()

    var personMedias : MutableLiveData<Resource<PersonMediasResponse>> = MutableLiveData()
    var personDetail : MutableLiveData<Resource<PersonDetailResponse>> = MutableLiveData()
//    var personMediasFilter : MutableLiveData<List<Result>> = MutableLiveData()
//    val filterMedias = mutableListOf<Result>()
//    var page = 1
//    var skip = 8

    //add remove list review
    fun addReview(review: ReviewRequest, authorization: String) = viewModelScope.launch {
        val response = mediaRepository.addReview(review, authorization)
        checkResponse.postValue(false)
        if(response.isSuccessful) {
//            response.body()?.let {  resultResponse ->
//                reviewResponse.postValue(resultResponse)
//                getAllReview(authorization)
//            }
            checkResponse.postValue(true)
            getAllReview(authorization)
        } else {
            Log.e("Review Add", "error")
        }
    }

    fun getAllReview(authorization: String) = viewModelScope.launch {
        listReview.postValue(Resource.Loading())
        val response = mediaRepository.getAllReview(authorization)
        listReview.postValue(handleGetAllReview(response))
    }

    private fun handleGetAllReview(response: Response<List<ReviewX>>) : Resource<List<ReviewX>> {
        if(response.isSuccessful) {
            response.body()?.let {  resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        Log.e("handleGetMediaDetail", response.errorBody()!!.string())
        return Resource.Error(response.errorBody()!!.string())
    }

    fun removeReview(reviewId: String, authorization: String) = viewModelScope.launch {
        val response = mediaRepository.removeReview(reviewId, authorization)
        if(response.isSuccessful) {
            getAllReview(authorization)
        } else {
            Log.e("Review Add", "error")
        }


    }

    //add remove favorite
    fun addFavorite(media: Media) = viewModelScope.launch {
        mediaRepository.addFavorite(media)
    }

    fun getAllFavorite() = mediaRepository.getAllFavorite()

    fun removeFavorite(media: Media) = viewModelScope.launch {
        mediaRepository.removeFavorite(media)
    }

    fun removeFavoriteById(mediaId: String) = viewModelScope.launch {
        mediaRepository.removeFavoriteById(mediaId)
    }

    fun getPersonDetail(personId: String) = viewModelScope.launch {
        personDetail.postValue(Resource.Loading())
        val response = mediaRepository.getPersonDetail(personId)
        personDetail.postValue(handleGetPersonDetail(response))
    }

    private fun handleGetPersonDetail(response: Response<PersonDetailResponse>) : Resource<PersonDetailResponse> {
        if(response.isSuccessful) {
            response.body()?.let {  resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.errorBody()!!.string())
    }

    fun getPersonMedias(personId: String) = viewModelScope.launch {
        personMedias.postValue(Resource.Loading())
        val response = mediaRepository.getPersonMedias(personId)
        personMedias.postValue(handleGetPersonMedias(response))
    }

    private fun handleGetPersonMedias(response: Response<PersonMediasResponse>) : Resource<PersonMediasResponse> {
        if(response.isSuccessful) {
            response.body()?.let {  resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.errorBody()!!.string())
    }

//    fun loadMorePersonMedias() {
//        val tempFilterMedias = personMedias.value!!.data!!.cast.slice(page until skip)
//        Log.e("person page", page.toString())
//        Log.e("person skip", skip.toString())
//        Log.e("person loadmore temp size", tempFilterMedias.size.toString())
//        filterMedias.addAll(tempFilterMedias)
//        Log.e("person loadmore filter media size", filterMedias.size.toString())
//        personMediasFilter.postValue(filterMedias)
//        page=skip
//        skip+=8
//    }


}