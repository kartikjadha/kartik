package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.dao.DailyHealthGoalDao
import com.example.data.local.entity.DailyHealthGoalEntity
import com.example.data.local.entity.HealthGoalLogEntity

/**
 * Local Room SQLite database for FitMarathi daily health goals (steps, water intake, calories)
 * and bilingual Marathi/English tracking logs.
 */
@Database(
    entities = [
        DailyHealthGoalEntity::class,
        HealthGoalLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(HealthGoalConverters::class)
abstract class FitMarathiDatabase : RoomDatabase() {

    abstract fun dailyHealthGoalDao(): DailyHealthGoalDao

    companion object {
        private const val DATABASE_NAME = "fitmarathi_health.db"

        @Volatile
        private var instance: FitMarathiDatabase? = null

        fun getInstance(context: Context): FitMarathiDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    FitMarathiDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { instance = it }
            }
        }

        /**
         * Creates an isolated in-memory database instance for JVM / Robolectric testing.
         */
        fun buildInMemory(context: Context): FitMarathiDatabase {
            return Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                FitMarathiDatabase::class.java
            )
                .allowMainThreadQueries()
                .build()
        }
    }
}
