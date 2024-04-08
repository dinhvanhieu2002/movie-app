package com.example.movieapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.movieapp.repositories.MediaRepository


@Suppress("UNCHECKED_CAST")
class MediaDetailViewModelFactory(val mediaRepository: MediaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MediaDetailViewModel(mediaRepository) as T
    }
}