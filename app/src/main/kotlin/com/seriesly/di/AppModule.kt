package com.seriesly.di

import com.seriesly.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Named("tvdb_api_key")
    fun provideTvdbApiKey(): String = BuildConfig.TVDB_API_KEY
}
