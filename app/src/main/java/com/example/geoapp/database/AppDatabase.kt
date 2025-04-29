package com.example.geoapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.geoapp.models.FavoritePoint

@Database(entities = [FavoritePoint::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritePointDao(): FavoritePointDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "geoapp_database"
                )
                    .setJournalMode(JournalMode.TRUNCATE)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
