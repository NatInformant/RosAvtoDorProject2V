package com.example.rosavtodorproject2.data.models

sealed class HttpResponseState<out T> {
    data class Success<out T>(val value: T) : HttpResponseState<T>()
    data class Failure(val message: String) : HttpResponseState<Nothing>()
    object Loading : HttpResponseState<Nothing>()
}
