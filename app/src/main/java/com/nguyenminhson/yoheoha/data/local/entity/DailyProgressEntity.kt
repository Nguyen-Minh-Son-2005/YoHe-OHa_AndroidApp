package com.nguyenminhson.yoheoha.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nguyenminhson.yoheoha.data.model.ProgressDto

@Entity(tableName = "daily_progress")
data class DailyProgressEntity(
    @PrimaryKey val date: String,
    val mainTaskPoints: Int,
    val quantitativePoints: Int,
    val eventVaultPoints: Int,
    val isVaultUnlocked: Boolean,
    val completionRate: Float,
    val totalPointsEarned: Int,
    val totalMaxPoints: Int
) {
    fun toDto(): ProgressDto = ProgressDto(
        mainTaskPoints = mainTaskPoints,
        quantitativePoints = quantitativePoints,
        eventVaultPoints = eventVaultPoints,
        isVaultUnlocked = isVaultUnlocked,
        completionRate = completionRate,
        totalPointsEarned = totalPointsEarned,
        totalMaxPoints = totalMaxPoints
    )

    companion object {
        fun fromDto(date: String, dto: ProgressDto): DailyProgressEntity = DailyProgressEntity(
            date = date,
            mainTaskPoints = dto.mainTaskPoints,
            quantitativePoints = dto.quantitativePoints,
            eventVaultPoints = dto.eventVaultPoints,
            isVaultUnlocked = dto.isVaultUnlocked,
            completionRate = dto.completionRate,
            totalPointsEarned = dto.totalPointsEarned,
            totalMaxPoints = dto.totalMaxPoints
        )
    }
}
