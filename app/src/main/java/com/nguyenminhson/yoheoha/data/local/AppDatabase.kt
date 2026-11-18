package com.nguyenminhson.yoheoha.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nguyenminhson.yoheoha.data.local.dao.DailyProgressDao
import com.nguyenminhson.yoheoha.data.local.dao.QuantitativeDao
import com.nguyenminhson.yoheoha.data.local.dao.TaskDao
import com.nguyenminhson.yoheoha.data.local.entity.DailyProgressEntity
import com.nguyenminhson.yoheoha.data.local.entity.QuantitativeEntity
import com.nguyenminhson.yoheoha.data.local.entity.TaskEntity

@Database(
    entities = [
        TaskEntity::class,
        QuantitativeEntity::class,
        DailyProgressEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun quantitativeDao(): QuantitativeDao
    abstract fun dailyProgressDao(): DailyProgressDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "yohe_oha_local.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
