package com.example.movieapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.api.models.MediaListResponse
import com.example.movieapp.repositories.MediaRepository
import com.example.movieapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class SearchViewModel(val mediaRepository: MediaRepository) : ViewModel() {


    val mediaList: MutableLiveData<Resource<MediaListResponse>> = MutableLiveData()
    var mediaListResponse: MediaListResponse? = null
    var page = 1
    private val _currentMediaType = MutableLiveData<String>()
    val currentMediaType: LiveData<String> = _currentMediaType

    fun setCurrentMediaType(mediaType: String) {
        _currentMediaType.value = mediaType
    }

    init {
        setCurrentMediaType("movie")
    }

    fun getMediaSearch(mediaType: String, query: String) = viewModelScope.launch {
        mediaList.postValue(Resource.Loading())
        val response = mediaRepository.getMediaSearch(mediaType, query, page)
        mediaList.postValue(handleGetMediaSearch(response))
    }

    fun handleGetMediaSearch(response: Response<MediaListResponse>) : Resource<MediaListResponse> {
        if(response.isSuccessful) {
            response.body()?.let {  resultResponse ->
                page++
                if(mediaListResponse == null) {
                    mediaListResponse = resultResponse
                } else {
                    val oldMediaList = mediaListResponse?.results
                    val newMediaList = resultResponse.results
                    mediaListResponse?.results?.addAll(newMediaList)

                }
                return Resource.Success(mediaListResponse ?: resultResponse)
            }
        }
        Log.e("handleGetMediaSearch", response.errorBody()!!.string())
        return Resource.Error(response.errorBody()!!.string())
    }
}