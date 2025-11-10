package org.app.billions.ui.screens.journa

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import billions.composeapp.generated.resources.Res
import billions.composeapp.generated.resources.bg_dashboard_dark_lime
import billions.composeapp.generated.resources.bg_dashboard_graphite_gold
import billions.composeapp.generated.resources.bg_dashboard_neon_coral
import billions.composeapp.generated.resources.bg_dashboard_royal_blue
import org.app.billions.ui.screens.Screen
import org.app.billions.ui.screens.viewModel.JournalViewModel
import org.app.billions.ui.screens.viewModel.SplashScreenViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    navController: NavController,
    viewModel: JournalViewModel,
    splashScreenViewModel: SplashScreenViewModel
) {
    val state by viewModel.state

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
        "graphite_gold" -> Color(0xFFFFD700)
        else -> Color(0xFF00FF00)
    }

    var selectedBottomNavIndex by remember { mutableStateOf(2) }

    val pageSize = 6
    var currentPage by remember { mutableStateOf(0) }
    val pages = state.entries.chunked(pageSize)

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
                    title = { Text("Journal", color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = barColor),
                    actions = {
                        IconButton(onClick = { viewModel.toggleFilterSheet() }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = Color.White)
                        }
                        IconButton(onClick = { viewModel.openStats() }) {
                            Icon(Icons.Default.Insights, contentDescription = "Statistics", tint = Color.White)
                        }
                        IconButton(onClick = { viewModel.exportCsv() }) {
                            Icon(Icons.Default.Share, contentDescription = "Export CSV", tint = Color.White)
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.showAddEntryDialog() },
                    modifier = Modifier
                        .padding(bottom = 70.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Entry")
                }
            },
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                NavigationBar(containerColor = barColor) {
                    val navItems = listOf(
                        Screen.MainMenuScreen to Icons.Default.Home,
                        Screen.ChallengesScreen to Icons.Default.Flag,
                        Screen.JournalScreen to Icons.Default.List,
                        Screen.SettingsScreen to Icons.Default.Settings
                    )

                    navItems.forEachIndexed { index, (screen, icon) ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                selectedBottomNavIndex = index
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(imageVector = icon, contentDescription = screen.route, tint = Color.White) },
                            label = { Text(screen.route.replaceFirstChar { it.uppercase() }, color = Color.White) }
                        )
                    }
                }
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (pages.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No entries yet", color = Color.White)
                    }
                } else {
                    Column(Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = Modifier.weight(1f)
                        ) {
                            items(pages[currentPage], key = { it.id }) { entry ->
                                JournalItem(
                                    entry = entry,
                                    onClick = {
                                        viewModel.selectEntry(entry)
                                        navController.navigate("entryDetail")
                                    },
                                    onEdit = { viewModel.startEdit(entry) },
                                    onDelete = { viewModel.deleteEntry(entry.id) },
                                    currentTheme = currentTheme,
                                    cardColor = cardColor,
                                    contentColor = contentColor
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { if (currentPage > 0) currentPage-- },
                                enabled = currentPage > 0
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Previous", tint = Color.White)
                            }

                            Text(
                                text = "Page ${currentPage + 1} / ${pages.size}",
                                color = Color.White
                            )

                            IconButton(
                                onClick = { if (currentPage < pages.lastIndex) currentPage++ },
                                enabled = currentPage < pages.lastIndex
                            ) {
                                Icon(Icons.Default.ArrowForward, contentDescription = "Next", tint = Color.White)
                            }
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

                if (state.showFilterSheet) {
                    FilterBottomSheet(
                        currentFilter = state.filter,
                        currentMetric = state.metric,
                        currentTheme = uiState.currentTheme,
                        onSelectFilter = { viewModel.selectFilterAndReload(it) },
                        onSelectMetric = { viewModel.setMetric(it) },
                        onDismiss = { viewModel.toggleFilterSheet() }
                    )
                }

                if (state.showStatsSheet) {
                    StatisticsSheet(
                        entries = state.entries,
                        metric = state.metric,
                        currentTheme = uiState.currentTheme,
                        onClose = { viewModel.closeStats() }
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
    }
}