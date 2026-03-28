package com.seriesly.feature.watchlist.di

import com.seriesly.core.domain.repository.WatchlistRepository
import com.seriesly.feature.watchlist.data.WatchlistRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WatchlistModule {

    @Binds
    @Singleton
    abstract fun bindWatchlistRepository(impl: WatchlistRepositoryImpl): WatchlistRepository
}
