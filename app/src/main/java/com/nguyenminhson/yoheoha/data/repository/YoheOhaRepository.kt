package com.nguyenminhson.yoheoha.data.repository

import android.content.Context
import com.nguyenminhson.yoheoha.data.local.AppDatabase
import com.nguyenminhson.yoheoha.data.local.entity.DailyProgressEntity
import com.nguyenminhson.yoheoha.data.local.entity.QuantitativeEntity
import com.nguyenminhson.yoheoha.data.local.entity.TaskEntity
import com.nguyenminhson.yoheoha.data.model.*
import com.nguyenminhson.yoheoha.data.remote.NetworkModule
import com.nguyenminhson.yoheoha.data.remote.YoheOhaApiService
import com.nguyenminhson.yoheoha.data.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class YoheOhaRepository(private val context: Context) {

    private val apiService: YoheOhaApiService = NetworkModule.getApiService(context)
    private val db: AppDatabase = AppDatabase.getDatabase(context)
    val sessionManager: SessionManager = SessionManager(context)

    private val taskDao = db.taskDao()
    private val quantDao = db.quantitativeDao()
    private val progressDao = db.dailyProgressDao()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getTodayString(): String = dateFormat.format(Date())

    // --- Local DB Flows (Offline-First) ---
    fun getLocalTasks(date: String = getTodayString()): Flow<List<TaskEntity>> =
        taskDao.getTasksForDate(date)

    fun getLocalMiniEvents(date: String = getTodayString()): Flow<List<TaskEntity>> =
        taskDao.getMiniEventsForDate(date)

    fun getLocalQuantitative(date: String = getTodayString()): Flow<List<QuantitativeEntity>> =
        quantDao.getQuantitativeTasksForDate(date)

    fun getLocalProgress(date: String = getTodayString()): Flow<DailyProgressEntity?> =
        progressDao.getProgressForDate(date)

    // --- Authentication ---
    suspend fun register(request: RegisterRequest): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.register(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                sessionManager.saveAuthToken(body.accessToken)
                sessionManager.saveUser(body.user.id, body.user.email, body.user.nickname)
                Result.success(body)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Đăng ký không thành công"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(request: LoginRequest): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.login(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                sessionManager.saveAuthToken(body.accessToken)
                sessionManager.saveUser(body.user.id, body.user.email, body.user.nickname)
                Result.success(body)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Đăng nhập thất bại"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        sessionManager.clearSession()
    }

    // --- Profile & Onboarding ---
    suspend fun getProfile(): Result<ProfileDto?> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getProfile()
            if (response.isSuccessful) {
                val profile = response.body()?.profile
                if (profile != null) {
                    sessionManager.setOnboardingCompleted(true)
                }
                Result.success(profile)
            } else {
                Result.failure(Exception("Không lấy được hồ sơ cá nhân"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveProfile(request: ProfileRequest): Result<ProfileDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.upsertProfile(request)
            if (response.isSuccessful && response.body()?.profile != null) {
                val profile = response.body()!!.profile!!
                sessionManager.setOnboardingCompleted(true)
                Result.success(profile)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Lưu hồ sơ thất bại"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Dashboard & Sync ---
    suspend fun fetchTodayDashboard(): Result<DashboardResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getTodayDashboard()
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                val today = data.date

                // Cache to Room DB
                val mainTaskEntities = data.mainTasks.map { TaskEntity.fromDto(it) }
                val miniEventEntities = data.miniEvents.map { TaskEntity.fromDto(it) }
                taskDao.insertOrUpdateTasks(mainTaskEntities + miniEventEntities)

                val quantEntities = data.quantitativeTasks.map { QuantitativeEntity.fromDto(it) }
                quantDao.insertOrUpdateTasks(quantEntities)

                val progressEntity = DailyProgressEntity.fromDto(today, data.progress)
                progressDao.insertOrUpdateProgress(progressEntity)

                Result.success(data)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Không tải được dữ liệu hôm nay"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkMainTask(taskId: String): Result<CheckTaskResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.checkTask(taskId)
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                taskDao.insertOrUpdateTask(TaskEntity.fromDto(data.task))
                // Refresh dashboard in background to ensure all points & vault stay synced
                fetchTodayDashboard()
                Result.success(data)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Check nhiệm vụ thất bại"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun incrementQuantitative(taskId: String, amount: Int? = null): Result<IncrementQuantitativeResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.incrementQuantitative(taskId, IncrementQuantitativeRequest(amount))
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                quantDao.insertOrUpdateTask(QuantitativeEntity.fromDto(data.task))
                fetchTodayDashboard()
                Result.success(data)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Cập nhật định lượng thất bại"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun completeMiniEvent(eventId: String): Result<CompleteMiniEventResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.completeMiniEvent(eventId)
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                taskDao.insertOrUpdateTask(TaskEntity.fromDto(data.task))
                fetchTodayDashboard()
                Result.success(data)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Hoàn thành mini event thất bại"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
