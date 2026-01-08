package com.gravity.ourmoments.domain.usecase.auth

import com.gravity.ourmoments.core.result.Result
import com.gravity.ourmoments.domain.model.LoginRequest
import com.gravity.ourmoments.domain.model.LoginResponse
import com.gravity.ourmoments.domain.repository.AuthRepositoryInterface
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepositoryInterface
) {
    suspend operator fun invoke(request: LoginRequest): Result<LoginResponse> {
        // 可以在这里添加额外的验证逻辑
        if (request.username.isBlank()) {
            return Result.error("用户名不能为空")
        }

        if (request.password.isBlank()) {
            return Result.error("密码不能为空")
        }

        return authRepository.login(request)
    }
}