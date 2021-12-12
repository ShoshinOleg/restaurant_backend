package com.shoshin.common

sealed class Reaction<out T> {
    data class Success<out T>(val data: T) : Reaction<T>()
    data class Error<out T>(val exception: Throwable) : Reaction<T>()
}