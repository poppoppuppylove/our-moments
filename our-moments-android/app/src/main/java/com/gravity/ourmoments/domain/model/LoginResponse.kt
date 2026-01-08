package com.gravity.ourmoments.domain.model

data class LoginResponse(
    val token: String,
    val user: User
)