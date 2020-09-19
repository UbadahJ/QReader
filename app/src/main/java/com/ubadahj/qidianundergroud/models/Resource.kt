package com.ubadahj.qidianundergroud.models

sealed class Resource<T>(val data: T? = null, val throwable: Throwable? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T> : Resource<T>()
    class Error<T>(message: Throwable) : Resource<T>(throwable = message)
}