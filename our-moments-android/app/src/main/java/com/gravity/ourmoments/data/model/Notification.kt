package com.gravity.ourmoments.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val notificationId: Long,
    val content: String,
    val type: String,
    val relatedId: Long,
    val isRead: Boolean,
    val createTime: String
)