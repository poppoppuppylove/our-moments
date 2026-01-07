package com.gravity.ourmoments.data.model

import kotlinx.serialization.Serializable

@Serializable
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