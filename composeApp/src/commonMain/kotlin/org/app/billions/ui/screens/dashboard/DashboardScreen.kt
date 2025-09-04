package org.app.billions.ui.screens.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import billions.composeapp.generated.resources.Res
import billions.composeapp.generated.resources.app_logo
import org.jetbrains.compose.resources.painterResource
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.app.billions.ui.screens.Screen
import org.app.billions.ui.screens.journa.AddEditEntryDialog
import org.app.billions.ui.screens.viewModel.JournalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: JournalViewModel, navController: NavHostController) {
    val state by viewModel.state
    val Lime = Color(0xFF00FF00)
    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF001F3F)),
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
            NavigationBar(containerColor = Color(0xFF001F3F)) {
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
        containerColor = Color(0xFF001F3F)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            if (state.entries.isEmpty()) {
                EmptyView("Let's add your first entry") { viewModel.showAddEntryDialog() }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF002A55))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Lifetime Counter", color = Color.White, fontSize = 18.sp)
                        Spacer(Modifier.height(8.dp))
                        OdometerView(
                            steps = state.entries.sumOf { it.steps },
                            distanceMeters = state.entries.sumOf { it.distanceMeters },
                            calories = state.entries.sumOf { it.activeEnergyKcal },
                            color = Lime
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF002A55))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Today Rings", color = Color.White, fontSize = 18.sp)
                        Spacer(Modifier.height(8.dp))
                        val stepsToday = state.entries.sumOf { it.steps }
                        val distanceToday = state.entries.sumOf { it.distanceMeters }
                        val caloriesToday = state.entries.sumOf { it.activeEnergyKcal }
                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RingView(
                                progress = stepsToday.toFloat() / 10000,
                                label = "Steps",
                                color = Lime,
                                goalReached = stepsToday >= 10000
                            )
                            RingView(
                                progress = distanceToday.toFloat() / 5000,
                                label = "Distance",
                                color = Lime,
                                goalReached = distanceToday >= 5000
                            )
                            RingView(
                                progress = caloriesToday.toFloat() / 500,
                                label = "Calories",
                                color = Lime,
                                goalReached = caloriesToday >= 500
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF002A55))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Quick Add", color = Color.White, fontSize = 18.sp)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            PrimaryButton("+Steps") { viewModel.showAddEntryDialog() }
                            PrimaryButton("+Distance") { viewModel.showAddEntryDialog() }
                            PrimaryButton("+Calories") { viewModel.showAddEntryDialog() }
                        }
                    }
                }

                HighlightsCard(
                    entries = state.entries,
                    color = Lime,
                    onClick = { navController.navigate(Screen.ComparisonScreen.route) }
                )
            }
        }
    }

    if (viewModel.showAddDialog.value) {
        AddEditEntryDialog(
            editing = viewModel.editingEntry.value,
            onSave = { viewModel.saveEntry(it) },
            onCancel = { viewModel.hideAddEntryDialog() }
        )
    }
}
