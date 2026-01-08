package com.gravity.ourmoments.core.di

import com.gravity.ourmoments.data.repository.AuthRepository
import com.gravity.ourmoments.data.repository.NotificationRepository
import com.gravity.ourmoments.data.repository.PostRepository
import com.gravity.ourmoments.data.repository.PostRepositoryImpl
import com.gravity.ourmoments.data.repository.UserRepository
import com.gravity.ourmoments.domain.repository.AuthRepositoryInterface
import com.gravity.ourmoments.domain.repository.NotificationRepositoryInterface
import com.gravity.ourmoments.domain.repository.PostRepositoryInterface
import com.gravity.ourmoments.domain.repository.UserRepositoryInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepository: AuthRepository): AuthRepositoryInterface

    @Binds
    @Singleton
    abstract fun bindUserRepository(userRepository: UserRepository): UserRepositoryInterface

    @Binds
    @Singleton
    abstract fun bindPostRepository(postRepository: PostRepositoryImpl): PostRepositoryInterface

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(notificationRepository: NotificationRepository): NotificationRepositoryInterface
}