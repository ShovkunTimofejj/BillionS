package org.app.billions.ui.screens

sealed class Screen(val route: String) {
    data object SplashScreen : Screen("splash")
    data object OnboardingScreen : Screen("onboarding")
    data object ChooseFocusScreen : Screen("choose_focus")
    data object MainMenuScreen : Screen("main")
    data object AddNoteScreen : Screen("add_note")
    data object EditNoteScreen : Screen("edit_note")
    data object ReportScreen : Screen("report")
    data object HistoryScreen : Screen("history")
    data object SettingsScreen : Screen("settings")
    data object ChallengesScreen : Screen("challenges")
}
