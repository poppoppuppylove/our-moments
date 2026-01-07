package com.gravity.ourmoments

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class OurMomentsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}