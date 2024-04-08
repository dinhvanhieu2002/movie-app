package com.example.movieapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.api.models.MediaListResponse
import com.example.movieapp.api.models.Result
import com.example.movieapp.repositories.MediaRepository
import com.example.movieapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class HomeViewModel(val mediaRepository: MediaRepository) : ViewModel() {
    var mediaHeroPager : MutableLiveData<Resource<MediaListResponse>> = MutableLiveData()
    var mediaPopularMovie : MutableLiveData<List<Result>> = MutableLiveData()
    var mediaPopularSeries : MutableLiveData<List<Result>> = MutableLiveData()
    var mediaTopRatedMovie : MutableLiveData<List<Result>> = MutableLiveData()
    var mediaTopRatedSeries : MutableLiveData<List<Result>> = MutableLiveData()

    init {
        getHeroPagerHome("movie", "popular")
        getMediaPopularMovie("movie", "popular")
        getMediaPopularSeries("tv", "popular")
        getMediaTopRatedMovie("movie", "top_rated")
        getMediaTopRatedSeries("tv", "top_rated")
    }

   private fun getHeroPagerHome(mediaType: String, mediaCategory: String) = viewModelScope.launch {
       mediaHeroPager.postValue(Resource.Loading())
       val response = mediaRepository.getMediaList(mediaType, mediaCategory, 1)
       mediaHeroPager.postValue(handleGetHeroPagerHome(response))
    }

    private fun handleGetHeroPagerHome(response : Response<MediaListResponse>) : Resource<MediaListResponse> {
        if(response.isSuccessful) {
            response.body()?.let {  resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.errorBody()!!.string())
    }

    private fun getMediaPopularMovie(mediaType: String, mediaCategory: String) = viewModelScope.launch {
        val response = mediaRepository.getMediaList(mediaType, mediaCategory, 1)
        if(response.isSuccessful) {
            response.body()?.let {  resultResponse ->
                mediaPopularMovie.postValue(resultResponse.results)
            }
        } else {
            Log.e("HomeViewModel", "Error call api")
        }
    }

    private fun getMediaPopularSeries(mediaType: String, mediaCategory: String) = viewModelScope.launch {
        val response = mediaRepository.getMediaList(mediaType, mediaCategory, 1)
        if(response.isSuccessful) {
            response.body()?.let {  resultResponse ->
                mediaPopularSeries.postValue(resultResponse.results)
            }
        } else {
            Log.e("HomeViewModel", "Error call api")
        }
    }

    fun getMediaTopRatedMovie(mediaType: String, mediaCategory: String) = viewModelScope.launch {
        val response = mediaRepository.getMediaList(mediaType, mediaCategory, 1)
        if(response.isSuccessful) {
            response.body()?.let {  resultResponse ->
                mediaTopRatedMovie.postValue(resultResponse.results)
            }
        } else {
            Log.e("HomeViewModel", "Error call api")
        }
    }

    fun getMediaTopRatedSeries(mediaType: String, mediaCategory: String) = viewModelScope.launch {
        val response = mediaRepository.getMediaList(mediaType, mediaCategory, 1)

        if(response.isSuccessful) {
            response.body()?.let {  resultResponse ->
                mediaTopRatedSeries.postValue(resultResponse.results)
            }
        } else {
            Log.e("HomeViewModel", "Error call api")
        }
    }


}