package com.gravity.ourmoments.domain.usecase.auth

import com.gravity.ourmoments.core.result.Result
import com.gravity.ourmoments.domain.repository.AuthRepositoryInterface
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepositoryInterface
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logout()
    }
}