package com.example.movieapp.utils

sealed class Resource<T>(
    val data: T? = null,
    val message : String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}

//sealed class Resource<T>(
//    val data: T? = null,
//    val error : ErrorResponse? = null
//) {
//    class Success<T>(data: T) : Resource<T>(data)
//    class Error<T>(error: ErrorResponse, data: T? = null) : Resource<T>(data, error)
//    class Loading<T> : Resource<T>()
//}