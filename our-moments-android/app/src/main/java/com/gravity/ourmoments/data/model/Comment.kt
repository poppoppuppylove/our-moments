package com.gravity.ourmoments.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val commentId: Long,
    val content: String,
    val post: Post,
    val author: User,
    val createTime: String,
    val updateTime: String? = null
)