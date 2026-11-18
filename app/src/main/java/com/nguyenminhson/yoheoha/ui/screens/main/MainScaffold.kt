package com.nguyenminhson.yoheoha.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nguyenminhson.yoheoha.ui.screens.dashboard.DailyDashboardScreen
import com.nguyenminhson.yoheoha.ui.screens.profile.ProfileScreen
import com.nguyenminhson.yoheoha.ui.screens.schedule.ScheduleScreen
import com.nguyenminhson.yoheoha.ui.screens.settings.SettingsScreen
import com.nguyenminhson.yoheoha.ui.theme.*
import com.nguyenminhson.yoheoha.ui.viewmodel.MainViewModel

sealed class NavigationTab(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : NavigationTab("dashboard", "Nhiệm vụ", Icons.Default.CheckCircle)
    object Schedule : NavigationTab("schedule", "Lịch tuần", Icons.Default.DateRange)
    object Profile : NavigationTab("profile", "Cá nhân", Icons.Default.Person)
    object Settings : NavigationTab("settings", "Cài đặt", Icons.Default.Settings)
}

@Composable
fun MainScaffold(
    viewModel: MainViewModel,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToAuth: () -> Unit
) {
    var currentTab by remember { mutableStateOf<NavigationTab>(NavigationTab.Dashboard) }
    var isBottomBarVisible by remember { mutableStateOf(true) }

    val tabs = listOf(
        NavigationTab.Dashboard,
        NavigationTab.Schedule,
        NavigationTab.Profile,
        NavigationTab.Settings
    )

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = isBottomBarVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp,
                    modifier = Modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                ) {
                    tabs.forEach { tab ->
                        val isSelected = currentTab.route == tab.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = tab },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title,
                                    tint = if (isSelected) HealthyGreenPrimary else TextSecondary
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    color = if (isSelected) HealthyGreenPrimary else TextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = HealthyGreenLight
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                NavigationTab.Dashboard -> DailyDashboardScreen(viewModel = viewModel)
                NavigationTab.Schedule -> ScheduleScreen()
                NavigationTab.Profile -> ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToEditProfile = onNavigateToOnboarding
                )
                NavigationTab.Settings -> SettingsScreen(
                    viewModel = viewModel,
                    onLogout = onNavigateToAuth
                )
            }

            // Quick toggle button to collapse/expand Bottom Navigation Bar (cuộn ra thu vào tùy ý)
            SmallFloatingActionButton(
                onClick = { isBottomBarVisible = !isBottomBarVisible },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 12.dp, bottom = 12.dp),
                containerColor = Color.White.copy(alpha = 0.9f),
                contentColor = TextSecondary
            ) {
                Icon(
                    imageVector = if (isBottomBarVisible) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = "Thu gọn/Mở rộng điều hướng",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
