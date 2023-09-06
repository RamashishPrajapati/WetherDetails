package com.ram.weatherdetails.utils

sealed class NetworkState<out T> {
    data class Success<out T>(val data: T) : NetworkState<T>()
    data class Error<T>(val error: String) : NetworkState<T>()
    class Loading<T> : NetworkState<T>()
}

