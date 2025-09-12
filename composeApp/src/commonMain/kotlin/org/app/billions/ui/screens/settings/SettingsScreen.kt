package org.app.billions.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import billions.composeapp.generated.resources.Res
import billions.composeapp.generated.resources.bg_dashboard_dark_lime
import billions.composeapp.generated.resources.bg_dashboard_graphite_gold
import billions.composeapp.generated.resources.bg_dashboard_neon_coral
import billions.composeapp.generated.resources.bg_dashboard_royal_blue
import billions.composeapp.generated.resources.theme_dark_lime
import billions.composeapp.generated.resources.theme_graphite_gold
import billions.composeapp.generated.resources.theme_graphite_gold_locked
import billions.composeapp.generated.resources.theme_neon_coral
import billions.composeapp.generated.resources.theme_neon_coral_locked
import billions.composeapp.generated.resources.theme_royal_blue
import billions.composeapp.generated.resources.theme_royal_blue_locked
import kotlinx.coroutines.launch
import org.app.billions.data.model.Subscription
import org.app.billions.data.model.Theme
import org.app.billions.data.model.Units
import org.app.billions.data.model.User
import org.app.billions.data.repository.ThemeRepository
import org.app.billions.data.repository.UserRepository
import org.app.billions.ui.screens.Screen
import org.app.billions.ui.screens.viewModel.JournalViewModel
import org.app.billions.ui.screens.viewModel.SplashScreenViewModel
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    splashScreenViewModel: SplashScreenViewModel,
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

    val uiState by splashScreenViewModel.uiState.collectAsState()
    val currentTheme = uiState.currentTheme

    val backgroundRes by remember(currentTheme) {
        derivedStateOf {
            when (currentTheme?.id) {
                "dark_lime" -> Res.drawable.bg_dashboard_dark_lime
                "neon_coral" -> Res.drawable.bg_dashboard_neon_coral
                "royal_blue" -> Res.drawable.bg_dashboard_royal_blue
                "graphite_gold" -> Res.drawable.bg_dashboard_graphite_gold
                else -> Res.drawable.bg_dashboard_dark_lime
            }
        }
    }

    val barColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0x801C2A3A)
        "neon_coral" -> Color(0x80431C2E)
        "royal_blue" -> Color(0x801D3B5C)
        "graphite_gold" -> Color(0x80383737)
        else -> Color(0x801C2A3A)
    }

    val cardColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFF1C2A3A)
        "neon_coral" -> Color(0xFF431C2E)
        "royal_blue" -> Color(0xFF1D3B5C)
        "graphite_gold" -> Color(0xFF383737)
        else -> Color(0xFF1C2A3A)
    }

    val contentColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFF00FF00)
        "neon_coral" -> Color(0xFFFF8FA0)
        "royal_blue" -> Color(0xFF00BFFF)
        "graphite_gold" -> Color(0xFFB59F00)
        else -> Color(0xFF00FF00)
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(backgroundRes),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Settings", color = contentColor) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = contentColor)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = barColor,
                        titleContentColor = contentColor,
                        navigationIconContentColor = contentColor
                    )
                )
            },
            bottomBar = {
                NavigationBar(containerColor = barColor) {
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
                            icon = { Icon(imageVector = icon, contentDescription = screen.route, tint = contentColor) },
                            label = {
                                Text(
                                    text = screen.route.replaceFirstChar { it.uppercase() },
                                    color = contentColor
                                )
                            }
                        )
                    }
                }
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                item {
                    SettingsSection("Appearance", contentColor) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            themes.forEach { theme ->
                                val imageRes = when (theme.id) {
                                    "dark_lime" -> Res.drawable.theme_dark_lime
                                    "neon_coral" -> if (theme.isPurchased) Res.drawable.theme_neon_coral else Res.drawable.theme_neon_coral_locked
                                    "royal_blue" -> if (theme.isPurchased) Res.drawable.theme_royal_blue else Res.drawable.theme_royal_blue_locked
                                    "graphite_gold" -> if (theme.isPurchased) Res.drawable.theme_graphite_gold else Res.drawable.theme_graphite_gold_locked
                                    else -> Res.drawable.theme_dark_lime
                                }

                                Image(
                                    painter = painterResource(imageRes),
                                    contentDescription = "Theme ${theme.id}",
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            if (theme.isPurchased) {
                                                scope.launch {
                                                    themeRepository.setCurrentTheme(theme.id)
                                                    splashScreenViewModel.updateTheme(theme.id)
                                                }
                                            } else {
                                                navController.navigate(Screen.InAppPurchaseScreen.route)
                                            }
                                        }
                                )
                            }
                        }
                    }
                }

                val sectionBackgroundColor = when (currentTheme?.id) {
                    "dark_lime" -> Color(0xFF0F1A2A)
                    "neon_coral" -> Color(0xFF331226)
                    "royal_blue" -> Color(0xFF0D223C)
                    "graphite_gold" -> Color(0xFF2C2A2A)
                    else -> Color(0xFF0F1A2A)
                }

                val sectionContentColor = when (currentTheme?.id) {
                    "dark_lime" -> Color(0xFF00FF00)
                    "neon_coral" -> Color(0xFFFF8FA0)
                    "royal_blue" -> Color(0xFF00BFFF)
                    "graphite_gold" -> Color(0xFFB59F00)
                    else -> Color(0xFF00FF00)
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(sectionBackgroundColor, RoundedCornerShape(12.dp))
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    ) {
                        Column {
                            Text(
                                text = "Daily Goals",
                                color = sectionContentColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Button(
                                onClick = { navController.navigate(Screen.DailyGoalsDetailScreen.route) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = sectionContentColor,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Set Goals")
                            }
                        }
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(sectionBackgroundColor, RoundedCornerShape(12.dp))
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    ) {
                        Column {
                            Text(
                                text = "Notifications",
                                color = sectionContentColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Button(
                                onClick = { navController.navigate("notificationsDetail") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = sectionContentColor,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                            ) {
                                Text("Daily Reminder")
                            }
                        }
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(sectionBackgroundColor, RoundedCornerShape(12.dp))
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    ) {
                        Column {
                            Text(
                                text = "Data",
                                color = sectionContentColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Button(
                                onClick = { journalViewModel.exportCsv() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = sectionContentColor,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                            ) {
                                Text("Export CSV")
                            }

                            Button(
                                onClick = { showResetDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = sectionContentColor,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Reset All Data")
                            }
                        }
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(sectionBackgroundColor, RoundedCornerShape(12.dp))
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    ) {
                        Column {
                            Text(
                                text = "About",
                                color = sectionContentColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Button(
                                onClick = { navController.navigate(Screen.AboutScreen.route) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = sectionContentColor,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Version 1.0 - Monocle Guy")
                            }
                        }
                    }
                }
            }

            if (showResetDialog) {
                AlertDialog(
                    onDismissRequest = { showResetDialog = false },
                    title = { Text("Confirm Reset", color = contentColor) },
                    text = { Text("Are you sure you want to reset all data? This action cannot be undone.", color = contentColor) },
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
                            Text("Cancel", color = contentColor)
                        }
                    },
                    containerColor = cardColor
                )
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    contentColor: Color,
    content: @Composable () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            color = contentColor
        )
        content()
    }
}
