package com.nguyenminhson.yoheoha.ui.screens.dashboard.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nguyenminhson.yoheoha.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun FloatingVaultWidget(
    vaultPoints: Int,
    isVaultUnlocked: Boolean,
    justUnlockedVault: Boolean,
    onDismissUnlockDialog: () -> Unit
) {
    var showInfoDialog by remember { mutableStateOf(false) }

    // Offset for dragging
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // Pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "VaultPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "VaultScale"
    )

    // Floating Button
    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
            .scale(if (vaultPoints > 0) pulseScale else 1f)
            .clip(RoundedCornerShape(30.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (isVaultUnlocked) {
                        listOf(HealthyGreenPrimary, HealthyGreenAccent)
                    } else {
                        listOf(VaultRed, VaultGold)
                    }
                )
            )
            .clickable { showInfoDialog = true }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isVaultUnlocked) Icons.Default.LockOpen else Icons.Default.CardGiftcard,
                contentDescription = "Quỹ Lì xì",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isVaultUnlocked) "Đã nổ hũ (+$vaultPoints Đ)" else "Quỹ: +$vaultPoints Đ",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }

    // Info Dialog
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = null,
                    tint = VaultRed,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Quỹ Lì Xì & Rương Kho Báu",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column {
                    Text(
                        text = "• Điểm thưởng từ Mini Event hôm nay đang được tạm lưu trong Quỹ: $vaultPoints Điểm.",
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Cơ chế Nổ Hũ Công Bằng: Rương sẽ mở khóa và cộng dồn toàn bộ điểm vào kết quả ngày khi bạn hoàn thành Check-in nhiệm vụ Đi ngủ (Bedtime) đúng giờ tối nay!",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                    if (isVaultUnlocked) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "✓ Bạn đã hoàn thành nhiệm vụ Đi ngủ! Quỹ đã nổ hũ thành công.",
                            fontWeight = FontWeight.Bold,
                            color = HealthyGreenDark,
                            fontSize = 13.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showInfoDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = HealthyGreenPrimary)
                ) {
                    Text("Đã hiểu")
                }
            }
        )
    }

    // Celebration Nổ Hũ Dialog
    if (justUnlockedVault) {
        AlertDialog(
            onDismissRequest = onDismissUnlockDialog,
            icon = {
                Icon(
                    imageVector = Icons.Default.Celebration,
                    contentDescription = null,
                    tint = VaultGold,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "🎉 NỔ HŨ THÀNH CÔNG! 🎉",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = HealthyGreenDark,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "Chúc mừng bạn đã hoàn thành nhiệm vụ Đi ngủ đúng giờ! Chiếc Rương Kho Báu đã được mở khóa, giải phóng toàn bộ điểm thưởng Mini Event vào thành tích ngày của bạn. Chúc bạn một giấc ngủ thật ngon lành!",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = onDismissUnlockDialog,
                    colors = ButtonDefaults.buttonColors(containerColor = HealthyGreenPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tuyệt vời!")
                }
            }
        )
    }
}
