package org.app.billions.ui.screens.settings

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import org.app.billions.data.model.DailyGoals
import org.app.billions.ui.screens.viewModel.JournalViewModel
import org.koin.compose.koinInject
import kotlin.math.PI
import kotlin.math.atan2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyGoalsScreen(
    navController: NavHostController,
    journalViewModel: JournalViewModel = koinInject()
) {
    val state by journalViewModel.state

    var stepGoal by remember { mutableStateOf(journalViewModel.dailyGoals.value.stepGoal) }
    var distanceGoal by remember { mutableStateOf(journalViewModel.dailyGoals.value.distanceGoal) }
    var calorieGoal by remember { mutableStateOf(journalViewModel.dailyGoals.value.calorieGoal) }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RingSlider("Steps", stepGoal, 1000L..50000L, { stepGoal = it }, color = Color(0xFF00FF00))
                RingSlider("Distance", distanceGoal, 100L..50000L, { distanceGoal = it }, color = Color(0xFF00BFFF))
                RingSlider("Calories", calorieGoal, 100L..10000L, { calorieGoal = it }, color = Color(0xFFFF4500))
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    journalViewModel.updateGoals(DailyGoals(stepGoal, distanceGoal, calorieGoal))
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
fun RingSlider(
    label: String,
    value: Long,
    range: LongRange,
    onValueChange: (Long) -> Unit,
    color: Color,
    size: Dp = 120.dp
) {
    var progress by remember { mutableStateOf((value - range.first).toFloat() / (range.last - range.first)) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val center = Offset(size.toPx() / 2, size.toPx() / 2)
                    val touch = change.position - center
                    val angle = (atan2(touch.y, touch.x) * 180f / PI).toFloat()
                    val normalized = ((angle + 450f) % 360f) / 360f
                    progress = normalized
                    val newValue = (range.first + progress * (range.last - range.first)).toLong()
                    onValueChange(newValue)
                }
            }
    ) {
        Canvas(modifier = Modifier.size(size)) {
            drawCircle(Color.Gray.copy(alpha = 0.2f), style = Stroke(16f))
            drawArc(
                color,
                startAngle = -90f,
                sweepAngle = 360 * progress,
                useCenter = false,
                style = Stroke(16f, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$value", color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(label, color = Color.White, fontSize = 14.sp)
        }
    }
}

