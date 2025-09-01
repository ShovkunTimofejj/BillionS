package org.app.billions.ui.screens.journa

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.app.billions.data.model.ActivitySample
import org.app.billions.ui.screens.viewModel.JournalViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryDetailScreen(
    navController: NavHostController,
    viewModel: JournalViewModel,
) {
    val entry = viewModel.selectedEntry.collectAsState().value
    if (entry == null) {
        Text("Entry not found")
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Entry") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(entry.date.toString(), fontWeight = FontWeight.SemiBold)

            val metrics = buildString {
                if (entry.steps > 0) append(entry.steps.asSteps())
                if (entry.distanceMeters > 0) append(" · ${entry.distanceMeters.asKm()}")
                if (entry.activeEnergyKcal > 0) append(" · ${entry.activeEnergyKcal.asKcal()}")
            }
            if (metrics.isNotEmpty()) Text(metrics)
            Text("Source: ${entry.source}")
            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    viewModel.startEdit(entry)
                    navController.popBackStack()
                }) {
                    Text("Edit")
                }
                OutlinedButton(onClick = {
                    viewModel.deleteEntry(entry.id)
                    navController.popBackStack()
                }) {
                    Text("Delete")
                }
                OutlinedButton(onClick = {
                    viewModel.showAddEntryDialog(entry.copy(id = 0L))
                    navController.popBackStack()
                }) {
                    Text("Add similar")
                }
            }
        }
    }
}
