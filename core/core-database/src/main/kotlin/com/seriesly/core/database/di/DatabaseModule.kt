package com.seriesly.core.database.di

import android.content.Context
import androidx.room.Room
import com.seriesly.core.database.AppDatabase
import com.seriesly.core.database.migration.ALL_MIGRATIONS
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "seriesly.db")
            .addMigrations(*ALL_MIGRATIONS)
            // NEVER use fallbackToDestructiveMigration() in production
            .build()

    @Provides fun provideUserDao(db: AppDatabase) = db.userDao()
    @Provides fun provideSearchCacheDao(db: AppDatabase) = db.searchCacheDao()
    @Provides fun provideMovieDao(db: AppDatabase) = db.movieDao()
    @Provides fun provideSeriesDao(db: AppDatabase) = db.seriesDao()
    @Provides fun provideWatchlistDao(db: AppDatabase) = db.watchlistDao()
    @Provides fun provideWatchProgressDao(db: AppDatabase) = db.watchProgressDao()
    @Provides fun provideRatingDao(db: AppDatabase) = db.ratingDao()
}
