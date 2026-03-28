package com.seriesly.feature.auth.di

import com.seriesly.feature.auth.data.AuthRepositoryImpl
import com.seriesly.feature.auth.domain.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
