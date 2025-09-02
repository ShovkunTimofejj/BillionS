package org.app.billions.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.*
import org.app.billions.ui.screens.viewModel.JournalViewModel
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun ComparisonScreen(
    navController: NavController,
    viewModel: JournalViewModel
) {
    val state by viewModel.state
    val Lime = Color(0xFF00FF00)

    val today: LocalDate = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }
    val weekStart = remember(today) { today.minus(DatePeriod(days = 6)) }

    val todaySteps = remember(state.entries, today) {
        state.entries
            .mapNotNull { e ->
                val entryDate: LocalDate? = when (val d = e.date) {
                    is LocalDateTime -> d.date
                    is LocalDate -> d
                    is String -> runCatching { LocalDate.parse(d) }.getOrNull()
                    else -> null
                }
                entryDate?.let { it to e.steps.toInt() }
            }
            .filter { (date, _) -> date == today }
            .sumOf { it.second }
    }

    val (bestDayDate, bestDaySteps) = remember(state.entries, weekStart, today) {
        val byDay: Map<LocalDate, Int> = state.entries.asSequence()
            .mapNotNull { e ->
                runCatching {
                    (if (e.date is LocalDateTime) (e.date as LocalDateTime).date else LocalDate.parse(e.date.toString())) to e.steps.toInt()
                }.getOrNull()
            }
            .filter { (d, _) -> d >= weekStart && d <= today }
            .groupBy({ it.first }, { it.second })
            .mapValues { it.value.sum() }

        val best = byDay.maxByOrNull { it.value }
        (best?.key ?: today) to (best?.value ?: 0)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comparison", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF001F3F))
            )
        },
        containerColor = Color(0xFF001F3F)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF002A55)),
                shape = CardDefaults.shape
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Best Day", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Text("${bestDaySteps} steps", color = Lime, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Date: ${bestDayDate}", color = Color.LightGray)
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF002A55)),
                shape = CardDefaults.shape
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Today", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Text("${todaySteps} steps", color = Lime, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Date: ${today}", color = Color.LightGray)
                }
            }

            Spacer(Modifier.height(24.dp))

            val progress = if (bestDaySteps > 0) todaySteps.toFloat() / bestDaySteps else 0f
            LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                color = Lime,
                trackColor = Color.DarkGray,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            when {
                bestDaySteps == 0 -> Text("No data yet", color = Color.Gray)
                todaySteps >= bestDaySteps -> Text(
                    "🔥 You beat your best day by ${todaySteps - bestDaySteps} steps",
                    color = Lime
                )
                else -> Text(
                    "${bestDaySteps - todaySteps} steps to beat your best day",
                    color = Color.White
                )
            }

            Spacer(Modifier.height(24.dp))
            PrimaryButton(text = "Back to Dashboard") { navController.popBackStack() }
        }
    }
}