package com.nguyenminhson.yoheoha.data.local.dao

import androidx.room.*
import com.nguyenminhson.yoheoha.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM daily_tasks WHERE taskDate = :date AND taskType != 'MINI_EVENT' ORDER BY startTime ASC")
    fun getTasksForDate(date: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM daily_tasks WHERE taskDate = :date AND taskType = 'MINI_EVENT'")
    fun getMiniEventsForDate(date: String): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateTasks(tasks: List<TaskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateTask(task: TaskEntity)

    @Query("UPDATE daily_tasks SET status = :status WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: String, status: String)

    @Query("SELECT * FROM daily_tasks WHERE isPendingSync = 1")
    suspend fun getPendingSyncTasks(): List<TaskEntity>

    @Query("DELETE FROM daily_tasks WHERE taskDate = :date")
    suspend fun clearTasksForDate(date: String)
}
