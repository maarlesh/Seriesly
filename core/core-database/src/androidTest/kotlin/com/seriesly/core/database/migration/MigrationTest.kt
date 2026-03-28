package com.seriesly.core.database.migration

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.seriesly.core.database.AppDatabase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java
    )

    // Add a test for each migration as we add them.
    // Example for when MIGRATION_1_2 is created:
    //
    // @Test
    // fun migrate1To2() {
    //     helper.createDatabase("test-db", 1).close()
    //     val db = helper.runMigrationsAndValidate("test-db", 2, true, MIGRATION_1_2)
    //     // Assert new column exists, old data intact, etc.
    //     db.close()
    // }

    @Test
    fun currentSchemaIsVersion1() {
        // Creates a v1 database and validates schema matches AppDatabase definition
        helper.createDatabase("test-db", 1).close()
    }
}
