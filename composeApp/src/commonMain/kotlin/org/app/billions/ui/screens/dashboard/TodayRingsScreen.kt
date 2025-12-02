package org.app.billions.ui.screens.dashboard

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import billions.composeapp.generated.resources.Res
import billions.composeapp.generated.resources.bg_dashboard_dark_lime
import billions.composeapp.generated.resources.bg_dashboard_graphite_gold
import billions.composeapp.generated.resources.bg_dashboard_neon_coral
import billions.composeapp.generated.resources.bg_dashboard_royal_blue
import kotlinx.coroutines.delay
import org.app.billions.ui.screens.buttonBar.AppBottomBar
import org.app.billions.ui.screens.viewModel.JournalViewModel
import org.app.billions.ui.screens.viewModel.SplashScreenViewModel
import org.jetbrains.compose.resources.painterResource
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayRingsScreen(
    viewModel: JournalViewModel,
    navController: NavController,
    splashScreenViewModel: SplashScreenViewModel
) {
    val state by viewModel.state
    val uiState by splashScreenViewModel.uiState.collectAsState()
    val currentTheme = uiState.currentTheme

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

    val ringColor = Color(0xFFF6E19F)

    val cardColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFF334A32)
        "neon_coral" -> Color(0xFF4B2637)
        "royal_blue" -> Color(0xFF1C193C)
        "graphite_gold" -> Color(0xFF3C1919)
        else -> Color(0xFF334A32)
    }

    val stepsToday = state.entries.sumOf { it.steps }
    val distanceToday = state.entries.sumOf { it.distanceMeters }
    val caloriesToday = state.entries.sumOf { it.activeEnergyKcal }
    val goals = viewModel.dailyGoals.value
    var selectedTabIndex by remember { mutableStateOf(0) }

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
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    title = {
                        Box(
                            Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Today Rings",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                modifier = Modifier.align(Alignment.CenterStart),
                                onClick = { navController.popBackStack() }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
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
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val totalProgress = (
                            (stepsToday.toDouble() / goals.stepGoal +
                                    (distanceToday / goals.distanceGoal) +
                                    (caloriesToday / goals.calorieGoal)) / 3.0
                            ).coerceIn(0.0, 1.0).toFloat()

                    MultiRingView(
                        stepsProgress = (stepsToday.toDouble() / goals.stepGoal).toFloat(),
                        distanceProgress = ((distanceToday / goals.distanceGoal).toFloat()),
                        caloriesProgress = (caloriesToday / goals.calorieGoal).toFloat(),
                        color = ringColor
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        StatItem(
                            label = "Steps",
                            value = stepsToday.toString(),
                            accent = ringColor,
                            bg = cardColor,
                            onDecrement = { viewModel.adjustEntry("steps", -1.0) },
                            onIncrement = { viewModel.adjustEntry("steps", 1.0) }
                        )

                        StatItem(
                            label = "Distance",
                            value = "${distanceToday.roundToInt()} km",
                            accent = ringColor,
                            bg = cardColor,
                            onDecrement = { viewModel.adjustEntry("distance", -0.1) },
                            onIncrement = { viewModel.adjustEntry("distance", 0.1) }
                        )

                        StatItem(
                            label = "Calories",
                            value = kotlin.math.round(caloriesToday).toInt().toString(),
                            accent = ringColor,
                            bg = cardColor,
                            onDecrement = { viewModel.adjustEntry("calories", -10.0) },
                            onIncrement = { viewModel.adjustEntry("calories", 10.0) }
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    "Activity history:",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(8.dp))

                state.entries.sortedByDescending { it.date }.forEach { entry ->
                    val (type, value) = when {
                        entry.steps > 0 -> "Steps" to "${entry.steps} steps"
                        entry.distanceMeters > 0 -> "Distance" to "${entry.distanceMeters} km"
                        else -> "Calories" to "${entry.activeEnergyKcal} kcal"
                    }
                    val date = entry.date.date.toString()
                    ActivityHistoryItem(
                        date = date,
                        type = type,
                        value = value,
                        color = ringColor,
                        bg = cardColor
                    )
                }

                Spacer(Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    accent: Color,
    bg: Color,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.White, fontSize = 15.sp)
        Spacer(Modifier.height(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(bg, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text("–", color = accent, fontSize = 20.sp, modifier = Modifier.clickable { onDecrement() })
            Spacer(Modifier.width(10.dp))
            Text(value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.width(10.dp))
            Text("+", color = accent, fontSize = 20.sp, modifier = Modifier.clickable { onIncrement() })
        }
    }
}

@Composable
fun ActivityHistoryItem(date: String, type: String, value: String, color: Color, bg: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = bg),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = date,
                    color = color,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Spacer(Modifier.width(8.dp))
                Text(type, color = Color.White, fontSize = 14.sp)
            }
            Text(value, color = Color.White, fontSize = 14.sp)
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
@Composable
fun MultiRingView(
    stepsProgress: Float,
    distanceProgress: Float,
    caloriesProgress: Float,
    color: Color,
    modifier: Modifier = Modifier,
    ringSize: Dp = 180.dp
) {
    var animateSteps by remember { mutableStateOf(0f) }
    var animateDistance by remember { mutableStateOf(0f) }
    var animateCalories by remember { mutableStateOf(0f) }

    val stepsAnimated by animateFloatAsState(
        targetValue = animateSteps,
        animationSpec = tween(durationMillis = 1600, easing = LinearOutSlowInEasing)
    )
    val distanceAnimated by animateFloatAsState(
        targetValue = animateDistance,
        animationSpec = tween(durationMillis = 1600, easing = LinearOutSlowInEasing)
    )
    val caloriesAnimated by animateFloatAsState(
        targetValue = animateCalories,
        animationSpec = tween(durationMillis = 1600, easing = LinearOutSlowInEasing)
    )

    val animationKey = remember { Uuid.random() }

    LaunchedEffect(animationKey) {
        animateSteps = 0f
        animateDistance = 0f
        animateCalories = 0f

        delay(300)

        animateSteps = stepsProgress
        delay(900)
        animateDistance = distanceProgress
        delay(900)
        animateCalories = caloriesProgress
    }

    Box(modifier = modifier.size(ringSize)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val drawSize = this.size
            val minSide = kotlin.math.min(drawSize.width, drawSize.height)

            val stroke = minSide * 0.07f
            val spacing = stroke * 0.6f
            val center = Offset(drawSize.width / 2f, drawSize.height / 2f)
            val radiusBase = minSide / 2f - stroke / 2f

            val rings = listOf(stepsAnimated, distanceAnimated, caloriesAnimated)

            rings.forEachIndexed { index, progress ->
                val radius = radiusBase - index * (stroke + spacing)
                val topLeft = Offset(center.x - radius, center.y - radius)
                val sz = Size(radius * 2, radius * 2)

                drawArc(
                    color = color.copy(alpha = 0.2f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = sz,
                    style = Stroke(width = stroke)
                )

                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360f * progress.coerceIn(0f, 1f),
                    useCenter = false,
                    topLeft = topLeft,
                    size = sz,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            }
        }
    }
}