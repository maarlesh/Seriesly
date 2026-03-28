package com.seriesly.feature.progress.di

import com.seriesly.core.domain.repository.ProgressRepository
import com.seriesly.core.domain.repository.SeriesProgressRepository
import com.seriesly.feature.progress.data.ProgressRepositoryImpl
import com.seriesly.feature.progress.data.SeriesProgressRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProgressModule {

    @Binds
    @Singleton
    abstract fun bindProgressRepository(impl: ProgressRepositoryImpl): ProgressRepository

    @Binds
    @Singleton
    abstract fun bindSeriesProgressRepository(impl: SeriesProgressRepositoryImpl): SeriesProgressRepository
}
