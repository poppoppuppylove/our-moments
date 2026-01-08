package com.gravity.ourmoments.domain.model

data class User(
    val userId: Long,
    val username: String,
    val nickname: String? = null,
    val email: String? = null,
    val avatar: String? = null,
    val role: String = "USER",
    val createTime: String? = null
)