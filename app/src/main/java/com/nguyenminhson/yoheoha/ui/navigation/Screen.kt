package com.nguyenminhson.yoheoha.ui.navigation

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Onboarding : Screen("onboarding")
    object Main : Screen("main")

    // Bottom Navigation items
    object Dashboard : Screen("dashboard")
    object Schedule : Screen("schedule")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}
