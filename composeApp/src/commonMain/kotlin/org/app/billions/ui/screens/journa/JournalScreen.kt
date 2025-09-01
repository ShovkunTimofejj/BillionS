package org.app.billions.ui.screens.journa

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import org.app.billions.ui.screens.Screen
import org.app.billions.ui.screens.viewModel.JournalViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(navController: NavController, viewModel: JournalViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Journal") },
                actions = {
                    IconButton(onClick = { viewModel.toggleFilter() }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showAddEntryDialog() }) {
                Icon(Icons.Default.Add, contentDescription = "Add Entry")
            }
        }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(state.entries, key = { it.id }) { entry ->
                JournalItem(
                    entry = entry,
                    onClick = { navController.navigate(Screen.EntryDetailScreen.createRoute(entry.id)) },
//                    onEdit = { viewModel.editEntry(entry) },
                    onDelete = { viewModel.deleteEntry(entry.id) }
                )
            }
        }
    }

    if (state.showAddDialog) {
        AddEntryDialog(
            onSave = { viewModel.saveEntry(it) },
            onCancel = { viewModel.hideAddEntryDialog() }
        )
    }
}
