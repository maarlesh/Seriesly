package com.seriesly.feature.detail.di

import com.seriesly.core.domain.repository.ContentDetailRepository
import com.seriesly.feature.detail.data.DetailRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DetailModule {

    @Binds
    @Singleton
    abstract fun bindContentDetailRepository(impl: DetailRepositoryImpl): ContentDetailRepository
}
