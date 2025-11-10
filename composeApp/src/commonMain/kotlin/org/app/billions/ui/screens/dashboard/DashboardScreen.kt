package org.app.billions.ui.screens.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import billions.composeapp.generated.resources.Res
import billions.composeapp.generated.resources.bg_dashboard_dark_lime
import billions.composeapp.generated.resources.bg_dashboard_graphite_gold
import billions.composeapp.generated.resources.bg_dashboard_neon_coral
import billions.composeapp.generated.resources.bg_dashboard_royal_blue
import billions.composeapp.generated.resources.bg_navbar_dark_lime
import billions.composeapp.generated.resources.bg_navbar_graphite_gold
import billions.composeapp.generated.resources.bg_navbar_neon_coral
import billions.composeapp.generated.resources.bg_navbar_royal_blue
import org.app.billions.ui.screens.Screen
import org.app.billions.ui.screens.journa.AddEditEntryDialog
import org.app.billions.ui.screens.journa.ConfettiOverlay
import org.app.billions.ui.screens.viewModel.JournalViewModel
import org.app.billions.ui.screens.viewModel.SplashScreenViewModel
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: JournalViewModel,
    navController: NavHostController,
    splashScreenViewModel: SplashScreenViewModel
) {
    val state by viewModel.state

    val uiState by splashScreenViewModel.uiState.collectAsState()
    val currentTheme = uiState.currentTheme

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
        "graphite_gold" -> Color(0xFFFFD700)
        else -> Color(0xFF00FF00)
    }

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

    var selectedTabIndex by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (currentTheme != null) {
            Image(
                painter = painterResource(backgroundRes),
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Dashboard", color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = barColor),
                    actions = {
                        IconButton(onClick = { viewModel.showAddEntryDialog() }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Entry",
                                tint = Color.White
                            )
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar(containerColor = barColor) {
                    NavigationBarItem(
                        selected = selectedTabIndex == 0,
                        onClick = {
                            selectedTabIndex = 0
                            navController.navigate(Screen.MainMenuScreen.route) {
                                popUpTo(Screen.MainMenuScreen.route) { inclusive = true }
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                        label = { Text("Home", color = Color.White) }
                    )
                    NavigationBarItem(
                        selected = selectedTabIndex == 1,
                        onClick = {
                            selectedTabIndex = 1
                            navController.navigate(Screen.ChallengesScreen.route) {
                                popUpTo(Screen.MainMenuScreen.route)
                            }
                        },
                        icon = { Icon(Icons.Default.Flag, contentDescription = "Challenges") },
                        label = { Text("Challenges", color = Color.White) }
                    )
                    NavigationBarItem(
                        selected = selectedTabIndex == 2,
                        onClick = {
                            selectedTabIndex = 2
                            navController.navigate(Screen.JournalScreen.route) {
                                popUpTo(Screen.MainMenuScreen.route)
                            }
                        },
                        icon = { Icon(Icons.Default.List, contentDescription = "Journal") },
                        label = { Text("Journal", color = Color.White) }
                    )
                    NavigationBarItem(
                        selected = selectedTabIndex == 3,
                        onClick = {
                            selectedTabIndex = 3
                            navController.navigate(Screen.SettingsScreen.route) {
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings", color = Color.White) }
                    )
                }
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                if (state.entries.isEmpty()) {
                    EmptyView(
                        message = "Let's add your first entry",
                        onAddEntry = { viewModel.showAddEntryDialog() },
                        currentTheme = currentTheme
                    )
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Lifetime Counter", color = contentColor, fontSize = 18.sp)
                            Spacer(Modifier.height(8.dp))
                            OdometerView(
                                steps = state.entries.sumOf { it.steps },
                                distanceMeters = state.entries.sumOf { it.distanceMeters },
                                calories = state.entries.sumOf { it.activeEnergyKcal },
                                color = contentColor
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Today Rings", color = contentColor, fontSize = 18.sp)
                            Spacer(Modifier.height(8.dp))
                            val stepsToday = state.entries.sumOf { it.steps }
                            val distanceToday = state.entries.sumOf { it.distanceMeters }
                            val caloriesToday = state.entries.sumOf { it.activeEnergyKcal }
                            Row(
                                horizontalArrangement = Arrangement.SpaceAround,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val goals = viewModel.dailyGoals.value

                                RingView(
                                    progress = stepsToday.toFloat() / goals.stepGoal,
                                    label = "Steps",
                                    color = contentColor,
                                    goalReached = stepsToday >= goals.stepGoal
                                )
                                RingView(
                                    progress = distanceToday.toFloat() / goals.distanceGoal,
                                    label = "Distance",
                                    color = contentColor,
                                    goalReached = distanceToday >= goals.distanceGoal
                                )
                                RingView(
                                    progress = caloriesToday.toFloat() / goals.calorieGoal,
                                    label = "Calories",
                                    color = contentColor,
                                    goalReached = caloriesToday >= goals.calorieGoal
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Quick Add", color = contentColor, fontSize = 18.sp)
                            Spacer(Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.SpaceAround,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                PrimaryButton(
                                    text = "+Steps",
                                    onClick = { viewModel.showAddEntryDialog() },
                                    backgroundColor = contentColor.copy(alpha = 0.2f),
                                    textColor = contentColor
                                )

                                PrimaryButton(
                                    text = "+Distance",
                                    onClick = { viewModel.showAddEntryDialog() },
                                    backgroundColor = contentColor.copy(alpha = 0.2f),
                                    textColor = contentColor
                                )

                                PrimaryButton(
                                    text = "+Calories",
                                    onClick = { viewModel.showAddEntryDialog() },
                                    backgroundColor = contentColor.copy(alpha = 0.2f),
                                    textColor = contentColor
                                )
                            }
                        }
                    }

                    HighlightsCard(
                        entries = state.entries,
                        contentColor = contentColor,
                        cardBackground = cardColor,
                        onClick = { navController.navigate(Screen.ComparisonScreen.route) }
                    )
                }
            }
        }

        if (viewModel.showAddDialog.value) {
            AddEditEntryDialog(
                editing = viewModel.editingEntry.value,
                onSave = { viewModel.saveEntry(it) },
                onCancel = { viewModel.hideAddEntryDialog() },
                splashScreenViewModel = splashScreenViewModel
            )
        }

        if (state.successEvent) {
            ConfettiOverlay()
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(1200)
                viewModel.consumeSuccess()
            }
        }
    }
}