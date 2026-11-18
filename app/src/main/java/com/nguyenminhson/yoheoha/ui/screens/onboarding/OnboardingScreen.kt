package com.nguyenminhson.yoheoha.ui.screens.onboarding

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nguyenminhson.yoheoha.ui.theme.*
import com.nguyenminhson.yoheoha.ui.util.ValidationUtils
import com.nguyenminhson.yoheoha.ui.viewmodel.MainViewModel
import com.nguyenminhson.yoheoha.ui.viewmodel.OnboardingUiState
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OnboardingScreen(
    viewModel: MainViewModel,
    onFinishOnboarding: () -> Unit
) {
    val state by viewModel.onboardingUiState.collectAsState()
    val scrollState = rememberScrollState()

    // Handle physical device back button: lùi về bước trước thay vì thoát app
    BackHandler(enabled = state.currentStep > 1) {
        viewModel.goToOnboardingStep(state.currentStep - 1)
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Thiết lập Hồ sơ Sức khỏe",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = HealthyGreenDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Interactive Clickable Step Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StepPill(
                        title = "1. Thể trạng",
                        stepNumber = 1,
                        isActive = state.currentStep >= 1,
                        isCurrent = state.currentStep == 1,
                        onClick = { viewModel.goToOnboardingStep(1) },
                        modifier = Modifier.weight(1f)
                    )
                    StepPill(
                        title = "2. Giờ giấc",
                        stepNumber = 2,
                        isActive = state.currentStep >= 2,
                        isCurrent = state.currentStep == 2,
                        onClick = { viewModel.goToOnboardingStep(2) },
                        modifier = Modifier.weight(1f)
                    )
                    StepPill(
                        title = "3. Chỉ số",
                        stepNumber = 3,
                        isActive = state.currentStep >= 3,
                        isCurrent = state.currentStep == 3,
                        onClick = { viewModel.goToOnboardingStep(3) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(ScreenBackground)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HERO GRAPHIC / LOGO SPACE (Hạ thấp khung nhập liệu, chừa chỗ thoáng) ---
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                shape = CircleShape,
                color = HealthyGreenLight,
                modifier = Modifier.size(68.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when (state.currentStep) {
                            1 -> Icons.Default.FitnessCenter
                            2 -> Icons.Default.AccessTime
                            else -> Icons.Default.Favorite
                        },
                        contentDescription = "Health Icon",
                        tint = HealthyGreenPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = when (state.currentStep) {
                    1 -> "Lắng nghe cơ thể bạn"
                    2 -> "Đồng hồ sinh học tự nhiên"
                    else -> "Hồ sơ thể trạng hoàn chỉnh"
                },
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = HealthyGreenDark
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = when (state.currentStep) {
                    1 -> "Cung cấp ngày sinh và số đo để tính toán lượng calo và nước chính xác"
                    2 -> "Xây dựng khung giờ nhắc nhở x2 điểm và nhịp sinh học lành mạnh"
                    else -> "Phân tích y khoa cá nhân hóa chuẩn nhân trắc học châu Á"
                },
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- INPUT CARD FOR CURRENT STEP ---
            when (state.currentStep) {
                1 -> Step1BodyMetrics(state = state, viewModel = viewModel)
                2 -> Step2Schedule(state = state, viewModel = viewModel)
                3 -> Step3Summary(state = state, viewModel = viewModel, onFinish = onFinishOnboarding)
            }

            state.errorMessage?.let { err ->
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = err,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun StepPill(
    title: String,
    stepNumber: Int,
    isActive: Boolean,
    isCurrent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(17.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(17.dp),
        color = when {
            isCurrent -> HealthyGreenPrimary
            isActive -> HealthyGreenLight
            else -> Color(0xFFEEEEEE)
        }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = title,
                color = when {
                    isCurrent -> Color.White
                    isActive -> HealthyGreenDark
                    else -> TextSecondary
                },
                fontSize = 11.sp,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun Step1BodyMetrics(
    state: OnboardingUiState,
    viewModel: MainViewModel
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Giới tính sinh học",
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("MALE" to "Nam", "FEMALE" to "Nữ", "OTHER" to "Khác").forEach { (key, label) ->
                    val isSelected = state.gender == key
                    OutlinedButton(
                        onClick = { viewModel.setGender(key) },
                        colors = if (isSelected) ButtonDefaults.outlinedButtonColors(containerColor = HealthyGreenLight) else ButtonDefaults.outlinedButtonColors(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) HealthyGreenDark else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // --- NGÀY THÁNG NĂM SINH (DatePicker) ---
            val displayBirthDate = ValidationUtils.formatDisplayDate(state.birthDateInput)
            val calculatedAge = ValidationUtils.calculateAge(state.birthDateInput)
            val birthDateLabel = if (calculatedAge != null) {
                "Ngày tháng năm sinh ($calculatedAge tuổi)"
            } else {
                "Ngày tháng năm sinh"
            }

            OutlinedTextField(
                value = displayBirthDate,
                onValueChange = {},
                readOnly = true,
                enabled = true,
                label = { Text(birthDateLabel) },
                placeholder = { Text("Chạm để chọn ngày sinh") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Cake,
                        contentDescription = "BirthDate",
                        tint = HealthyGreenPrimary
                    )
                },
                trailingIcon = {
                    IconButton(onClick = {
                        showDatePickerDialog(context, state.birthDateInput) { selectedDate ->
                            viewModel.setBirthDateInput(selectedDate)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Chọn ngày",
                            tint = HealthyGreenPrimary
                        )
                    }
                },
                isError = state.birthDateError != null,
                supportingText = {
                    if (state.birthDateError != null) {
                        Text(state.birthDateError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showDatePickerDialog(context, state.birthDateInput) { selectedDate ->
                            viewModel.setBirthDateInput(selectedDate)
                        }
                    }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // --- CHIỀU CAO ---
            OutlinedTextField(
                value = state.heightInput,
                onValueChange = { viewModel.setHeightInput(it) },
                label = { Text("Chiều cao (cm)") },
                placeholder = { Text("Ví dụ: 170") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Height,
                        contentDescription = null,
                        tint = HealthyGreenPrimary
                    )
                },
                isError = state.heightError != null,
                supportingText = {
                    if (state.heightError != null) {
                        Text(state.heightError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(6.dp))

            // --- CÂN NẶNG ---
            OutlinedTextField(
                value = state.weightInput,
                onValueChange = { viewModel.setWeightInput(it) },
                label = { Text("Cân nặng (kg)") },
                placeholder = { Text("Ví dụ: 65") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.MonitorWeight,
                        contentDescription = null,
                        tint = HealthyGreenPrimary
                    )
                },
                isError = state.weightError != null,
                supportingText = {
                    if (state.weightError != null) {
                        Text(state.weightError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Mức độ vận động hàng ngày",
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            listOf(
                "SEDENTARY" to "Ít vận động (Dân văn phòng, ngồi nhiều)",
                "LIGHT" to "Vận động nhẹ (Đi dạo nhẹ, 1-3 ngày/tuần)",
                "MODERATE" to "Vận động vừa (Tập luyện đều đặn 3-5 ngày)",
                "VERY_ACTIVE" to "Vận động nhiều (Cường độ cao hàng ngày)"
            ).forEach { (key, label) ->
                val isSelected = state.activityLevel == key
                Surface(
                    onClick = { viewModel.setActivityLevel(key) },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) HealthyGreenLight else Color(0xFFF9F9F9),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    Text(
                        text = label,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 12.sp,
                        color = if (isSelected) HealthyGreenDark else TextPrimary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { viewModel.validateAndGoToStep2() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = HealthyGreenPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Tiếp tục (Lịch trình giờ giấc) ➔", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun Step2Schedule(
    state: OnboardingUiState,
    viewModel: MainViewModel
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Khung giờ sinh hoạt & Làm việc",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = HealthyGreenDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Chọn mốc giờ bằng đồng hồ trực quan để xây dựng lịch nhắc nhở đúng nhịp sinh học tự nhiên.",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // --- GIỜ THỨC DẬY (TimePicker) ---
            OutlinedTextField(
                value = state.wakeTimeInput,
                onValueChange = {},
                readOnly = true,
                label = { Text("Giờ thức dậy (Nên trước 07:30)") },
                placeholder = { Text("Chạm để chọn giờ thức dậy") },
                leadingIcon = {
                    Icon(Icons.Default.WbSunny, contentDescription = null, tint = VaultGold)
                },
                trailingIcon = {
                    IconButton(onClick = {
                        showTimePickerDialog(context, state.wakeTimeInput.ifBlank { "06:30" }) { selectedTime ->
                            viewModel.setWakeTimeInput(selectedTime)
                        }
                    }) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = HealthyGreenPrimary)
                    }
                },
                isError = state.wakeTimeError != null,
                supportingText = {
                    if (state.wakeTimeError != null) {
                        Text(state.wakeTimeError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showTimePickerDialog(context, state.wakeTimeInput.ifBlank { "06:30" }) { selectedTime ->
                            viewModel.setWakeTimeInput(selectedTime)
                        }
                    }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // --- GIỜ ĐI NGỦ (TimePicker) ---
            OutlinedTextField(
                value = state.sleepTimeInput,
                onValueChange = {},
                readOnly = true,
                label = { Text("Giờ đi ngủ (Nên trước 01:00)") },
                placeholder = { Text("Chạm để chọn giờ đi ngủ") },
                leadingIcon = {
                    Icon(Icons.Default.Bedtime, contentDescription = null, tint = Color(0xFF5C6BC0))
                },
                trailingIcon = {
                    IconButton(onClick = {
                        showTimePickerDialog(context, state.sleepTimeInput.ifBlank { "23:00" }) { selectedTime ->
                            viewModel.setSleepTimeInput(selectedTime)
                        }
                    }) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = HealthyGreenPrimary)
                    }
                },
                isError = state.sleepTimeError != null,
                supportingText = {
                    if (state.sleepTimeError != null) {
                        Text(state.sleepTimeError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showTimePickerDialog(context, state.sleepTimeInput.ifBlank { "23:00" }) { selectedTime ->
                            viewModel.setSleepTimeInput(selectedTime)
                        }
                    }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // --- GIỜ BẮT ĐẦU ĐI LÀM / ĐI HỌC (Đổi tên nhãn theo yêu cầu) ---
            OutlinedTextField(
                value = state.workStartInput,
                onValueChange = {},
                readOnly = true,
                label = { Text("Giờ bắt đầu đi làm / đi học") },
                placeholder = { Text("Chạm để chọn giờ đi học/làm") },
                leadingIcon = {
                    Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = HealthyGreenPrimary)
                },
                trailingIcon = {
                    IconButton(onClick = {
                        showTimePickerDialog(context, state.workStartInput.ifBlank { "08:00" }) { selectedTime ->
                            viewModel.setWorkStartInput(selectedTime)
                        }
                    }) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = HealthyGreenPrimary)
                    }
                },
                isError = state.workTimeError != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showTimePickerDialog(context, state.workStartInput.ifBlank { "08:00" }) { selectedTime ->
                            viewModel.setWorkStartInput(selectedTime)
                        }
                    }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // --- GIỜ TAN HỌC / LÀM VỀ ĐẾN NHÀ (Đổi tên nhãn theo yêu cầu) ---
            OutlinedTextField(
                value = state.workEndInput,
                onValueChange = {},
                readOnly = true,
                label = { Text("Giờ tan học / làm về đến nhà") },
                placeholder = { Text("Chạm để chọn giờ về đến nhà") },
                leadingIcon = {
                    Icon(Icons.Default.Home, contentDescription = null, tint = HealthyGreenPrimary)
                },
                trailingIcon = {
                    IconButton(onClick = {
                        showTimePickerDialog(context, state.workEndInput.ifBlank { "17:30" }) { selectedTime ->
                            viewModel.setWorkEndInput(selectedTime)
                        }
                    }) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = HealthyGreenPrimary)
                    }
                },
                isError = state.workTimeError != null,
                supportingText = {
                    if (state.workTimeError != null) {
                        Text(state.workTimeError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showTimePickerDialog(context, state.workEndInput.ifBlank { "17:30" }) { selectedTime ->
                            viewModel.setWorkEndInput(selectedTime)
                        }
                    }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Navigation Row: Quay lại & Tiếp tục
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { viewModel.goToOnboardingStep(1) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Quay lại")
                }

                Button(
                    onClick = { viewModel.validateAndGoToStep3() },
                    modifier = Modifier.weight(1.5f),
                    colors = ButtonDefaults.buttonColors(containerColor = HealthyGreenPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Xem kết quả ➔", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun Step3Summary(
    state: OnboardingUiState,
    viewModel: MainViewModel,
    onFinish: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Hồ sơ Thể trạng Hoàn chỉnh",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = HealthyGreenDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Chỉ số dựa trên nhân trắc học châu Á 5 mức và phương trình Mifflin-St Jeor:",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // BMI với màu sắc tương ứng chuẩn châu Á
            val bmiColor = when (state.bmiCategoryKey) {
                "UNDERWEIGHT" -> Color(0xFF0288D1)
                "NORMAL" -> HealthyGreenPrimary
                "OVERWEIGHT" -> Color(0xFFFFA000)
                "OBESE_I" -> Color(0xFFEF6C00)
                else -> Color(0xFFD32F2F)
            }

            MetricRow("Chỉ số BMI (Chuẩn Châu Á)", "${state.bmi} • ${state.bmiCategory}", bmiColor)
            MetricRow("BMR (Trao đổi chất cơ bản)", "${state.bmr.toInt()} kcal/ngày", Color(0xFFE65100))
            MetricRow("TDEE (Năng lượng tiêu hao)", "${state.tdee.toInt()} kcal/ngày", Color(0xFFD81B60))
            MetricRow("Mục tiêu Calo (${state.bmiCategory})", "${state.targetCalories} kcal/ngày", HealthyGreenDark)
            MetricRow("Lượng nước khuyến nghị", "${state.waterTargetMl} ml/ngày", WaterBlue)
            MetricRow("Mục tiêu bước chân", "${state.stepTarget} bước/ngày", StepsOrange)

            Spacer(modifier = Modifier.height(20.dp))

            // Dual Button Row: Cho phép lùi về sửa và xác nhận hoàn thành
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { viewModel.goToOnboardingStep(2) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Sửa giờ giấc", fontSize = 13.sp)
                }

                Button(
                    onClick = { viewModel.submitOnboarding(onSuccess = onFinish) },
                    modifier = Modifier.weight(1.6f),
                    enabled = !state.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = HealthyGreenPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Bắt đầu ngay 🎉", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun MetricRow(title: String, value: String, accentColor: Color) {
    Surface(
        color = ScreenBackground,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontSize = 13.sp, color = TextPrimary)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = accentColor)
        }
    }
}

// --- HELPER DIALOG FUNCTIONS ---

fun showDatePickerDialog(context: Context, initialDateStr: String, onDateSelected: (String) -> Unit) {
    val cal = Calendar.getInstance()
    if (initialDateStr.isNotBlank()) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val d = sdf.parse(initialDateStr)
            if (d != null) cal.time = d
        } catch (_: Exception) {}
    } else {
        cal.set(2004, Calendar.AUGUST, 15) // Thân thiện với độ tuổi người trẻ/sinh viên
    }

    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formatted = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth)
            onDateSelected(formatted)
        },
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    ).show()
}

fun showTimePickerDialog(context: Context, initialTimeStr: String, onTimeSelected: (String) -> Unit) {
    var initialHour = 6
    var initialMinute = 30
    if (initialTimeStr.contains(":")) {
        try {
            val parts = initialTimeStr.split(":")
            initialHour = parts[0].toInt()
            initialMinute = parts[1].toInt()
        } catch (_: Exception) {}
    }

    TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val formatted = String.format(Locale.US, "%02d:%02d", hourOfDay, minute)
            onTimeSelected(formatted)
        },
        initialHour,
        initialMinute,
        true // 24h format
    ).show()
}
