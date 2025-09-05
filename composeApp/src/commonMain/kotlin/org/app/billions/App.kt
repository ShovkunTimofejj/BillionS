package org.app.billions

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.app.billions.notifications.NotificationsDetailScreen
import org.app.billions.ui.screens.Screen
import org.app.billions.ui.screens.challenges.ChallengeDetailScreen
import org.app.billions.ui.screens.challenges.ChallengesScreen
import org.app.billions.ui.screens.challenges.RewardsGalleryScreen
import org.app.billions.ui.screens.dashboard.ComparisonScreen
import org.app.billions.ui.screens.dashboard.DashboardScreen
import org.app.billions.ui.screens.inAppPurchase.BillingRepository
import org.app.billions.ui.screens.inAppPurchase.InAppPurchaseScreen
import org.app.billions.ui.screens.journa.EntryDetailScreen
import org.app.billions.ui.screens.journa.JournalScreen
import org.app.billions.ui.screens.onboarding.OnboardingScreen
import org.app.billions.ui.screens.settings.AboutScreen
import org.app.billions.ui.screens.settings.DailyGoalsScreen
import org.app.billions.ui.screens.settings.SettingsScreen
import org.app.billions.ui.screens.splash.SplashScreen
import org.app.billions.ui.screens.viewModel.ChallengesViewModel
import org.app.billions.ui.screens.viewModel.JournalViewModel
import org.app.billions.ui.screens.viewModel.SplashScreenViewModel
import org.koin.compose.getKoin
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(billingRepository: BillingRepository) {
    val navController = rememberNavController()
    val splashScreenViewModel: SplashScreenViewModel = koinViewModel()
    val journalViewModel: JournalViewModel = koinViewModel()
    val challengesViewModel: ChallengesViewModel = koinViewModel()

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
            DashboardScreen(journalViewModel, navController)
        }

        composable(Screen.ChallengesScreen.route) {
            ChallengesScreen(navController, challengesViewModel)
        }

        composable("challengeDetail") {
            ChallengeDetailScreen(navController, challengesViewModel)
        }

        composable("rewards") {
            RewardsGalleryScreen(navController, challengesViewModel)
        }

        composable(Screen.JournalScreen.route) {
            JournalScreen(navController, journalViewModel)
        }

        composable("entryDetail") {
            EntryDetailScreen(navController, journalViewModel)
        }

        composable(Screen.ComparisonScreen.route) {
            ComparisonScreen(
                navController = navController,
                viewModel = journalViewModel
            )
        }

        composable(Screen.SettingsScreen.route) {
            SettingsScreen(navController)
        }

        composable(Screen.InAppPurchaseScreen.route) {
            InAppPurchaseScreen(
                navController = navController,
                billingRepository = billingRepository
            )
        }

        composable(Screen.DailyGoalsDetailScreen.route) {
            DailyGoalsScreen(navController)
        }

        composable(Screen.AboutScreen.route) {
            AboutScreen(navController)
        }

        composable("notificationsDetail") {
            NotificationsDetailScreen(
                navController = navController,
                notificationsManager = getKoin().get()
            )
        }
    }
}