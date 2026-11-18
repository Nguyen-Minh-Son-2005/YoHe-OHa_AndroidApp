package com.nguyenminhson.yoheoha.data.model

import com.google.gson.annotations.SerializedName

data class SystemMessageDto(
    @SerializedName("greeting") val greeting: String,
    @SerializedName("content") val content: String,
    @SerializedName("category") val category: String? = null,
    @SerializedName("timeSlot") val timeSlot: String? = null
)

data class ProgressDto(
    @SerializedName("mainTaskPoints") val mainTaskPoints: Int,
    @SerializedName("quantitativePoints") val quantitativePoints: Int,
    @SerializedName("eventVaultPoints") val eventVaultPoints: Int,
    @SerializedName("isVaultUnlocked") val isVaultUnlocked: Boolean,
    @SerializedName("completionRate") val completionRate: Float,
    @SerializedName("totalPointsEarned") val totalPointsEarned: Int,
    @SerializedName("totalMaxPoints") val totalMaxPoints: Int
)

data class MainTaskDto(
    @SerializedName("id") val id: String,
    @SerializedName("taskName") val taskName: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("taskType") val taskType: String, // MAIN, MINI_EVENT
    @SerializedName("category") val category: String, // WAKE, BREAKFAST, LUNCH, EXERCISE, DINNER, SLEEP
    @SerializedName("startTime") val startTime: String, // HH:mm
    @SerializedName("endTime") val endTime: String, // HH:mm
    @SerializedName("bufferMinutes") val bufferMinutes: Int,
    @SerializedName("basePoints") val basePoints: Int,
    @SerializedName("status") val status: String, // UPCOMING, ONGOING, COMPLETED, MISSED
    @SerializedName("taskDate") val taskDate: String,
    @SerializedName("durationMinutes") val durationMinutes: Int? = null,
    @SerializedName("remainingSeconds") val remainingSeconds: Int? = null
)

data class QuantitativeTaskDto(
    @SerializedName("id") val id: String,
    @SerializedName("taskType") val taskType: String, // WATER, STEPS
    @SerializedName("taskName") val taskName: String,
    @SerializedName("currentValue") val currentValue: Int,
    @SerializedName("targetValue") val targetValue: Int,
    @SerializedName("unit") val unit: String, // ml, bước
    @SerializedName("stepIncrement") val stepIncrement: Int,
    @SerializedName("maxPoints") val maxPoints: Int,
    @SerializedName("pointsEarned") val pointsEarned: Int,
    @SerializedName("percentage") val percentage: Int,
    @SerializedName("taskDate") val taskDate: String
)

data class DashboardResponse(
    @SerializedName("date") val date: String,
    @SerializedName("currentTime") val currentTime: String,
    @SerializedName("message") val message: SystemMessageDto,
    @SerializedName("progress") val progress: ProgressDto,
    @SerializedName("quantitativeTasks") val quantitativeTasks: List<QuantitativeTaskDto>,
    @SerializedName("mainTasks") val mainTasks: List<MainTaskDto>,
    @SerializedName("miniEvents") val miniEvents: List<MainTaskDto>
)

data class CheckTaskResponse(
    @SerializedName("message") val message: String,
    @SerializedName("pointsAwarded") val pointsAwarded: Int,
    @SerializedName("checkType") val checkType: String,
    @SerializedName("isVaultUnlocked") val isVaultUnlocked: Boolean,
    @SerializedName("justUnlockedVault") val justUnlockedVault: Boolean,
    @SerializedName("vaultPointsReleased") val vaultPointsReleased: Int,
    @SerializedName("task") val task: MainTaskDto
)

data class IncrementQuantitativeRequest(
    @SerializedName("amount") val amount: Int? = null
)

data class IncrementQuantitativeResponse(
    @SerializedName("message") val message: String,
    @SerializedName("increment") val increment: Int,
    @SerializedName("currentValue") val currentValue: Int,
    @SerializedName("targetValue") val targetValue: Int,
    @SerializedName("percentage") val percentage: Int,
    @SerializedName("pointsEarned") val pointsEarned: Int,
    @SerializedName("pointsAwarded") val pointsAwarded: Int,
    @SerializedName("task") val task: QuantitativeTaskDto
)

data class CompleteMiniEventResponse(
    @SerializedName("message") val message: String,
    @SerializedName("pointsAwarded") val pointsAwarded: Int,
    @SerializedName("vaultTotal") val vaultTotal: Int,
    @SerializedName("task") val task: MainTaskDto
)
