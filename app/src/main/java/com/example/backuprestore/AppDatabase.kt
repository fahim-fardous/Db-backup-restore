package com.example.backuprestore

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [Data::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dataDao(): Dao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "TaskHive.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
