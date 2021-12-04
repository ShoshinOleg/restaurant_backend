package com.shoshin.common

sealed class Reaction<out T> {
    data class OnSuccess<out T>(val data: T) : Reaction<T>()
    data class OnError<out T>(val exception: Throwable) : Reaction<T>()
}