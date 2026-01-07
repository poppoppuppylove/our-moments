package com.gravity.ourmoments.data.repository

import com.gravity.ourmoments.data.network.NetworkResult
import retrofit2.Response

abstract class BaseRepository {
    protected suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Error(
                    message = response.message(),
                    code = response.code()
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(
                message = e.message ?: "Unknown error occurred",
                code = null
            )
        }
    }
}