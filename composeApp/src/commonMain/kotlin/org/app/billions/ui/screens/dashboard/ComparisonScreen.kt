package org.app.billions.ui.screens.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import billions.composeapp.generated.resources.Res
import billions.composeapp.generated.resources.bg_dashboard_dark_lime
import billions.composeapp.generated.resources.bg_dashboard_graphite_gold
import billions.composeapp.generated.resources.bg_dashboard_neon_coral
import billions.composeapp.generated.resources.bg_dashboard_royal_blue
import billions.composeapp.generated.resources.ic_calories
import billions.composeapp.generated.resources.ic_distance
import billions.composeapp.generated.resources.ic_steps
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.*
import org.app.billions.data.model.Theme
import org.app.billions.ui.screens.buttonBar.AppBottomBar
import org.app.billions.ui.screens.viewModel.JournalViewModel
import org.app.billions.ui.screens.viewModel.SplashScreenViewModel
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonScreen(
    navController: NavController,
    viewModel: JournalViewModel,
    splashScreenViewModel: SplashScreenViewModel
) {
    val uiState by splashScreenViewModel.uiState.collectAsState()
    val currentTheme = uiState.currentTheme
    val goals = viewModel.dailyGoals.value

    val contentColor = Color(0xFFF6E19F)

    val cardColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFF334A32)
        "neon_coral" -> Color(0xFF4B2637)
        "royal_blue" -> Color(0xFF1C193C)
        "graphite_gold" -> Color(0xFF3C1919)
        else -> Color(0xFF334A32)
    }

    val backgroundRes = when (currentTheme?.id) {
        "dark_lime" -> Res.drawable.bg_dashboard_dark_lime
        "neon_coral" -> Res.drawable.bg_dashboard_neon_coral
        "royal_blue" -> Res.drawable.bg_dashboard_royal_blue
        "graphite_gold" -> Res.drawable.bg_dashboard_graphite_gold
        else -> Res.drawable.bg_dashboard_dark_lime
    }

    val barColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFF1F2D1E)
        "neon_coral" -> Color(0xFF2A151E)
        "royal_blue" -> Color(0xFF110E32)
        "graphite_gold" -> Color(0xFF320F0E)
        else -> Color(0xFF1F2D1E)
    }

    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    var selectedBottomNavIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.loadAllEntries()
    }

    val allEntries by viewModel.allEntries
    val todayEntries = allEntries.filter {
        (it.date as? LocalDateTime)?.date == today
    }

    val todaySteps = todayEntries.sumOf { it.steps }
    val todayDistance = todayEntries.sumOf { it.distanceMeters }
    val todayCalories = todayEntries.sumOf { it.activeEnergyKcal }

    val groupedByDate = remember(allEntries) {
        allEntries.groupBy {
            (it.date as? LocalDateTime)?.date
                ?: runCatching { LocalDate.parse(it.date.toString()) }.getOrNull()
        }.filterKeys { it != null }
    }

    data class DayStats(
        val steps: Long,
        val distance: Double,
        val calories: Double,
        val score: Float
    )

    val dailyStats = groupedByDate.mapValues { (_, entries) ->
        val steps = entries.sumOf { it.steps }
        val distance = entries.sumOf { it.distanceMeters }
        val calories = entries.sumOf { it.activeEnergyKcal }

        val stepsProgress = (steps.toFloat() / goals.stepGoal.coerceAtLeast(1)).coerceIn(0f, 1f)
        val distanceProgress = (distance.toFloat() / goals.distanceGoal.coerceAtLeast(1)).coerceIn(0f, 1f)
        val caloriesProgress = (calories.toFloat() / goals.calorieGoal.coerceAtLeast(1)).coerceIn(0f, 1f)

        val score = (stepsProgress + distanceProgress + caloriesProgress) / 3f
        DayStats(steps, distance, calories, score)
    }

    val bestDayEntry = dailyStats.maxByOrNull { it.value.score }
    val bestDayDate = bestDayEntry?.key
    val bestDayStats = bestDayEntry?.value ?: DayStats(0, 0.0, 0.0, 0f)

    val todayStepsProgress = (todaySteps.toFloat() / goals.stepGoal).coerceIn(0f, 1f)
    val todayDistanceProgress = (todayDistance.toFloat() / goals.distanceGoal).coerceIn(0f, 1f)
    val todayCaloriesProgress = (todayCalories.toFloat() / goals.calorieGoal).coerceIn(0f, 1f)

    val bestStepsProgress = (bestDayStats.steps.toFloat() / goals.stepGoal).coerceIn(0f, 1f)
    val bestDistanceProgress = (bestDayStats.distance.toFloat() / goals.distanceGoal).coerceIn(0f, 1f)
    val bestCaloriesProgress = (bestDayStats.calories.toFloat() / goals.calorieGoal).coerceIn(0f, 1f)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(backgroundRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Best day vs Today",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            bottomBar = {
                AppBottomBar(
                    navController = navController,
                    selectedTabIndex = selectedBottomNavIndex,
                    onTabSelected = { selectedBottomNavIndex = it },
                    barColor = barColor,
                    currentTheme = currentTheme
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ComparisonDayBlock(
                    label = "Today",
                    steps = todaySteps,
                    distance = todayDistance,
                    calories = todayCalories,
                    color = contentColor,
                    cardColor = cardColor,
                    currentTheme = currentTheme,
                    stepsProgress = todayStepsProgress,
                    distanceProgress = todayDistanceProgress,
                    caloriesProgress = todayCaloriesProgress
                )

                Spacer(Modifier.height(24.dp))

                ComparisonDayBlock(
                    label = bestDayDate?.dayOfWeek?.name
                        ?.lowercase()
                        ?.replaceFirstChar { it.uppercase() } ?: "Best",
                    steps = bestDayStats.steps,
                    distance = bestDayStats.distance,
                    calories = bestDayStats.calories,
                    color = contentColor,
                    cardColor = cardColor,
                    currentTheme = currentTheme,
                    stepsProgress = bestStepsProgress,
                    distanceProgress = bestDistanceProgress,
                    caloriesProgress = bestCaloriesProgress
                )
            }
        }
    }
}

@Composable
private fun ComparisonDayBlock(
    label: String,
    steps: Long,
    distance: Double,
    calories: Double,
    color: Color,
    cardColor: Color,
    currentTheme: Theme?,
    stepsProgress: Float,
    distanceProgress: Float,
    caloriesProgress: Float
) {
    val (stepsIcon, distanceIcon, caloriesIcon) = remember(currentTheme) {
        Triple(
            Res.drawable.ic_steps,
            Res.drawable.ic_distance,
            Res.drawable.ic_calories
        )
    }

    val avgProgress = (stepsProgress + distanceProgress + caloriesProgress) / 3f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Text(label, color = Color.White, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Box(contentAlignment = Alignment.Center) {
                MultiRingView(
                    stepsProgress = stepsProgress,
                    distanceProgress = distanceProgress,
                    caloriesProgress = caloriesProgress,
                    color = color,
                    ringSize = 220.dp
                )
                Text(
                    text = "${(avgProgress * 100).toInt()}%",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(stepsIcon, "$steps st", color, cardColor)
            StatCard(distanceIcon, "${distance.toInt()} km", color, cardColor)
            StatCard(caloriesIcon, "${calories.toInt()} kcal", color, cardColor)
        }
    }
}

@Composable
private fun StatCard(icon: DrawableResource, value: String, color: Color, bg: Color) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        modifier = Modifier.width(100.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 2.dp),
                colorFilter = ColorFilter.tint(color)
            )
            Spacer(Modifier.height(4.dp))
            Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}