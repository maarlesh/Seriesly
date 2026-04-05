package com.seriesly.di

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.seriesly.BuildConfig
import com.seriesly.R
import com.seriesly.core.network.TvdbApiKeyProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideRemoteConfig(): FirebaseRemoteConfig {
        val config = FirebaseRemoteConfig.getInstance()
        val settings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(if (BuildConfig.DEBUG) 0L else 3600L)
            .build()
        config.setConfigSettingsAsync(settings)
        config.setDefaultsAsync(R.xml.remote_config_defaults)
        return config
    }

    @Provides @Singleton
    fun provideTvdbApiKeyProvider(remoteConfig: FirebaseRemoteConfig): TvdbApiKeyProvider =
        TvdbApiKeyProvider {
            remoteConfig.getString("tvdb_api_key").ifEmpty { BuildConfig.TVDB_API_KEY }
        }
}
