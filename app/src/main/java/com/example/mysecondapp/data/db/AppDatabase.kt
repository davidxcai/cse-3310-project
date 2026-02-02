package com.example.mysecondapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mysecondapp.data.db.dao.UserDao
import com.example.mysecondapp.data.db.entity.UserEntity

@Database(
    // These are the tables we have, can add more inside the array
    entities = [UserEntity::class],
    version = 3,
    exportSchema = false
)
// required for room to generate code behind the scenes
abstract class AppDatabase : RoomDatabase() {
    // we can request a userDao from this db
    abstract fun userDao(): UserDao

    // creates a single DB instance per app instance
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            )
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
        }
        }
    }

}