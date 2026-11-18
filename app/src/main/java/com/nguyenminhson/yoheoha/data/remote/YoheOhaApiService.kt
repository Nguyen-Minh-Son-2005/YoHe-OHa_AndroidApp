package com.nguyenminhson.yoheoha.data.remote

import com.nguyenminhson.yoheoha.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface YoheOhaApiService {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("profile")
    suspend fun getProfile(): Response<ProfileResponse>

    @POST("profile")
    suspend fun upsertProfile(@Body request: ProfileRequest): Response<ProfileResponse>

    @GET("dashboard/today")
    suspend fun getTodayDashboard(): Response<DashboardResponse>

    @POST("dashboard/tasks/{id}/check")
    suspend fun checkTask(
        @Path("id") taskId: String,
        @Query("clientTime") clientTime: String? = null
    ): Response<CheckTaskResponse>

    @POST("dashboard/quantitative/{id}/increment")
    suspend fun incrementQuantitative(
        @Path("id") taskId: String,
        @Body body: IncrementQuantitativeRequest = IncrementQuantitativeRequest()
    ): Response<IncrementQuantitativeResponse>

    @POST("dashboard/mini-events/{id}/complete")
    suspend fun completeMiniEvent(
        @Path("id") eventId: String
    ): Response<CompleteMiniEventResponse>
}
