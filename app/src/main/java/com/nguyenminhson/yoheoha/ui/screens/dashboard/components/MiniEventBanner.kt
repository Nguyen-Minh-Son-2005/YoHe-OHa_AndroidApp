package com.nguyenminhson.yoheoha.ui.screens.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nguyenminhson.yoheoha.data.model.MainTaskDto
import com.nguyenminhson.yoheoha.ui.theme.*

@Composable
fun MiniEventBanner(
    event: MainTaskDto?,
    remainingSeconds: Int,
    onComplete: (String) -> Unit
) {
    if (event == null || remainingSeconds <= 0) return

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val formattedTime = String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = VaultGoldLight),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Celebration,
                        contentDescription = "Event",
                        tint = VaultGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MINI EVENT HÔM NAY",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100),
                        fontSize = 12.sp
                    )
                }

                // Countdown badge
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Timer",
                            tint = Color.Red,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formattedTime,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = event.taskName,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 15.sp
            )

            if (!event.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = event.description,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 17.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = HealthyGreenLight,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "+${event.basePoints} Đ (Vào Quỹ Lì xì)",
                        color = HealthyGreenDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Button(
                    onClick = { onComplete(event.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = HealthyGreenPrimary),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("Hoàn thành ngay", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
