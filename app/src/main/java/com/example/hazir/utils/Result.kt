package com.example.hazir.utils

sealed interface Result<out D> {
    data class Success<out D>(val data: D): Result<D>
    data class Error<out D>(val error: String): Result<D>
}

inline fun <T, E: Error, R> Result<T>.map(map: (T) -> R): Result<R> {
    return when(this) {
        is Result.Error -> Result.Error(error)
        is Result.Success -> Result.Success(map(data))
    }
}


inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    return when(this) {
        is Result.Error -> this
        is Result.Success -> {
            action(data)
            this
        }
    }
}


typealias EmptyResult<E> = Result<Unit>