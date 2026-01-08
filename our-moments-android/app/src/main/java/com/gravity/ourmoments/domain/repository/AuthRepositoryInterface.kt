package com.gravity.ourmoments.domain.repository

import com.gravity.ourmoments.core.result.Result
import com.gravity.ourmoments.domain.model.LoginRequest
import com.gravity.ourmoments.domain.model.LoginResponse
import com.gravity.ourmoments.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepositoryInterface {
    suspend fun login(request: LoginRequest): Result<LoginResponse>
    suspend fun register(request: LoginRequest): Result<User>
    suspend fun logout(): Result<Unit>
    fun getCurrentUser(): Flow<User?>
    fun isLoggedIn(): Flow<Boolean>
}