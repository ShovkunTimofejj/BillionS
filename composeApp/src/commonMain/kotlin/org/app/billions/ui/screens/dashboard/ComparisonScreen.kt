package org.app.billions.ui.screens.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import billions.composeapp.generated.resources.Res
import billions.composeapp.generated.resources.bg_dashboard_dark_lime
import billions.composeapp.generated.resources.bg_dashboard_graphite_gold
import billions.composeapp.generated.resources.bg_dashboard_neon_coral
import billions.composeapp.generated.resources.bg_dashboard_royal_blue
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.*
import org.app.billions.ui.screens.viewModel.JournalViewModel
import org.app.billions.ui.screens.viewModel.SplashScreenViewModel
import org.jetbrains.compose.resources.painterResource
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun ComparisonScreen(
    navController: NavController,
    viewModel: JournalViewModel,
    splashScreenViewModel: SplashScreenViewModel
) {
    val state by viewModel.state

    val uiState by splashScreenViewModel.uiState.collectAsState()
    val currentTheme = uiState.currentTheme

    val contentColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFF00FF00)
        "neon_coral" -> Color(0xFFFF8FA0)
        "royal_blue" -> Color(0xFF8FCFFF)
        "graphite_gold" -> Color(0xFFFFD700)
        else -> Color(0xFF00FF00)
    }

    val cardColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFF1C2A3A)
        "neon_coral" -> Color(0xFF431C2E)
        "royal_blue" -> Color(0xFF1D3B5C)
        "graphite_gold" -> Color(0xFF383737)
        else -> Color(0xFF1C2A3A)
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
                    title = { Text("Comparison", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Best Day", color = Color.White, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "${bestDaySteps} steps",
                            color = contentColor,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Date: $bestDayDate", color = Color.LightGray)
                    }
                }

                Spacer(Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Today", color = Color.White, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "$todaySteps steps",
                            color = contentColor,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Date: $today", color = Color.LightGray)
                    }
                }

                Spacer(Modifier.height(24.dp))

                val progress = if (bestDaySteps > 0) todaySteps.toFloat() / bestDaySteps else 0f
                LinearProgressIndicator(
                    progress = progress.coerceIn(0f, 1f),
                    color = contentColor,
                    trackColor = Color.DarkGray,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                PrimaryButton(
                    text = "Back to Dashboard",
                    onClick = { navController.popBackStack() },
                    backgroundColor = contentColor.copy(alpha = 0.2f),
                    textColor = contentColor
                )
            }
        }
    }
}