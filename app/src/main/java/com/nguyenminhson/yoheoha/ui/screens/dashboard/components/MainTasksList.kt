package com.nguyenminhson.yoheoha.ui.screens.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nguyenminhson.yoheoha.data.model.MainTaskDto
import com.nguyenminhson.yoheoha.ui.theme.*

@Composable
fun MainTasksList(
    tasks: List<MainTaskDto>,
    onCheckTask: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Lịch trình sinh hoạt chính",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = HealthyGreenDark
            )
            Surface(
                color = HealthyGreenLight,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "x2 Điểm đúng giờ",
                    fontWeight = FontWeight.Bold,
                    color = HealthyGreenDark,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        if (tasks.isEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White
            ) {
                Text(
                    text = "Chưa có nhiệm vụ hôm nay. Hãy tải lại trang!",
                    modifier = Modifier.padding(16.dp),
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                tasks.forEach { task ->
                    MainTaskCard(task = task, onCheckTask = onCheckTask)
                }
            }
        }
    }
}

@Composable
fun MainTaskCard(
    task: MainTaskDto,
    onCheckTask: (String) -> Unit
) {
    val categoryIcon: ImageVector = when (task.category) {
        "WAKE" -> Icons.Default.WbSunny
        "BREAKFAST" -> Icons.Default.Restaurant
        "LUNCH" -> Icons.Default.LunchDining
        "EXERCISE" -> Icons.Default.FitnessCenter
        "DINNER" -> Icons.Default.DinnerDining
        "SLEEP" -> Icons.Default.Bedtime
        else -> Icons.Default.CheckCircle
    }

    val isCompleted = task.status == "COMPLETED"
    val isOngoing = task.status == "ONGOING"
    val isMissed = task.status == "MISSED"
    val isUpcoming = task.status == "UPCOMING"

    val cardBg = when {
        isCompleted -> Color(0xFFF1F8E9)
        isOngoing -> Color(0xFFFFFDE7)
        isMissed -> Color(0xFFFFEBEE)
        else -> Color.White
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon with Circle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = when {
                            isCompleted -> HealthyGreenLight
                            isOngoing -> VaultGoldLight
                            isMissed -> Color(0xFFFFCDD2)
                            else -> Color(0xFFECEFF1)
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = task.category,
                    tint = when {
                        isCompleted -> HealthyGreenPrimary
                        isOngoing -> Color(0xFFF57F17)
                        isMissed -> Color.Red
                        else -> TextSecondary
                    },
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Task Info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = task.taskName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${task.startTime} – ${task.endTime} (±${task.bufferMinutes}p)",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Status tag
                when {
                    isCompleted -> {
                        Text(
                            text = "✓ Đã hoàn thành",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HealthyGreenDark
                        )
                    }
                    isOngoing -> {
                        Text(
                            text = "● Đang diễn ra (x2 Điểm)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                    }
                    isMissed -> {
                        Text(
                            text = "✕ Đã bỏ lỡ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                    }
                    isUpcoming -> {
                        Text(
                            text = "○ Sắp diễn ra",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Check button
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = HealthyGreenPrimary,
                    modifier = Modifier.size(28.dp)
                )
            } else if (isOngoing) {
                Button(
                    onClick = { onCheckTask(task.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = HealthyGreenPrimary),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Check +${task.basePoints * 2}Đ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            } else if (isUpcoming) {
                OutlinedButton(
                    onClick = { onCheckTask(task.id) },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Check", fontSize = 11.sp, color = TextSecondary)
                }
            } else {
                Text(
                    text = "Hết hạn",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
