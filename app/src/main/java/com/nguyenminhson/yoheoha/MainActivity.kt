package com.nguyenminhson.yoheoha

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nguyenminhson.yoheoha.ui.navigation.Screen
import com.nguyenminhson.yoheoha.ui.screens.auth.AuthScreen
import com.nguyenminhson.yoheoha.ui.screens.main.MainScaffold
import com.nguyenminhson.yoheoha.ui.screens.onboarding.OnboardingScreen
import com.nguyenminhson.yoheoha.ui.theme.ScreenBackground
import com.nguyenminhson.yoheoha.ui.theme.YoheohaTheme
import com.nguyenminhson.yoheoha.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YoheohaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ScreenBackground
                ) {
                    YoheOhaApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun YoheOhaApp(viewModel: MainViewModel) {
    val navController = rememberNavController()

    val startDestination = when {
        !viewModel.isLoggedIn() -> Screen.Auth.route
        !viewModel.isOnboardingCompleted() -> Screen.Onboarding.route
        else -> Screen.Main.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Auth.route) {
            AuthScreen(
                viewModel = viewModel,
                onNavigateToMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                },
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                viewModel = viewModel,
                onFinishOnboarding = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainScaffold(
                viewModel = viewModel,
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route)
                },
                onNavigateToAuth = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
