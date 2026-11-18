package com.nguyenminhson.yoheoha.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nguyenminhson.yoheoha.ui.screens.dashboard.components.*
import com.nguyenminhson.yoheoha.ui.theme.ScreenBackground
import com.nguyenminhson.yoheoha.ui.viewmodel.MainViewModel

@Composable
fun DailyDashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.dashboardUiState.collectAsState()
    val nickname by viewModel.userNickname.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    // Trigger snackbar on new message
    LaunchedEffect(state.snackbarMessage) {
        state.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Khối 1: Header (Contextual Greeting & Progress %)
            item {
                HeaderSection(
                    userNickname = nickname,
                    greeting = state.greeting,
                    messageContent = state.messageContent,
                    progressRate = state.progressRate,
                    totalPoints = state.totalPointsEarned,
                    maxPoints = state.totalMaxPoints,
                    isRefreshing = state.isRefreshing,
                    onRefresh = { viewModel.loadDashboard(isPullToRefresh = true) }
                )
            }

            // Khối 2: Mini Event Banner (with countdown)
            item {
                MiniEventBanner(
                    event = state.activeMiniEvent,
                    remainingSeconds = state.miniEventRemainingSeconds,
                    onComplete = { eventId -> viewModel.completeMiniEvent(eventId) }
                )
            }

            // Khối 3: Thẻ đôi Nhiệm vụ phụ theo lượng (Nước & Đi bộ)
            item {
                QuantitativeCards(
                    waterTask = state.waterTask,
                    stepsTask = state.stepsTask,
                    onAddWater = { amount -> viewModel.drinkWater(amount) },
                    onAddSteps = { amount -> viewModel.addSteps(amount) }
                )
            }

            // Khối 4: Danh sách Task chính
            item {
                MainTasksList(
                    tasks = state.mainTasks,
                    onCheckTask = { taskId -> viewModel.checkTask(taskId) }
                )
            }
        }

        // Floating Vault Widget (Quỹ Lì xì / Rương kho báu)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
        ) {
            FloatingVaultWidget(
                vaultPoints = state.vaultPoints,
                isVaultUnlocked = state.isVaultUnlocked,
                justUnlockedVault = state.justUnlockedVault,
                onDismissUnlockDialog = { viewModel.dismissVaultUnlockedDialog() }
            )
        }

        // Snackbar Host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )
    }
}
