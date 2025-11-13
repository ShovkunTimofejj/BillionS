package org.app.billions.ui.screens.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import billions.composeapp.generated.resources.Res
import billions.composeapp.generated.resources.add_dark_lime
import billions.composeapp.generated.resources.add_graphite_gold
import billions.composeapp.generated.resources.add_neon_coral
import billions.composeapp.generated.resources.add_royal_blue
import billions.composeapp.generated.resources.bg_dashboard_dark_lime
import billions.composeapp.generated.resources.bg_dashboard_graphite_gold
import billions.composeapp.generated.resources.bg_dashboard_neon_coral
import billions.composeapp.generated.resources.bg_dashboard_royal_blue
import billions.composeapp.generated.resources.ic_arrow_right
import org.app.billions.ui.screens.Screen
import org.app.billions.ui.screens.buttonBar.AppBottomBar
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
        "dark_lime" -> Color(0xFFB6FE03)
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

    val addEntry by remember(currentTheme) {
        derivedStateOf {
            when (currentTheme?.id) {
                "dark_lime" -> Res.drawable.add_dark_lime
                "neon_coral" -> Res.drawable.add_neon_coral
                "royal_blue" -> Res.drawable.add_royal_blue
                "graphite_gold" -> Res.drawable.add_graphite_gold
                else -> Res.drawable.add_dark_lime
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
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    title = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Dashboard",
                                color = Color.White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold
                            )

                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .offset(x = (-8).dp, y = 6.dp)
                                    .size(60.dp)
                                    .clickable { viewModel.showAddEntryDialog("steps") },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(addEntry),
                                    contentDescription = "Add Entry",
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                        }
                    }
                )
            },
            bottomBar = {
                AppBottomBar(
                    navController = navController,
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { selectedTabIndex = it },
                    barColor = barColor,
                    currentTheme = currentTheme

                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            if (state.entries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyView(
                        onAddEntry = { viewModel.showAddEntryDialog("steps") },
                        currentTheme = currentTheme
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Lifetime Counter",
                                color = contentColor,
                                fontSize = 18.sp
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Transparent, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                OdometerView(
                                    steps = state.entries.sumOf { it.steps },
                                    distanceMeters = state.entries.sumOf { it.distanceMeters },
                                    calories = state.entries.sumOf { it.activeEnergyKcal },
                                    color = Color.White
                                )
                            }
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Today Rings",
                                    color = contentColor,
                                    fontSize = 18.sp
                                )

                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clickable { navController.navigate(Screen.TodayRings.route) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(Res.drawable.ic_arrow_right),
                                        contentDescription = "Open Today Rings",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            val entries = state.entries

                            val stepsToday by remember(entries) {
                                derivedStateOf { entries.sumOf { it.steps } }
                            }
                            val distanceToday by remember(entries) {
                                derivedStateOf { entries.sumOf { it.distanceMeters } }
                            }
                            val caloriesToday by remember(entries) {
                                derivedStateOf { entries.sumOf { it.activeEnergyKcal } }
                            }

                            Row(
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val goals = viewModel.dailyGoals.value

                                RingView(
                                    progress = stepsToday.toFloat() / goals.stepGoal,
                                    label = "Step",
                                    color = contentColor,
                                    goalReached = stepsToday >= goals.stepGoal,
                                    size = 70.dp
                                )

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.offset(y = (-8).dp)
                                ) {
                                    RingView(
                                        progress = distanceToday.toFloat() / goals.distanceGoal,
                                        label = "Distance",
                                        color = contentColor,
                                        goalReached = distanceToday >= goals.distanceGoal,
                                        size = 100.dp
                                    )
                                }

                                RingView(
                                    progress = caloriesToday.toFloat() / goals.calorieGoal,
                                    label = "Calories",
                                    color = contentColor,
                                    goalReached = caloriesToday >= goals.calorieGoal,
                                    size = 70.dp
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceAround,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                SmallAddCard(
                                    label = "Steps",
                                    textColor = Color.White,
                                    bgColor = cardColor,
                                    onClick = { viewModel.showAddEntryDialog("steps") }
                                )
                                SmallAddCard(
                                    label = "Distance",
                                    textColor = Color.White,
                                    bgColor = cardColor,
                                    onClick = { viewModel.showAddEntryDialog("distance") }
                                )
                                SmallAddCard(
                                    label = "Calories",
                                    textColor = Color.White,
                                    bgColor = cardColor,
                                    onClick = { viewModel.showAddEntryDialog("calories") }
                                )
                            }
                        }
                    }

                    HighlightsCard(
                        viewModel = viewModel,
                        navController = navController,
                        contentColor = contentColor,
                        cardBackground = cardColor
                    )
                }
            }
        }

        if (viewModel.showAddDialog.value) {
            AddEditEntryDialog(
                editing = viewModel.editingEntry.value,
                typeFromViewModel = viewModel.state.value.activeAddType,
                onSave = {
                    viewModel.saveEntry(it) },
                onCancel = { viewModel.hideAddEntryDialog() },
                splashScreenViewModel = splashScreenViewModel
            )
        }

        if (state.successEvent) {
            ConfettiOverlay(currentTheme)
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(1200)
                viewModel.consumeSuccess()
            }
        }
    }
}