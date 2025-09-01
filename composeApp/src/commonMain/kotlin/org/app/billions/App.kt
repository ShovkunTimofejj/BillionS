package org.app.billions

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.app.billions.ui.screens.Screen
import org.app.billions.ui.screens.challenges.ChallengeDetailScreen
import org.app.billions.ui.screens.challenges.ChallengesScreen
import org.app.billions.ui.screens.challenges.RewardsGalleryScreen
import org.app.billions.ui.screens.dashboard.DashboardScreen
import org.app.billions.ui.screens.journa.EntryDetailScreen
import org.app.billions.ui.screens.journa.JournalScreen
import org.app.billions.ui.screens.onboarding.OnboardingScreen
import org.app.billions.ui.screens.splash.SplashScreen
import org.app.billions.ui.screens.viewModel.JournalViewModel
import org.app.billions.ui.screens.viewModel.SplashScreenViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    val navController = rememberNavController()
    val splashScreenViewModel: SplashScreenViewModel = koinViewModel()
    val journalViewModel: JournalViewModel = koinViewModel()

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

        composable(Screen.ChallengesScreen.route) {
            ChallengesScreen(navController)
        }

        composable("challengeDetail") {
            ChallengeDetailScreen(navController)
        }

        composable("rewards") {
            RewardsGalleryScreen(navController)
        }

        composable(Screen.JournalScreen.route) {
            JournalScreen(navController)
        }

        composable("entryDetail") {
            EntryDetailScreen(navController, journalViewModel)
        }
    }
}
