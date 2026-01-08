package com.gravity.ourmoments.data.repository

import com.gravity.ourmoments.core.result.Result
import com.gravity.ourmoments.data.local.datastore.UserDataStore
import com.gravity.ourmoments.data.model.LoginRequest
import com.gravity.ourmoments.data.model.LoginResponse
import com.gravity.ourmoments.data.model.User
import com.gravity.ourmoments.data.network.NetworkResult
import com.gravity.ourmoments.data.network.RetrofitClient
import com.gravity.ourmoments.domain.model.LoginRequest as DomainLoginRequest
import com.gravity.ourmoments.domain.model.LoginResponse as DomainLoginResponse
import com.gravity.ourmoments.domain.model.User as DomainUser
import com.gravity.ourmoments.domain.repository.AuthRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val userDataStore: UserDataStore
) : BaseRepository(), AuthRepositoryInterface {
    private val apiService = RetrofitClient.apiService

    override suspend fun login(request: DomainLoginRequest): Result<DomainLoginResponse> {
        return when (val result = safeApiCall {
            apiService.login(LoginRequest(request.username, request.password))
        }) {
            is NetworkResult.Success -> {
                val loginResponse = result.data
                // 保存用户信息到本地存储
                saveUserToDataStore(loginResponse)
                Result.success(
                    DomainLoginResponse(
                        token = loginResponse.token,
                        user = loginResponse.user.toDomainModel()
                    )
                )
            }
            is NetworkResult.Error -> {
                Result.error(result.message, result.code)
            }
            is NetworkResult.Loading -> {
                Result.loading()
            }
        }
    }

    override suspend fun register(request: DomainLoginRequest): Result<DomainUser> {
        // 注册功能将在后续实现
        return Result.error("注册功能尚未实现")
    }

    override suspend fun logout(): Result<Unit> {
        return when (val result = safeApiCall {
            apiService.logout()
        }) {
            is NetworkResult.Success -> {
                // 清除本地存储的用户信息
                clearUserDataFromDataStore()
                Result.success(Unit)
            }
            is NetworkResult.Error -> {
                Result.error(result.message, result.code)
            }
            is NetworkResult.Loading -> {
                Result.loading()
            }
        }
    }

    override fun getCurrentUser(): Flow<DomainUser?> {
        return userDataStore.userId.map { userId ->
            if (userId != null) {
                // 这里应该从本地数据库或API获取完整用户信息
                // 暂时返回一个简单的用户对象
                DomainUser(
                    userId = userId.toLongOrNull() ?: 0L,
                    username = "test_user"
                )
            } else {
                null
            }
        }
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return userDataStore.authToken.map { token ->
            !token.isNullOrEmpty()
        }
    }

    private suspend fun saveUserToDataStore(loginResponse: LoginResponse) {
        userDataStore.saveAuthToken(loginResponse.token)
        userDataStore.saveUserId(loginResponse.user.userId.toString())
        userDataStore.saveUsername(loginResponse.user.username)
        loginResponse.user.nickname?.let { userDataStore.saveNickname(it) }
    }

    private suspend fun clearUserDataFromDataStore() {
        userDataStore.clearAll()
    }

    // 扩展函数：将data model转换为domain model
    private fun User.toDomainModel(): DomainUser {
        return DomainUser(
            userId = this.userId,
            username = this.username,
            nickname = this.nickname,
            email = this.email,
            avatar = this.avatar,
            role = this.role,
            createTime = this.createTime
        )
    }
}