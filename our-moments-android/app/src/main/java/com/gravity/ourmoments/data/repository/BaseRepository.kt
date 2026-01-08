package com.gravity.ourmoments.data.repository

import com.gravity.ourmoments.data.model.ApiResponse
import com.gravity.ourmoments.data.network.NetworkResult
import retrofit2.Response

abstract class BaseRepository {
    protected suspend fun <T> safeApiCall(apiCall: suspend () -> Response<ApiResponse<T>>): NetworkResult<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse != null && apiResponse.code == ApiResponse.SUCCESS) {
                    NetworkResult.Success(apiResponse.data!!)
                } else {
                    NetworkResult.Error(
                        message = apiResponse?.message ?: "Request failed",
                        code = apiResponse?.code
                    )
                }
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