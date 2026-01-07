package com.gravity.ourmoments.data.repository

import com.gravity.ourmoments.data.model.User
import com.gravity.ourmoments.data.network.NetworkResult
import com.gravity.ourmoments.data.network.RetrofitClient
import javax.inject.Inject

class UserRepository @Inject constructor() : BaseRepository() {
    private val apiService = RetrofitClient.apiService

    suspend fun getProfile(): NetworkResult<User> {
        return safeApiCall {
            apiService.getProfile()
        }
    }

    suspend fun updateProfile(user: User): NetworkResult<User> {
        return safeApiCall {
            apiService.updateProfile(user)
        }
    }
}