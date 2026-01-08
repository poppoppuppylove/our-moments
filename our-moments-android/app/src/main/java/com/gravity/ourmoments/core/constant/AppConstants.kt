package com.gravity.ourmoments.core.constant

object AppConstants {
    const val BASE_URL = "http://10.0.2.2:8080/"
    const val WS_BASE_URL = "ws://10.0.2.2:8080"

    // DataStore
    const val PREFERENCES_NAME = "our_moments_preferences"
    const val KEY_AUTH_TOKEN = "auth_token"
    const val KEY_USER_ID = "user_id"
    const val KEY_USERNAME = "username"
    const val KEY_NICKNAME = "nickname"

    // Pagination
    const val DEFAULT_PAGE_SIZE = 20

    // Timeouts
    const val NETWORK_TIMEOUT = 30L
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L

    // Image
    const val MAX_IMAGE_SIZE = 1920
    const val IMAGE_QUALITY = 85
}