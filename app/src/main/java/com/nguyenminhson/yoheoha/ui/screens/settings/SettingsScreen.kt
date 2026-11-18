package com.nguyenminhson.yoheoha.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nguyenminhson.yoheoha.ui.theme.*
import com.nguyenminhson.yoheoha.ui.viewmodel.MainViewModel

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var notificationsEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "Cài đặt & Tùy biến",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = HealthyGreenDark
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = HealthyGreenPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Thông báo nhắc nhở", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("Nhắc trước khi bắt đầu nhiệm vụ 15 phút", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = HealthyGreenPrimary)
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp), color = BorderSubtle)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Palette, contentDescription = null, tint = HealthyGreenPrimary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Chế độ giao diện", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Mặc định: Healthy Sống khỏe (Trung tính)", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = HealthyGreenLight)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "YoHe - OHa v1.0.0 (MVP)",
                    fontWeight = FontWeight.Bold,
                    color = HealthyGreenDark,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Hệ thống hỗ trợ cấu hình động từ Admin Portal (Kho thông điệp, Mini Events, Định lượng nước/bước). Kiến trúc mở rộng linh hoạt.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = { viewModel.logout(onLogout) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Đăng xuất tài khoản", fontWeight = FontWeight.Bold)
        }
    }
}
