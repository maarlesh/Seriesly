package com.seriesly.core.network.di

import android.content.SharedPreferences
import com.seriesly.core.network.api.TvdbApiService
import com.seriesly.core.network.interceptor.TvdbAuthInterceptor
import com.seriesly.core.network.token.TvdbTokenStore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    @Provides @Singleton
    fun provideTokenStore(prefs: SharedPreferences) = TvdbTokenStore(prefs)

    @Provides @Singleton
    fun provideAuthInterceptor(
        tokenStore: TvdbTokenStore,
        api: Lazy<TvdbApiService>,
        @Named("tvdb_api_key") apiKey: String
    ) = TvdbAuthInterceptor(tokenStore, api, apiKey)

    @Provides @Singleton
    fun provideOkHttpClient(auth: TvdbAuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(auth)
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api4.thetvdb.com/v4/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides @Singleton
    fun provideApiService(retrofit: Retrofit): TvdbApiService =
        retrofit.create(TvdbApiService::class.java)
}
