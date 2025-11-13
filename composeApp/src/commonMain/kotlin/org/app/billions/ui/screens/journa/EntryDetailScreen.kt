package org.app.billions.ui.screens.journa

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import billions.composeapp.generated.resources.Res
import billions.composeapp.generated.resources.bg_dashboard_dark_lime
import billions.composeapp.generated.resources.bg_dashboard_graphite_gold
import billions.composeapp.generated.resources.bg_dashboard_neon_coral
import billions.composeapp.generated.resources.bg_dashboard_royal_blue
import billions.composeapp.generated.resources.ic_calories_dark_lime
import billions.composeapp.generated.resources.ic_calories_graphite_gold
import billions.composeapp.generated.resources.ic_calories_neon_coral
import billions.composeapp.generated.resources.ic_calories_royal_blue
import billions.composeapp.generated.resources.ic_distance_dark_lime
import billions.composeapp.generated.resources.ic_distance_graphite_gold
import billions.composeapp.generated.resources.ic_distance_neon_coral
import billions.composeapp.generated.resources.ic_distance_royal_blue
import billions.composeapp.generated.resources.ic_steps_dark_lime
import billions.composeapp.generated.resources.ic_steps_dark_lime_big
import billions.composeapp.generated.resources.ic_steps_graphite_gold
import billions.composeapp.generated.resources.ic_steps_graphite_gold_big
import billions.composeapp.generated.resources.ic_steps_neon_coral
import billions.composeapp.generated.resources.ic_steps_neon_coral_big
import billions.composeapp.generated.resources.ic_steps_royal_blue
import billions.composeapp.generated.resources.ic_steps_royal_blue_big
import org.app.billions.data.model.ActivitySample
import org.app.billions.data.model.Theme
import org.app.billions.ui.screens.buttonBar.AppBottomBar
import org.app.billions.ui.screens.viewModel.JournalViewModel
import org.app.billions.ui.screens.viewModel.SplashScreenViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryDetailScreen(
    navController: NavHostController,
    viewModel: JournalViewModel,
    splashScreenViewModel: SplashScreenViewModel
) {
    val entry = viewModel.selectedEntry.collectAsState().value
    if (entry == null) {
        Text("Entry not found")
        return
    }

    val uiState by splashScreenViewModel.uiState.collectAsState()
    val currentTheme = uiState.currentTheme
    val state by viewModel.state

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

    val stepsIconRes = when (currentTheme?.id) {
        "dark_lime" -> Res.drawable.ic_steps_dark_lime_big
        "neon_coral" -> Res.drawable.ic_steps_neon_coral_big
        "royal_blue" -> Res.drawable.ic_steps_royal_blue_big
        "graphite_gold" -> Res.drawable.ic_steps_graphite_gold_big
        else -> Res.drawable.ic_steps_dark_lime_big
    }

    val distanceIconRes = when (currentTheme?.id) {
        "dark_lime" -> Res.drawable.ic_distance_dark_lime
        "neon_coral" -> Res.drawable.ic_distance_neon_coral
        "royal_blue" -> Res.drawable.ic_distance_royal_blue
        "graphite_gold" -> Res.drawable.ic_distance_graphite_gold
        else -> Res.drawable.ic_distance_dark_lime
    }

    val caloriesIconRes = when (currentTheme?.id) {
        "dark_lime" -> Res.drawable.ic_calories_dark_lime
        "neon_coral" -> Res.drawable.ic_calories_neon_coral
        "royal_blue" -> Res.drawable.ic_calories_royal_blue
        "graphite_gold" -> Res.drawable.ic_calories_graphite_gold
        else -> Res.drawable.ic_calories_dark_lime
    }

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
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    title = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Journal",
                                color = Color.White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier.align(Alignment.CenterStart)
                            ) {
                                Icon(
                                    Icons.Default.ArrowBack,
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
                    selectedTabIndex = selectedBottomNavIndex,
                    onTabSelected = { selectedBottomNavIndex = it },
                    barColor = barColor,
                    currentTheme = currentTheme
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(215.dp)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 18.dp, end = 18.dp, top = 30.dp, bottom = 18.dp)
                        ) {

                            Text(
                                text = entry.date.date.toString(),
                                color = contentColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            Spacer(Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                val iconRes = when {
                                    entry.steps > 0 -> stepsIconRes
                                    entry.distanceMeters > 0 -> distanceIconRes
                                    else -> caloriesIconRes
                                }

                                Image(
                                    painter = painterResource(iconRes),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(72.dp)
                                        .offset(y = (-2).dp)
                                )

                                Spacer(Modifier.width(24.dp))

                                val metrics = when {
                                    entry.steps > 0 -> entry.steps.asSteps()
                                    entry.distanceMeters > 0 -> entry.distanceMeters.asKm()
                                    else -> entry.activeEnergyKcal.asKcal()
                                }

                                Column {
                                    Text(
                                        metrics,
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        entry.source,
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 16.sp
                                    )
                                }
                            }

                            if (entry.note.isNotBlank()) {
                                Spacer(Modifier.height(16.dp))

                                Text(
                                    text = entry.note,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val buttonShape = RoundedCornerShape(8.dp)

                        Button(
                            onClick = {
                                viewModel.startEdit(entry)
                                navController.popBackStack()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = buttonShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E3A47)),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Text("Edit", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                viewModel.deleteEntry(entry.id)
                                navController.popBackStack()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = buttonShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Text("Delete", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                viewModel.showAddEntryDialog("steps", entry.copy(id = 0L))
                                navController.popBackStack()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = buttonShape,
                            colors = ButtonDefaults.buttonColors(containerColor = contentColor),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Text("Add Similar", color = cardColor, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                item {
                    ChartCard(
                        title = "Progress chart",
                        entries = state.entries.filter {
                            when {
                                entry.steps > 0 -> it.steps > 0
                                entry.distanceMeters > 0 -> it.distanceMeters > 0
                                else -> it.activeEnergyKcal > 0
                            }
                        },
                        metricSelector = {
                            when {
                                entry.steps > 0 -> it.steps
                                entry.distanceMeters > 0 -> it.distanceMeters
                                else -> it.activeEnergyKcal
                            }
                        },
                        cardColor = cardColor,
                        onExport = { viewModel.exportCsv() }
                    )
                }

                item {
                    ChartCard(
                        title = "All-time statistics",
                        entries = viewModel.allEntries.value,
                        metricSelector = {
                            when {
                                entry.steps > 0 -> it.steps
                                entry.distanceMeters > 0 -> it.distanceMeters
                                else -> it.activeEnergyKcal
                            }
                        },
                        cardColor = cardColor,
                        onExport = { viewModel.exportCsv() }
                    )
                }
            }
        }
    }
}

@Composable
fun ChartCard(
    title: String,
    entries: List<ActivitySample>,
    metricSelector: (ActivitySample) -> Number,
    cardColor: Color,
    onExport: () -> Unit
) {
    val neonColor = Color(0xFFBFFF00)
    val backgroundColor = Color(0x801C2A3A)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, color = Color.White, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))

            val points = entries.mapIndexed { index, it ->
                index.toFloat() to metricSelector(it).toFloat()
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(98.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(backgroundColor)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {

                    if (points.isEmpty()) return@Canvas

                    val safePoints = if (points.size == 1) {
                        val (x, y) = points[0]
                        listOf(
                            x to (y * 0.92f),
                            x + 1f to (y * 0.75f),
                            x + 2f to (y * 0.90f)
                        )
                    } else points

                    val values = safePoints.map { it.second }

                    val maxY = (values.maxOrNull() ?: 0f).let { if (it == 0f) 1f else it }

                    val minY = (values.minOrNull() ?: 0f)

                    val stepX = size.width / (safePoints.size - 1)
                    val smoothness = 0.33f

                    val smoothed = safePoints.mapIndexed { index, (_, yRaw) ->
                        val yNorm = (yRaw - minY) / maxY
                        Offset(
                            x = index * stepX,
                            y = size.height - yNorm * (size.height * 0.85f)
                        )
                    }

                    val path = Path()
                    val fill = Path()

                    path.moveTo(smoothed.first().x, smoothed.first().y)
                    fill.moveTo(smoothed.first().x, smoothed.first().y)

                    for (i in 1 until smoothed.size) {
                        val prev = smoothed[i - 1]
                        val curr = smoothed[i]

                        val c1 = Offset(
                            x = prev.x + (curr.x - prev.x) * smoothness,
                            y = prev.y
                        )
                        val c2 = Offset(
                            x = curr.x - (curr.x - prev.x) * smoothness,
                            y = curr.y
                        )

                        path.cubicTo(c1.x, c1.y, c2.x, c2.y, curr.x, curr.y)
                        fill.cubicTo(c1.x, c1.y, c2.x, c2.y, curr.x, curr.y)
                    }

                    fill.lineTo(size.width, size.height)
                    fill.lineTo(0f, size.height)
                    fill.close()

                    drawPath(
                        path = path,
                        color = neonColor.copy(alpha = 0.45f),
                        style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                    )

                    drawPath(
                        path = path,
                        color = neonColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    drawPath(
                        path = fill,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                neonColor.copy(alpha = 0.30f),
                                neonColor.copy(alpha = 0.10f),
                                Color.Transparent
                            )
                        )
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onExport,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Export CSV", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}