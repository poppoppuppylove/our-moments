package com.gravity.ourmoments.data.repository

import com.gravity.ourmoments.data.model.Notification
import com.gravity.ourmoments.data.network.NetworkResult
import com.gravity.ourmoments.data.network.RetrofitClient
import javax.inject.Inject

class NotificationRepository @Inject constructor() : BaseRepository() {
    private val apiService = RetrofitClient.apiService

    suspend fun getNotifications(page: Int, size: Int): NetworkResult<List<Notification>> {
        return safeApiCall {
            apiService.getNotifications(page, size)
        }
    }

    suspend fun markNotificationAsRead(id: Long): NetworkResult<Unit> {
        return safeApiCall {
            apiService.markNotificationAsRead(id)
        }
    }

    suspend fun markAllNotificationsAsRead(): NetworkResult<Unit> {
        return safeApiCall {
            apiService.markAllNotificationsAsRead()
        }
    }

    suspend fun getUnreadNotificationCount(): NetworkResult<Int> {
        return safeApiCall {
            apiService.getUnreadNotificationCount()
        }
    }
}