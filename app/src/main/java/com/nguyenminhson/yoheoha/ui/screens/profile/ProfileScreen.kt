package com.nguyenminhson.yoheoha.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nguyenminhson.yoheoha.ui.screens.onboarding.MetricRow
import com.nguyenminhson.yoheoha.ui.theme.*
import com.nguyenminhson.yoheoha.ui.util.ValidationUtils
import com.nguyenminhson.yoheoha.ui.viewmodel.MainViewModel

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    onNavigateToEditProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val nickname by viewModel.userNickname.collectAsState()
    val onboardingState by viewModel.onboardingUiState.collectAsState()
    val email = viewModel.repository.sessionManager.getUserEmail() ?: "son@yoheoha.local"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar & Name Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = HealthyGreenLight,
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = HealthyGreenPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = nickname,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = email,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Metrics Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Thông tin thể trạng & Sức khỏe",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = HealthyGreenDark
                )
                Spacer(modifier = Modifier.height(12.dp))

                val birthDateFormatted = ValidationUtils.formatDisplayDate(onboardingState.birthDateInput)
                val birthAndAge = if (birthDateFormatted.isNotEmpty()) {
                    "$birthDateFormatted (${onboardingState.age} tuổi)"
                } else if (onboardingState.age > 0) {
                    "${onboardingState.age} tuổi"
                } else {
                    "Chưa thiết lập"
                }

                MetricRow("Ngày sinh & Tuổi", birthAndAge, TextPrimary)
                MetricRow("BMI (Chuẩn Châu Á)", "${onboardingState.bmi} • ${onboardingState.bmiCategory}", HealthyGreenPrimary)
                MetricRow("BMR (Cơ bản)", "${onboardingState.bmr.toInt()} kcal", Color(0xFFE65100))
                MetricRow("TDEE (Năng lượng tiêu hao)", "${onboardingState.tdee.toInt()} kcal", Color(0xFFD81B60))
                if (onboardingState.targetCalories > 0) {
                    MetricRow("Mục tiêu Calorie", "${onboardingState.targetCalories} kcal", Color(0xFF2E7D32))
                }
                MetricRow("Mục tiêu nước", "${onboardingState.waterTargetMl} ml", WaterBlue)
                MetricRow("Mục tiêu bước", "${onboardingState.stepTarget} bước", StepsOrange)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Schedule Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Thời gian biểu sinh học",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = HealthyGreenDark
                )
                Spacer(modifier = Modifier.height(12.dp))

                MetricRow("Giờ thức dậy", onboardingState.wakeTime, TextPrimary)
                MetricRow("Bắt đầu đi làm / đi học", onboardingState.workStartTime, TextPrimary)
                MetricRow("Tan học / làm về đến nhà", onboardingState.workEndTime, TextPrimary)
                MetricRow("Giờ đi ngủ", onboardingState.sleepTime, TextPrimary)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            onClick = {
                viewModel.prepareEditProfile()
                onNavigateToEditProfile()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Cập nhật lại thông số thể trạng")
        }
    }
}
