package org.app.billions

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.app.billions.ui.screens.Screen
import org.app.billions.ui.screens.dashboard.DashboardScreen
import org.app.billions.ui.screens.onboarding.OnboardingScreen

import org.app.billions.ui.screens.splash.SplashScreen
import org.app.billions.ui.screens.viewModel.SplashScreenViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    val navController = rememberNavController()
    val splashScreenViewModel: SplashScreenViewModel = koinViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen.route
    ) {
        composable(Screen.SplashScreen.route) {
            SplashScreen(navController, splashScreenViewModel)
        }

        composable(Screen.OnboardingScreen.route) {
            OnboardingScreen(navController)
        }

        composable(Screen.MainMenuScreen.route) {
            DashboardScreen(navController)
        }

//        composable(Screen.MainMenuScreen.route) {
//            MainMenuScreen(navController)
//        }
    }
}
