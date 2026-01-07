package com.gravity.ourmoments.di

import com.gravity.ourmoments.data.repository.AuthRepository
import com.gravity.ourmoments.data.repository.NotificationRepository
import com.gravity.ourmoments.data.repository.PostRepository
import com.gravity.ourmoments.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return AuthRepository()
    }

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository {
        return UserRepository()
    }

    @Provides
    @Singleton
    fun providePostRepository(): PostRepository {
        return PostRepository()
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(): NotificationRepository {
        return NotificationRepository()
    }
}