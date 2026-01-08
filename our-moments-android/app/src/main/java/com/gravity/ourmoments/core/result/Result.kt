package com.gravity.ourmoments.core.result

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String, val code: Int? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()

    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun error(message: String, code: Int? = null): Result<Nothing> = Error(message, code)
        fun loading(): Result<Nothing> = Loading
    }
}