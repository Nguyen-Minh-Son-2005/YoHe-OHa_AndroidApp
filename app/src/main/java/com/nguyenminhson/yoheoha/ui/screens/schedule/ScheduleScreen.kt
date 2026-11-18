package com.nguyenminhson.yoheoha.ui.screens.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nguyenminhson.yoheoha.ui.theme.*

@Composable
fun ScheduleScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "Lịch tuần & Kỷ luật",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = HealthyGreenDark
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Theo dõi chuỗi thói quen và tỷ lệ hoàn thành trong tuần",
            fontSize = 13.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Days of week row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("T2" to true, "T3" to true, "T4" to true, "T5" to false, "T6" to false, "T7" to false, "CN" to false).forEach { (day, isDone) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(day, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = RoundedCornerShape(18.dp),
                        color = if (isDone) HealthyGreenLight else Color.White,
                        border = if (isDone) null else androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (isDone) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = HealthyGreenPrimary, modifier = Modifier.size(20.dp))
                            } else {
                                Text("•", color = TextSecondary, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = HealthyGreenPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tổng kết tuần này", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = HealthyGreenDark)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("• Chuỗi ngày hoàn thành (Streak): 3 ngày liên tiếp 🔥", fontSize = 14.sp, color = TextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Text("• Tỷ lệ check-in đúng giờ: 85%", fontSize = 14.sp, color = TextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Text("• Lượng nước trung bình: 2100 ml/ngày", fontSize = 14.sp, color = TextPrimary)
            }
        }
    }
}
