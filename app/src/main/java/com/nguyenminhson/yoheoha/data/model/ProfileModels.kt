package com.nguyenminhson.yoheoha.data.model

import com.google.gson.annotations.SerializedName

data class ProfileRequest(
    @SerializedName("age") val age: Int,
    @SerializedName("birthDate") val birthDate: String? = null, // YYYY-MM-DD
    @SerializedName("gender") val gender: String, // MALE, FEMALE, OTHER
    @SerializedName("heightCm") val heightCm: Float,
    @SerializedName("weightKg") val weightKg: Float,
    @SerializedName("activityLevel") val activityLevel: String, // SEDENTARY, LIGHT, MODERATE, VERY_ACTIVE
    @SerializedName("wakeTime") val wakeTime: String, // HH:mm
    @SerializedName("sleepTime") val sleepTime: String, // HH:mm
    @SerializedName("workStartTime") val workStartTime: String? = null,
    @SerializedName("workEndTime") val workEndTime: String? = null
)

data class ProfileDto(
    @SerializedName("id") val id: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("age") val age: Int,
    @SerializedName("birthDate") val birthDate: String?,
    @SerializedName("gender") val gender: String,
    @SerializedName("heightCm") val heightCm: Float,
    @SerializedName("weightKg") val weightKg: Float,
    @SerializedName("activityLevel") val activityLevel: String,
    @SerializedName("wakeTime") val wakeTime: String,
    @SerializedName("sleepTime") val sleepTime: String,
    @SerializedName("workStartTime") val workStartTime: String?,
    @SerializedName("workEndTime") val workEndTime: String?,
    @SerializedName("bmi") val bmi: Float,
    @SerializedName("bmiCategory") val bmiCategory: String?,
    @SerializedName("bmr") val bmr: Float,
    @SerializedName("tdee") val tdee: Float,
    @SerializedName("targetCalories") val targetCalories: Int?,
    @SerializedName("targetWaterMl") val waterTargetMl: Int,
    @SerializedName("targetSteps") val stepTarget: Int
)

data class ProfileResponse(
    @SerializedName("profile") val profile: ProfileDto?,
    @SerializedName("user") val user: UserDto? = null
)
