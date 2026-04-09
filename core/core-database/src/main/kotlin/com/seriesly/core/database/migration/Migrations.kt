package com.seriesly.core.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Phase 1.2 — updatedAt for conflict resolution
        db.execSQL("ALTER TABLE watchlists ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE watchlist_items ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE watch_progress ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
        // RatingEntity already had updatedAt — no ALTER needed

        // Phase 1.3 — pendingSync + syncedAt for offline queue
        db.execSQL("ALTER TABLE watchlists ADD COLUMN pendingSync INTEGER NOT NULL DEFAULT 1")
        db.execSQL("ALTER TABLE watchlists ADD COLUMN syncedAt INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE watchlist_items ADD COLUMN pendingSync INTEGER NOT NULL DEFAULT 1")
        db.execSQL("ALTER TABLE watchlist_items ADD COLUMN syncedAt INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE ratings ADD COLUMN pendingSync INTEGER NOT NULL DEFAULT 1")
        db.execSQL("ALTER TABLE ratings ADD COLUMN syncedAt INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE watch_progress ADD COLUMN pendingSync INTEGER NOT NULL DEFAULT 1")
        db.execSQL("ALTER TABLE watch_progress ADD COLUMN syncedAt INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Phase 3 fix — store title/posterUrl on ratings so they survive reinstall
        db.execSQL("ALTER TABLE ratings ADD COLUMN title TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE ratings ADD COLUMN posterUrl TEXT")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Store display metadata on watchlist items so they survive reinstall (same fix as ratings)
        db.execSQL("ALTER TABLE watchlist_items ADD COLUMN title TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE watchlist_items ADD COLUMN posterUrl TEXT")
        db.execSQL("ALTER TABLE watchlist_items ADD COLUMN year INTEGER")
    }
}

val ALL_MIGRATIONS: Array<Migration> = arrayOf(
    MIGRATION_1_2,
    MIGRATION_2_3,
    MIGRATION_3_4
)
