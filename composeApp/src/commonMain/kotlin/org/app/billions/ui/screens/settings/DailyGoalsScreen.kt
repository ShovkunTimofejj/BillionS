package org.app.billions.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.app.billions.ui.screens.viewModel.JournalViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyGoalsScreen(
    navController: NavHostController,
    journalViewModel: JournalViewModel = koinInject()
) {
    val state by journalViewModel.state

    var stepGoal by remember {
        mutableStateOf((state.entries.maxOfOrNull { it.steps } ?: 1000L).toInt())
    }
    var distanceGoal by remember {
        mutableStateOf((state.entries.maxOfOrNull { it.distanceMeters } ?: 1000.0).toInt())
    }
    var calorieGoal by remember {
        mutableStateOf((state.entries.maxOfOrNull { it.activeEnergyKcal } ?: 500.0).toInt())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Goals", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF001F3F))
            )
        },
        containerColor = Color(0xFF001F3F)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            GoalSlider("Step Goal", stepGoal, 1000..50000) { stepGoal = it }
            GoalSlider("Distance Goal (m)", distanceGoal, 100..50000) { distanceGoal = it }
            GoalSlider("Calories Goal", calorieGoal, 100..10000) { calorieGoal = it }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF00)),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Goals", color = Color.Black)
            }
        }
    }
}

@Composable
fun GoalSlider(label: String, value: Int, range: IntRange, onValueChange: (Int) -> Unit) {
    Column {
        Text(
            text = "$label: $value",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF00FF00),
                activeTrackColor = Color(0xFF00FF00),
                inactiveTrackColor = Color.Gray
            )
        )
    }
}

