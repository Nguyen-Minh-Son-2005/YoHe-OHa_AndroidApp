package com.nguyenminhson.yoheoha.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nguyenminhson.yoheoha.data.model.QuantitativeTaskDto

@Entity(tableName = "quantitative_tasks")
data class QuantitativeEntity(
    @PrimaryKey val id: String,
    val taskType: String,
    val taskName: String,
    val currentValue: Int,
    val targetValue: Int,
    val unit: String,
    val stepIncrement: Int,
    val maxPoints: Int,
    val pointsEarned: Int,
    val percentage: Int,
    val taskDate: String,
    val isPendingSync: Boolean = false
) {
    fun toDto(): QuantitativeTaskDto = QuantitativeTaskDto(
        id = id,
        taskType = taskType,
        taskName = taskName,
        currentValue = currentValue,
        targetValue = targetValue,
        unit = unit,
        stepIncrement = stepIncrement,
        maxPoints = maxPoints,
        pointsEarned = pointsEarned,
        percentage = percentage,
        taskDate = taskDate
    )

    companion object {
        fun fromDto(dto: QuantitativeTaskDto, isPendingSync: Boolean = false): QuantitativeEntity = QuantitativeEntity(
            id = dto.id,
            taskType = dto.taskType,
            taskName = dto.taskName,
            currentValue = dto.currentValue,
            targetValue = dto.targetValue,
            unit = dto.unit,
            stepIncrement = dto.stepIncrement,
            maxPoints = dto.maxPoints,
            pointsEarned = dto.pointsEarned,
            percentage = dto.percentage,
            taskDate = dto.taskDate,
            isPendingSync = isPendingSync
        )
    }
}
