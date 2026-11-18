package com.nguyenminhson.yoheoha.data.local.dao

import androidx.room.*
import com.nguyenminhson.yoheoha.data.local.entity.DailyProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyProgressDao {

    @Query("SELECT * FROM daily_progress WHERE date = :date")
    fun getProgressForDate(date: String): Flow<DailyProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: DailyProgressEntity)

    @Query("DELETE FROM daily_progress WHERE date = :date")
    suspend fun clearProgressForDate(date: String)
}
