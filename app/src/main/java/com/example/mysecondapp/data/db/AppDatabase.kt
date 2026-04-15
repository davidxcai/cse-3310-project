package com.example.mysecondapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mysecondapp.data.db.dao.CartDao
import com.example.mysecondapp.data.db.dao.ListingDao
import com.example.mysecondapp.data.db.dao.TransactionDao
import com.example.mysecondapp.data.db.dao.UserDao
import com.example.mysecondapp.data.db.entity.UserEntity
import com.example.mysecondapp.data.db.entity.ListingEntity
import com.example.mysecondapp.data.db.entity.CartEntity
import com.example.mysecondapp.data.db.entity.TransactionEntity

@Database(
    entities = [
        UserEntity::class,
        ListingEntity::class,
        CartEntity::class,
        TransactionEntity::class
    ],
    version = 6, // Incremented version to support localImagePath in ListingEntity
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun listingDao(): ListingDao
    abstract fun cartDao(): CartDao
    abstract fun transactionDao(): TransactionDao

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