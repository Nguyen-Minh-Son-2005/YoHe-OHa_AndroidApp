package com.nguyenminhson.yoheoha.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nguyenminhson.yoheoha.data.model.MainTaskDto

@Entity(tableName = "daily_tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val taskName: String,
    val description: String?,
    val taskType: String,
    val category: String,
    val startTime: String,
    val endTime: String,
    val bufferMinutes: Int,
    val basePoints: Int,
    val status: String,
    val taskDate: String,
    val durationMinutes: Int?,
    val remainingSeconds: Int?,
    val isPendingSync: Boolean = false
) {
    fun toDto(): MainTaskDto = MainTaskDto(
        id = id,
        taskName = taskName,
        description = description,
        taskType = taskType,
        category = category,
        startTime = startTime,
        endTime = endTime,
        bufferMinutes = bufferMinutes,
        basePoints = basePoints,
        status = status,
        taskDate = taskDate,
        durationMinutes = durationMinutes,
        remainingSeconds = remainingSeconds
    )

    companion object {
        fun fromDto(dto: MainTaskDto, isPendingSync: Boolean = false): TaskEntity = TaskEntity(
            id = dto.id,
            taskName = dto.taskName,
            description = dto.description,
            taskType = dto.taskType,
            category = dto.category,
            startTime = dto.startTime,
            endTime = dto.endTime,
            bufferMinutes = dto.bufferMinutes,
            basePoints = dto.basePoints,
            status = dto.status,
            taskDate = dto.taskDate,
            durationMinutes = dto.durationMinutes,
            remainingSeconds = dto.remainingSeconds,
            isPendingSync = isPendingSync
        )
    }
}
