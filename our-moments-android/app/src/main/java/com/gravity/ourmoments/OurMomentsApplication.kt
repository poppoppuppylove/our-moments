package com.gravity.ourmoments

import android.app.Application
import com.gravity.ourmoments.data.network.AuthInterceptor
import com.gravity.ourmoments.data.network.RetrofitClient
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class OurMomentsApplication : Application() {

    @Inject
    lateinit var authInterceptor: AuthInterceptor

    override fun onCreate() {
        super.onCreate()
        // 初始化RetrofitClient
        RetrofitClient.initialize(authInterceptor)
    }
}