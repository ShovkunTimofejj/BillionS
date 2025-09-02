package org.app.billions.ui.screens.journa

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import org.app.billions.ui.screens.Screen
import org.app.billions.ui.screens.viewModel.JournalViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    navController: NavController,
    viewModel: JournalViewModel
) {
    val state by viewModel.state
    var selectedTabIndex by remember { mutableStateOf(2) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Journal", color = Color.White) },
                actions = {
                    IconButton(onClick = { viewModel.toggleFilterSheet() }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                    IconButton(onClick = { viewModel.openStats() }) {
                        Icon(Icons.Default.Insights, contentDescription = "Statistics")
                    }
                    IconButton(onClick = { viewModel.exportCsv() }) {
                        Icon(Icons.Default.Share, contentDescription = "Export CSV")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF001F3F))
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showAddEntryDialog() }) {
                Icon(Icons.Default.Add, contentDescription = "Add Entry")
            }
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
                    onClick = { selectedTabIndex = 2 },
                    icon = { Icon(Icons.Default.List, contentDescription = "Journal") },
                    label = { Text("Journal", color = Color.White) }
                )
                NavigationBarItem(
                    selected = selectedTabIndex == 3,
                    onClick = {
                        selectedTabIndex = 3
                        navController.navigate("settings") {
                            popUpTo("dashboard")
                        }
                    },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings", color = Color.White) }
                )
            }
        },
        containerColor = Color(0xFF001F3F)
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.entries.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No entries yet", color = Color.White)
                }
            } else {
                LazyColumn {
                    items(state.entries, key = { it.id }) { entry ->
                        JournalItem(
                            entry = entry,
                            onClick = {
                                viewModel.selectEntry(entry)
                                navController.navigate("entryDetail")
                            },
                            onEdit = { viewModel.startEdit(entry) },
                            onDelete = { viewModel.deleteEntry(entry.id) }
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

            if (state.showFilterSheet) {
                FilterBottomSheet(
                    currentFilter = state.filter,
                    currentMetric = state.metric,
                    onSelectFilter = { viewModel.selectFilterAndReload(it) },
                    onSelectMetric = { viewModel.setMetric(it) },
                    onDismiss = { viewModel.toggleFilterSheet() }
                )
            }

            if (state.showStatsSheet) {
                StatisticsSheet(
                    entries = state.entries,
                    metric = state.metric,
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