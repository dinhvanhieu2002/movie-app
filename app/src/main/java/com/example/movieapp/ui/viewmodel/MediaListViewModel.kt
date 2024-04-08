package com.example.movieapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.api.models.MediaListResponse
import com.example.movieapp.api.models.Result
import com.example.movieapp.repositories.MediaRepository
import com.example.movieapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class MediaListViewModel(val mediaRepository: MediaRepository) : ViewModel() {
    var mediaHeroPager : MutableLiveData<List<Result>> = MutableLiveData()

    val mediaList: MutableLiveData<Resource<MediaListResponse>> = MutableLiveData()
    var mediaListResponse: MediaListResponse? = null
    var page = 1

    private val _currentCategory = MutableLiveData<String>()
    val currentCategory: LiveData<String> = _currentCategory

    fun setCurrentMediaType(mediaCategory: String) {
        _currentCategory.value = mediaCategory
    }

    init {
        setCurrentMediaType("popular")
    }

    fun getHeroPager(mediaType: String, mediaCategory: String) = viewModelScope.launch {
        val response = mediaRepository.getMediaList(mediaType, mediaCategory, 1)
        if(response.isSuccessful) {
            response.body()?.let {  resultResponse ->
                mediaHeroPager.postValue(resultResponse.results)
            }
        } else {
            Log.e("HomeViewModel", "Error call api")
        }
    }

    fun getMediaList(mediaType: String, mediaCategory: String) = viewModelScope.launch {
        mediaList.postValue(Resource.Loading())
        val response = mediaRepository.getMediaList(mediaType, mediaCategory, page)
        mediaList.postValue(handleGetMediaList(response))
    }

    private fun handleGetMediaList(response: Response<MediaListResponse>) : Resource<MediaListResponse> {
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
        Log.e("handleGetMediaList", response.errorBody()!!.string())
        return Resource.Error(response.errorBody()!!.string())
    }
}