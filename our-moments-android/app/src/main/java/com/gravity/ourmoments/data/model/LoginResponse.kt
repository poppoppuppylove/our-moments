package com.gravity.ourmoments.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String,
    val user: User
)