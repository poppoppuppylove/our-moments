package com.gravity.ourmoments.domain.model

data class Post(
    val postId: Long,
    val title: String,
    val content: String,
    val images: List<String> = emptyList(),
    val author: User,
    val createTime: String,
    val updateTime: String? = null,
    val visibility: String = "PUBLIC"
)
