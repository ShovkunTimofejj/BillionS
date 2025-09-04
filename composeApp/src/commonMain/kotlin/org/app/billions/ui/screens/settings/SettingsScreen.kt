package org.app.billions.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch
import org.app.billions.data.model.Subscription
import org.app.billions.data.model.Theme
import org.app.billions.data.model.Units
import org.app.billions.data.model.User
import org.app.billions.data.repository.ThemeRepository
import org.app.billions.data.repository.UserRepository
import org.app.billions.ui.screens.Screen
import org.app.billions.ui.screens.viewModel.JournalViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    userRepository: UserRepository = koinInject(),
    themeRepository: ThemeRepository = koinInject(),
    journalViewModel: JournalViewModel = koinInject()
) {
    val scope = rememberCoroutineScope()
    var user by remember { mutableStateOf<User?>(null) }
    var themes by remember { mutableStateOf<List<Theme>>(emptyList()) }
    var showResetDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        user = userRepository.getUser()
        themes = themeRepository.getThemes()
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF001F3F))
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF001F3F)) {
                val navItems = listOf(
                    Screen.MainMenuScreen to Icons.Default.Home,
                    Screen.ChallengesScreen to Icons.Default.Flag,
                    Screen.JournalScreen to Icons.Default.List,
                    Screen.SettingsScreen to Icons.Default.Settings
                )

                navItems.forEach { (screen, icon) ->
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(imageVector = icon, contentDescription = screen.route) },
                        label = {
                            Text(
                                text = screen.route.replaceFirstChar { it.uppercase() },
                                color = Color.White
                            )
                        }
                    )
                }
            }
        },
        containerColor = Color(0xFF001F3F)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            item {
                SettingsSection("Appearance") {
                    ListItem(
                        headlineContent = { Text("Theme") },
                        supportingContent = { Text("1 free + 3 paid") },
                        modifier = Modifier.clickable {
                            navController.navigate(Screen.InAppPurchaseScreen.route)
                        }
                    )
                }
            }

            item {
                SettingsSection("Daily Goals") {
                    ListItem(
                        headlineContent = { Text("Set Goals") },
                        modifier = Modifier.clickable {
                            navController.navigate(Screen.DailyGoalsDetailScreen.route)
                        }
                    )
                }
            }

            item {
                SettingsSection("Notifications") {
                    ListItem(
                        headlineContent = { Text("Daily Reminder") },
                        modifier = Modifier.clickable {
                            navController.navigate("notificationsDetail")
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Challenge Reminder") },
                        modifier = Modifier.clickable {
                            navController.navigate("notificationsDetail")
                        }
                    )
                }
            }

            item {
                SettingsSection("Data") {
                    ListItem(
                        headlineContent = { Text("Export CSV") },
                        modifier = Modifier.clickable {
                            journalViewModel.exportCsv()
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Reset All Data") },
                        modifier = Modifier.clickable {
                            showResetDialog = true
                        }
                    )
                }
            }

            item {
                SettingsSection("About") {
                    ListItem(
                        headlineContent = { Text("Version 1.0") },
                        supportingContent = { Text("Monocle Guy") },
                        modifier = Modifier.clickable {
                            navController.navigate(Screen.AboutScreen.route)
                        }
                    )
                }
            }
        }

        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Confirm Reset") },
                text = { Text("Are you sure you want to reset all data? This action cannot be undone.") },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch {
                            userRepository.saveUser(
                                User(
                                    id = 0L,
                                    nickname = "User",
                                    avatar = "",
                                    units = Units.Metric,
                                    timezone = "UTC",
                                    subscription = Subscription.Free
                                )
                            )
                            journalViewModel.state.value.entries.forEach { entry ->
                                journalViewModel.deleteEntry(entry.id)
                            }
                        }
                        showResetDialog = false
                    }) {
                        Text("Reset", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            color = Color.White
        )
        content()
    }
}