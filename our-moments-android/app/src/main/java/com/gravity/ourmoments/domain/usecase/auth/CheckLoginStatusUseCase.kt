package com.gravity.ourmoments.domain.usecase.auth

import com.gravity.ourmoments.domain.repository.AuthRepositoryInterface
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckLoginStatusUseCase @Inject constructor(
    private val authRepository: AuthRepositoryInterface
) {
    operator fun invoke(): Flow<Boolean> {
        return authRepository.isLoggedIn()
    }
}