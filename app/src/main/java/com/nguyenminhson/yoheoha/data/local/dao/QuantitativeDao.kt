package com.nguyenminhson.yoheoha.data.local.dao

import androidx.room.*
import com.nguyenminhson.yoheoha.data.local.entity.QuantitativeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuantitativeDao {

    @Query("SELECT * FROM quantitative_tasks WHERE taskDate = :date")
    fun getQuantitativeTasksForDate(date: String): Flow<List<QuantitativeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateTasks(tasks: List<QuantitativeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateTask(task: QuantitativeEntity)

    @Query("SELECT * FROM quantitative_tasks WHERE isPendingSync = 1")
    suspend fun getPendingSyncTasks(): List<QuantitativeEntity>

    @Query("DELETE FROM quantitative_tasks WHERE taskDate = :date")
    suspend fun clearTasksForDate(date: String)
}
