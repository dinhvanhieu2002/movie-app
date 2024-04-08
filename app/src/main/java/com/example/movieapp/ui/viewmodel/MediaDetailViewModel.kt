package com.example.movieapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.api.models.MediaDetailResponse
import com.example.movieapp.repositories.MediaRepository
import com.example.movieapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class MediaDetailViewModel(val mediaRepository: MediaRepository) : ViewModel() {

    var mediaDetail : MutableLiveData<Resource<MediaDetailResponse>> = MutableLiveData()
    fun getMediaDetail(mediaType: String, mediaId: String) = viewModelScope.launch {
        mediaDetail.postValue(Resource.Loading())
        val response = mediaRepository.getMediaDetail(mediaType, mediaId)
        mediaDetail.postValue(handleGetMediaDetail(response))
    }

    fun handleGetMediaDetail(response: Response<MediaDetailResponse>) : Resource<MediaDetailResponse> {
        if(response.isSuccessful) {
            response.body()?.let {  resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        Log.e("handleGetMediaDetail", response.errorBody()!!.string())
        return Resource.Error(response.errorBody()!!.string())
    }
}