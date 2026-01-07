package com.gravity.ourmoments.data.repository

import com.gravity.ourmoments.data.model.LoginRequest
import com.gravity.ourmoments.data.network.NetworkResult
import com.gravity.ourmoments.data.network.RetrofitClient
import javax.inject.Inject

class AuthRepository @Inject constructor() : BaseRepository() {
    private val apiService = RetrofitClient.apiService

    suspend fun login(username: String, password: String): NetworkResult<com.gravity.ourmoments.data.model.LoginResponse> {
        return safeApiCall {
            apiService.login(LoginRequest(username, password))
        }
    }

    suspend fun logout(): NetworkResult<Unit> {
        return safeApiCall {
            apiService.logout()
        }
    }
}