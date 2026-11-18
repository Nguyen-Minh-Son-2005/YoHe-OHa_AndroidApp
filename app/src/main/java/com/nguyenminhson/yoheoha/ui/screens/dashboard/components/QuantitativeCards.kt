package com.nguyenminhson.yoheoha.ui.screens.dashboard.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nguyenminhson.yoheoha.data.model.QuantitativeTaskDto
import com.nguyenminhson.yoheoha.ui.theme.*

@Composable
fun QuantitativeCards(
    waterTask: QuantitativeTaskDto?,
    stepsTask: QuantitativeTaskDto?,
    onAddWater: (Int) -> Unit,
    onAddSteps: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = "Nhiệm vụ phụ theo lượng",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = HealthyGreenDark,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- WATER CARD ---
            val waterCurrent = waterTask?.currentValue ?: 0
            val waterTarget = waterTask?.targetValue ?: 2000
            val waterPercent = if (waterTarget > 0) ((waterCurrent.toFloat() / waterTarget) * 100).toInt() else 0
            val animatedWaterProgress by animateFloatAsState(
                targetValue = (waterCurrent.toFloat() / waterTarget).coerceIn(0f, 1f),
                label = "WaterProgress"
            )

            Card(
                modifier = Modifier
                    .weight(1f),
                colors = CardDefaults.cardColors(containerColor = WaterBlueLight),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = "Water",
                                tint = WaterBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Nước uống",
                                fontWeight = FontWeight.Bold,
                                color = WaterBlue,
                                fontSize = 13.sp
                            )
                        }
                        Text(
                            text = "${waterPercent.coerceAtMost(100)}%",
                            fontWeight = FontWeight.Bold,
                            color = WaterBlue,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "$waterCurrent / $waterTarget ml",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { animatedWaterProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = WaterBlue,
                        trackColor = Color.White
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { onAddWater(200) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = WaterBlue),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("+200ml", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // --- STEPS CARD ---
            val stepsCurrent = stepsTask?.currentValue ?: 0
            val stepsTarget = stepsTask?.targetValue ?: 6000
            val stepsPercent = if (stepsTarget > 0) ((stepsCurrent.toFloat() / stepsTarget) * 100).toInt() else 0
            val animatedStepsProgress by animateFloatAsState(
                targetValue = (stepsCurrent.toFloat() / stepsTarget).coerceIn(0f, 1f),
                label = "StepsProgress"
            )

            Card(
                modifier = Modifier
                    .weight(1f),
                colors = CardDefaults.cardColors(containerColor = StepsOrangeLight),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DirectionsWalk,
                                contentDescription = "Steps",
                                tint = StepsOrange,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Đi bộ",
                                fontWeight = FontWeight.Bold,
                                color = StepsOrange,
                                fontSize = 13.sp
                            )
                        }
                        Text(
                            text = "${stepsPercent.coerceAtMost(100)}%",
                            fontWeight = FontWeight.Bold,
                            color = StepsOrange,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "$stepsCurrent / $stepsTarget bước",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { animatedStepsProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = StepsOrange,
                        trackColor = Color.White
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { onAddSteps(500) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = StepsOrange),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("+500 bước", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
