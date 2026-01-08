package com.gravity.ourmoments.domain.model

data class Comment(
    val commentId: Long,
    val postId: Long,
    val content: String,
    val author: User,
    val createTime: String,
    val updateTime: String? = null
)