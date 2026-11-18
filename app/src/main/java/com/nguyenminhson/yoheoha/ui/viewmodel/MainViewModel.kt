package com.nguyenminhson.yoheoha.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nguyenminhson.yoheoha.data.model.*
import com.nguyenminhson.yoheoha.data.repository.YoheOhaRepository
import com.nguyenminhson.yoheoha.ui.util.ValidationUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

data class OnboardingUiState(
    val currentStep: Int = 1, // 1: Body, 2: Schedule, 3: Summary
    // Raw inputs - empty for new users, filled for re-editing
    val birthDateInput: String = "", // YYYY-MM-DD
    val ageInput: String = "",
    val gender: String = "MALE", // MALE, FEMALE, OTHER
    val heightInput: String = "",
    val weightInput: String = "",
    val activityLevel: String = "LIGHT", // SEDENTARY, LIGHT, MODERATE, VERY_ACTIVE
    val wakeTimeInput: String = "",
    val sleepTimeInput: String = "",
    val workStartInput: String = "",
    val workEndInput: String = "",
    // Field validation errors
    val birthDateError: String? = null,
    val heightError: String? = null,
    val weightError: String? = null,
    val wakeTimeError: String? = null,
    val sleepTimeError: String? = null,
    val workTimeError: String? = null,
    // Aliases for profile display
    val wakeTime: String = "",
    val sleepTime: String = "",
    val workStartTime: String = "",
    val workEndTime: String = "",
    // Parsed / Calculated metrics
    val age: Int = 22,
    val heightCm: Float = 170f,
    val weightKg: Float = 65f,
    val bmi: Float = 22.5f,
    val bmiCategory: String = "Bình thường (Cân đối)",
    val bmiCategoryKey: String = "NORMAL",
    val bmr: Float = 1600f,
    val tdee: Float = 2200f,
    val targetCalories: Int = 2200,
    val waterTargetMl: Int = 2000,
    val stepTarget: Int = 6000,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isCompleted: Boolean = false
)

data class DashboardUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val snackbarMessage: String? = null,
    val greeting: String = "Xin chào!",
    val messageContent: String = "Chúc bạn một ngày tràn đầy năng lượng và tích cực!",
    val progressRate: Float = 0f,
    val totalPointsEarned: Int = 0,
    val totalMaxPoints: Int = 0,
    val vaultPoints: Int = 0,
    val isVaultUnlocked: Boolean = false,
    val justUnlockedVault: Boolean = false,
    val mainTasks: List<MainTaskDto> = emptyList(),
    val waterTask: QuantitativeTaskDto? = null,
    val stepsTask: QuantitativeTaskDto? = null,
    val activeMiniEvent: MainTaskDto? = null,
    val miniEventRemainingSeconds: Int = 0
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repository = YoheOhaRepository(application)

    private val _authUiState = MutableStateFlow(AuthUiState())
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    private val _onboardingUiState = MutableStateFlow(OnboardingUiState())
    val onboardingUiState: StateFlow<OnboardingUiState> = _onboardingUiState.asStateFlow()

    private val _dashboardUiState = MutableStateFlow(DashboardUiState())
    val dashboardUiState: StateFlow<DashboardUiState> = _dashboardUiState.asStateFlow()

    private val _userNickname = MutableStateFlow(repository.sessionManager.getUserNickname() ?: "Bạn")
    val userNickname: StateFlow<String> = _userNickname.asStateFlow()

    private var miniEventTimerJob: Job? = null

    init {
        if (isLoggedIn()) {
            loadUserProfile()
            if (isOnboardingCompleted()) {
                loadDashboard()
            }
        }
    }

    fun isLoggedIn(): Boolean = repository.sessionManager.isLoggedIn()
    fun isOnboardingCompleted(): Boolean = repository.sessionManager.isOnboardingCompleted()

    // --- Authentication ---
    fun login(email: String, password: String, onNavigate: (String) -> Unit) {
        val emailErr = ValidationUtils.validateEmail(email)
        val passErr = ValidationUtils.validatePassword(password)
        if (emailErr != null || passErr != null) {
            _authUiState.update { it.copy(errorMessage = emailErr ?: passErr) }
            return
        }

        viewModelScope.launch {
            _authUiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.login(LoginRequest(email.trim(), password.trim()))
            result.fold(
                onSuccess = { res ->
                    _authUiState.update { it.copy(isLoading = false, isSuccess = true) }
                    _userNickname.value = res.user.nickname
                    val profileResult = repository.getProfile()
                    if (profileResult.isSuccess && profileResult.getOrNull() != null) {
                        val profile = profileResult.getOrNull()!!
                        syncProfileToUiState(profile)
                        repository.sessionManager.setOnboardingCompleted(true)
                        loadDashboard()
                        onNavigate("main")
                    } else {
                        repository.sessionManager.setOnboardingCompleted(false)
                        onNavigate("onboarding")
                    }
                },
                onFailure = { err ->
                    _authUiState.update { it.copy(isLoading = false, errorMessage = err.message ?: "Đăng nhập thất bại") }
                }
            )
        }
    }

    fun register(nickname: String, email: String, password: String, confirmPassword: String, onNavigate: (String) -> Unit) {
        val nickErr = ValidationUtils.validateNickname(nickname)
        val emailErr = ValidationUtils.validateEmail(email)
        val passErr = ValidationUtils.validatePassword(password)
        val confirmErr = ValidationUtils.validateConfirmPassword(password, confirmPassword)

        val firstErr = nickErr ?: emailErr ?: passErr ?: confirmErr
        if (firstErr != null) {
            _authUiState.update { it.copy(errorMessage = firstErr) }
            return
        }

        viewModelScope.launch {
            _authUiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.register(RegisterRequest(email.trim(), password.trim(), nickname.trim()))
            result.fold(
                onSuccess = { res ->
                    _authUiState.update { it.copy(isLoading = false, isSuccess = true) }
                    _userNickname.value = res.user.nickname
                    repository.sessionManager.setOnboardingCompleted(false)
                    onNavigate("onboarding")
                },
                onFailure = { err ->
                    _authUiState.update { it.copy(isLoading = false, errorMessage = err.message ?: "Đăng ký thất bại") }
                }
            )
        }
    }

    fun logout(onNavigateToAuth: () -> Unit) {
        viewModelScope.launch {
            repository.logout()
            _dashboardUiState.value = DashboardUiState()
            _authUiState.value = AuthUiState()
            _onboardingUiState.value = OnboardingUiState()
            onNavigateToAuth()
        }
    }

    // --- Profile & Re-edit Syncing ---

    fun loadUserProfile() {
        viewModelScope.launch {
            val result = repository.getProfile()
            if (result.isSuccess && result.getOrNull() != null) {
                syncProfileToUiState(result.getOrNull()!!)
            }
        }
    }

    fun prepareEditProfile() {
        viewModelScope.launch {
            val result = repository.getProfile()
            if (result.isSuccess && result.getOrNull() != null) {
                syncProfileToUiState(result.getOrNull()!!)
            }
            _onboardingUiState.update { it.copy(currentStep = 1, errorMessage = null) }
        }
    }

    fun syncProfileToUiState(profile: ProfileDto) {
        val (categoryName, categoryKey) = ValidationUtils.getAsianBmiClassification(profile.bmi)
        // Translate stored bmiCategory key → Vietnamese display name
        val bmiCategoryDisplay = when (profile.bmiCategory) {
            "UNDERWEIGHT" -> "Thiếu cân (Gầy)"
            "NORMAL"      -> "Bình thường (Cân đối)"
            "OVERWEIGHT"  -> "Tiền béo phì (Thừa cân)"
            "OBESE_I"     -> "Béo phì Độ I"
            "OBESE_II"    -> "Béo phì Độ II"
            else          -> categoryName // fallback: recalculate from BMI
        }
        val bmiCategoryKeyResolved = when (profile.bmiCategory) {
            "UNDERWEIGHT", "NORMAL", "OVERWEIGHT", "OBESE_I", "OBESE_II" -> profile.bmiCategory
            else -> categoryKey
        }
        _onboardingUiState.update {
            it.copy(
                birthDateInput = profile.birthDate ?: "",
                ageInput = profile.age.toString(),
                age = profile.age,
                gender = profile.gender,
                heightInput = if (profile.heightCm > 0) profile.heightCm.toInt().toString() else "",
                heightCm = profile.heightCm,
                weightInput = if (profile.weightKg > 0) profile.weightKg.toInt().toString() else "",
                weightKg = profile.weightKg,
                activityLevel = profile.activityLevel,
                wakeTimeInput = profile.wakeTime,
                sleepTimeInput = profile.sleepTime,
                workStartInput = profile.workStartTime ?: "",
                workEndInput = profile.workEndTime ?: "",
                wakeTime = profile.wakeTime,
                sleepTime = profile.sleepTime,
                workStartTime = profile.workStartTime ?: "",
                workEndTime = profile.workEndTime ?: "",
                bmi = profile.bmi,
                bmiCategory = bmiCategoryDisplay,
                bmiCategoryKey = bmiCategoryKeyResolved,
                bmr = profile.bmr,
                tdee = profile.tdee,
                targetCalories = profile.targetCalories ?: profile.tdee.toInt(),
                waterTargetMl = profile.waterTargetMl,
                stepTarget = profile.stepTarget
            )
        }
    }

    // --- Onboarding Flow & Realtime Validation ---

    fun setBirthDateInput(birthDate: String) {
        val calculatedAge = ValidationUtils.calculateAge(birthDate)
        val err = ValidationUtils.validateBirthDate(birthDate)
        _onboardingUiState.update {
            it.copy(
                birthDateInput = birthDate,
                ageInput = calculatedAge?.toString() ?: "",
                birthDateError = err
            )
        }
        recalculateOnboardingMetrics()
    }

    fun setHeightInput(input: String) {
        val filtered = input.filter { it.isDigit() }
        val err = if (filtered.isNotEmpty()) ValidationUtils.validateHeight(filtered) else null
        _onboardingUiState.update { it.copy(heightInput = filtered, heightError = err) }
        recalculateOnboardingMetrics()
    }

    fun setWeightInput(input: String) {
        val filtered = input.filter { it.isDigit() }
        val err = if (filtered.isNotEmpty()) ValidationUtils.validateWeight(filtered) else null
        _onboardingUiState.update { it.copy(weightInput = filtered, weightError = err) }
        recalculateOnboardingMetrics()
    }

    fun setGender(gender: String) {
        _onboardingUiState.update { it.copy(gender = gender) }
        recalculateOnboardingMetrics()
    }

    fun setActivityLevel(level: String) {
        _onboardingUiState.update { it.copy(activityLevel = level) }
        recalculateOnboardingMetrics()
    }

    fun setWakeTimeInput(time: String) {
        val err = ValidationUtils.validateTime(time, "Giờ thức dậy")
        _onboardingUiState.update { it.copy(wakeTimeInput = time, wakeTimeError = err) }
    }

    fun setSleepTimeInput(time: String) {
        val err = ValidationUtils.validateTime(time, "Giờ đi ngủ")
        _onboardingUiState.update { it.copy(sleepTimeInput = time, sleepTimeError = err) }
    }

    fun setWorkStartInput(time: String) {
        val err = if (time.isNotBlank()) ValidationUtils.validateTime(time, "Giờ bắt đầu đi làm / đi học") else null
        _onboardingUiState.update { it.copy(workStartInput = time, workTimeError = err) }
    }

    fun setWorkEndInput(time: String) {
        val err = if (time.isNotBlank()) ValidationUtils.validateTime(time, "Giờ tan học / làm về đến nhà") else null
        _onboardingUiState.update { it.copy(workEndInput = time, workTimeError = err) }
    }

    fun validateAndGoToStep2(): Boolean {
        val s = _onboardingUiState.value
        val birthErr = ValidationUtils.validateBirthDate(s.birthDateInput)
        val heightErr = ValidationUtils.validateHeight(s.heightInput)
        val weightErr = ValidationUtils.validateWeight(s.weightInput)

        if (birthErr != null || heightErr != null || weightErr != null) {
            _onboardingUiState.update {
                it.copy(
                    birthDateError = birthErr,
                    heightError = heightErr,
                    weightError = weightErr,
                    errorMessage = birthErr ?: heightErr ?: weightErr
                )
            }
            return false
        }

        recalculateOnboardingMetrics()
        _onboardingUiState.update { it.copy(currentStep = 2, errorMessage = null) }
        return true
    }

    fun validateAndGoToStep3(): Boolean {
        val s = _onboardingUiState.value
        val scheduleErr = ValidationUtils.validateSchedule(s.wakeTimeInput, s.sleepTimeInput)
        val workStartErr = if (s.workStartInput.isNotBlank()) ValidationUtils.validateTime(s.workStartInput, "Giờ bắt đầu đi làm / đi học") else null
        val workEndErr = if (s.workEndInput.isNotBlank()) ValidationUtils.validateTime(s.workEndInput, "Giờ tan học / làm về đến nhà") else null

        val firstErr = scheduleErr ?: workStartErr ?: workEndErr
        if (firstErr != null) {
            _onboardingUiState.update {
                it.copy(
                    wakeTimeError = scheduleErr,
                    workTimeError = workStartErr ?: workEndErr,
                    errorMessage = firstErr
                )
            }
            return false
        }

        _onboardingUiState.update {
            it.copy(
                currentStep = 3,
                errorMessage = null,
                wakeTime = s.wakeTimeInput.trim(),
                sleepTime = s.sleepTimeInput.trim(),
                workStartTime = s.workStartInput.trim(),
                workEndTime = s.workEndInput.trim()
            )
        }
        return true
    }

    fun goToOnboardingStep(targetStep: Int) {
        val current = _onboardingUiState.value.currentStep
        if (targetStep < current) {
            _onboardingUiState.update { it.copy(currentStep = targetStep, errorMessage = null) }
        } else if (targetStep == 2 && current == 1) {
            validateAndGoToStep2()
        } else if (targetStep == 3 && current == 2) {
            validateAndGoToStep3()
        }
    }

    private fun recalculateOnboardingMetrics() {
        val s = _onboardingUiState.value
        val age = ValidationUtils.calculateAge(s.birthDateInput) ?: s.ageInput.toIntOrNull() ?: 22
        val heightCm = s.heightInput.toFloatOrNull() ?: 170f
        val weightKg = s.weightInput.toFloatOrNull() ?: 65f

        val heightM = heightCm / 100f
        val bmi = if (heightM > 0) weightKg / (heightM * heightM) else 22.5f

        // Asian BMI 5 levels classification (WHO WPRO & Bộ Y Tế VN)
        val (categoryName, categoryKey) = ValidationUtils.getAsianBmiClassification(bmi)

        // Mifflin-St Jeor formula
        val bmr = if (s.gender == "MALE") {
            (10f * weightKg) + (6.25f * heightCm) - (5f * age) + 5f
        } else {
            (10f * weightKg) + (6.25f * heightCm) - (5f * age) - 161f
        }

        val multiplier = when (s.activityLevel) {
            "SEDENTARY" -> 1.2f
            "LIGHT" -> 1.375f
            "MODERATE" -> 1.55f
            "VERY_ACTIVE" -> 1.725f
            else -> 1.2f
        }
        val tdee = bmr * multiplier

        // Target Calories tailored to BMI level
        val targetCalories = when (categoryKey) {
            "UNDERWEIGHT" -> Math.round(tdee + 350).toInt()
            "OVERWEIGHT" -> Math.round(tdee - 300).toInt()
            "OBESE_I" -> Math.round(tdee - 400).toInt()
            "OBESE_II" -> Math.round(tdee - 500).toInt()
            else -> Math.round(tdee).toInt() // NORMAL
        }

        // Water calculation (approx 35ml per kg, rounded to 100ml)
        val water = (Math.round((weightKg * 35f) / 100f) * 100).coerceIn(1500, 3500)

        val steps = when (s.activityLevel) {
            "SEDENTARY" -> 6000
            "LIGHT" -> 8000
            "MODERATE" -> 10000
            "VERY_ACTIVE" -> 12000
            else -> 8000
        }

        _onboardingUiState.update {
            it.copy(
                age = age,
                heightCm = heightCm,
                weightKg = weightKg,
                bmi = String.format(java.util.Locale.US, "%.1f", bmi).toFloat(),
                bmiCategory = categoryName,
                bmiCategoryKey = categoryKey,
                bmr = Math.round(bmr).toFloat(),
                tdee = Math.round(tdee).toFloat(),
                targetCalories = targetCalories,
                waterTargetMl = water,
                stepTarget = steps
            )
        }
    }

    fun submitOnboarding(onSuccess: () -> Unit) {
        val s = _onboardingUiState.value
        viewModelScope.launch {
            _onboardingUiState.update { it.copy(isLoading = true, errorMessage = null) }
            val req = ProfileRequest(
                age = s.age,
                birthDate = s.birthDateInput.ifBlank { null },
                gender = s.gender,
                heightCm = s.heightCm,
                weightKg = s.weightKg,
                activityLevel = s.activityLevel,
                wakeTime = s.wakeTimeInput.trim(),
                sleepTime = s.sleepTimeInput.trim(),
                workStartTime = s.workStartInput.trim().ifBlank { null },
                workEndTime = s.workEndInput.trim().ifBlank { null }
            )

            val result = repository.saveProfile(req)
            result.fold(
                onSuccess = { profile ->
                    _onboardingUiState.update { it.copy(isLoading = false, isCompleted = true) }
                    syncProfileToUiState(profile)
                    loadDashboard()
                    onSuccess()
                },
                onFailure = { err ->
                    _onboardingUiState.update { it.copy(isLoading = false, errorMessage = err.message ?: "Lỗi lưu hồ sơ") }
                }
            )
        }
    }

    // --- Dashboard Operations ---
    fun loadDashboard(isPullToRefresh: Boolean = false) {
        viewModelScope.launch {
            _dashboardUiState.update {
                if (isPullToRefresh) it.copy(isRefreshing = true) else it.copy(isLoading = true)
            }

            val result = repository.fetchTodayDashboard()
            result.fold(
                onSuccess = { res ->
                    val water = res.quantitativeTasks.find { it.taskType == "WATER" }
                    val steps = res.quantitativeTasks.find { it.taskType == "STEPS" }
                    val activeEvent = res.miniEvents.firstOrNull { it.status != "COMPLETED" && (it.remainingSeconds ?: 0) > 0 }

                    _dashboardUiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            greeting = res.message.greeting,
                            messageContent = res.message.content,
                            progressRate = res.progress.completionRate,
                            totalPointsEarned = res.progress.totalPointsEarned,
                            totalMaxPoints = res.progress.totalMaxPoints,
                            vaultPoints = res.progress.eventVaultPoints,
                            isVaultUnlocked = res.progress.isVaultUnlocked,
                            mainTasks = res.mainTasks,
                            waterTask = water,
                            stepsTask = steps,
                            activeMiniEvent = activeEvent,
                            miniEventRemainingSeconds = activeEvent?.remainingSeconds ?: 0
                        )
                    }

                    startMiniEventCountdown(activeEvent?.remainingSeconds ?: 0)
                },
                onFailure = { err ->
                    _dashboardUiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = err.message
                        )
                    }
                }
            )
        }
    }

    private fun startMiniEventCountdown(initialSeconds: Int) {
        miniEventTimerJob?.cancel()
        if (initialSeconds <= 0) return

        miniEventTimerJob = viewModelScope.launch {
            var remaining = initialSeconds
            while (remaining > 0) {
                delay(1000)
                remaining--
                _dashboardUiState.update { it.copy(miniEventRemainingSeconds = remaining) }
            }
            loadDashboard()
        }
    }

    fun checkTask(taskId: String) {
        viewModelScope.launch {
            val result = repository.checkMainTask(taskId)
            result.fold(
                onSuccess = { res ->
                    val bonusText = if (res.justUnlockedVault) {
                        " 💥 NỔ HŨ QUỸ ĐIỂM! Bạn nhận thêm +${res.vaultPointsReleased} điểm từ Lì xì/Rương!"
                    } else ""

                    _dashboardUiState.update {
                        it.copy(
                            snackbarMessage = "${res.message}$bonusText",
                            justUnlockedVault = res.justUnlockedVault
                        )
                    }
                    loadDashboard()
                },
                onFailure = { err ->
                    _dashboardUiState.update {
                        it.copy(snackbarMessage = err.message ?: "Không thể check-in lúc này")
                    }
                }
            )
        }
    }

    fun drinkWater(customAmount: Int = 200) {
        val waterTaskId = _dashboardUiState.value.waterTask?.id ?: return
        viewModelScope.launch {
            val result = repository.incrementQuantitative(waterTaskId, customAmount)
            result.fold(
                onSuccess = { res ->
                    _dashboardUiState.update {
                        it.copy(snackbarMessage = "+$customAmount ml nước! ${res.message}")
                    }
                    loadDashboard()
                },
                onFailure = { err ->
                    _dashboardUiState.update { it.copy(snackbarMessage = err.message ?: "Lỗi thêm nước") }
                }
            )
        }
    }

    fun addSteps(customAmount: Int = 500) {
        val stepsTaskId = _dashboardUiState.value.stepsTask?.id ?: return
        viewModelScope.launch {
            val result = repository.incrementQuantitative(stepsTaskId, customAmount)
            result.fold(
                onSuccess = { res ->
                    _dashboardUiState.update {
                        it.copy(snackbarMessage = "+$customAmount bước! ${res.message}")
                    }
                    loadDashboard()
                },
                onFailure = { err ->
                    _dashboardUiState.update { it.copy(snackbarMessage = err.message ?: "Lỗi cập nhật bước chân") }
                }
            )
        }
    }

    fun completeMiniEvent(eventId: String) {
        viewModelScope.launch {
            val result = repository.completeMiniEvent(eventId)
            result.fold(
                onSuccess = { res ->
                    _dashboardUiState.update {
                        it.copy(
                            snackbarMessage = "🎉 Hoàn thành Mini Event! Điểm (+${res.pointsAwarded}) đã được tích vào Quỹ Lì xì/Rương kho báu!",
                            activeMiniEvent = null,
                            miniEventRemainingSeconds = 0
                        )
                    }
                    loadDashboard()
                },
                onFailure = { err ->
                    _dashboardUiState.update { it.copy(snackbarMessage = err.message ?: "Lỗi hoàn thành mini event") }
                }
            )
        }
    }

    fun dismissVaultUnlockedDialog() {
        _dashboardUiState.update { it.copy(justUnlockedVault = false) }
    }

    fun clearSnackbar() {
        _dashboardUiState.update { it.copy(snackbarMessage = null) }
    }
}
