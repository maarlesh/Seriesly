package com.seriesly.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.seriesly.core.database.dao.*
import com.seriesly.core.database.entity.*
import com.seriesly.core.database.migration.ALL_MIGRATIONS

@Database(
    entities = [
        UserEntity::class,
        SearchCacheEntity::class,
        MovieEntity::class,
        SeriesEntity::class,
        SeasonEntity::class,
        EpisodeEntity::class,
        WatchlistEntity::class,
        WatchlistItemEntity::class,
        WatchProgressEntity::class,
        RatingEntity::class
    ],
    version = 4,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun searchCacheDao(): SearchCacheDao
    abstract fun movieDao(): MovieDao
    abstract fun seriesDao(): SeriesDao
    abstract fun watchlistDao(): WatchlistDao
    abstract fun watchProgressDao(): WatchProgressDao
    abstract fun ratingDao(): RatingDao
}
