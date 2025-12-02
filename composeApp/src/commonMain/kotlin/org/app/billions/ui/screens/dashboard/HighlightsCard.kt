package org.app.billions.ui.screens.dashboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import billions.composeapp.generated.resources.Res
import billions.composeapp.generated.resources.ic_arrow_right
import org.app.billions.data.model.ActivitySample
import org.app.billions.ui.screens.Screen
import org.app.billions.ui.screens.viewModel.JournalViewModel
import org.jetbrains.compose.resources.painterResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun HighlightsCard(
    viewModel: JournalViewModel,
    navController: NavController,
    contentColor: Color,
    cardBackground: Color
) {

    val goals = viewModel.dailyGoals.value
    val allEntries by viewModel.allEntries
    LaunchedEffect(Unit) {
        viewModel.loadAllEntries()
    }
    val entries = allEntries


    if (entries.isEmpty()) return

    val groupedByDate = remember(entries) {
        entries.groupBy { it.date.date }
    }

    val bestDayEntry = groupedByDate.maxByOrNull { (_, list) -> list.sumOf { it.steps } }
    val bestDayName = bestDayEntry?.key
        ?.dayOfWeek
        ?.name
        ?.lowercase()
        ?.replaceFirstChar { it.titlecase() } ?: "Unknown"

    val bestDaySteps = bestDayEntry?.value?.sumOf { it.steps } ?: 0L
    val bestDayProgress = (bestDaySteps.toFloat() / goals.stepGoal).coerceIn(0f, 1f)

    val totalSteps = entries.sumOf { it.steps }.toFloat()
    val weeklyTargetProgress = (totalSteps / (goals.stepGoal * 7f)).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Best day this week",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clickable { navController.navigate(Screen.ComparisonScreen.route) },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_arrow_right),
                    contentDescription = "Open comparison",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HighlightItem(
                title = bestDayName,
                percent = (bestDayProgress * 100).toInt(),
                color = contentColor,
                bgColor = cardBackground,
                modifier = Modifier.weight(1f)
            )
            HighlightItem(
                title = "To weekly target",
                percent = (weeklyTargetProgress * 100).toInt(),
                color = contentColor,
                bgColor = cardBackground,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
@Composable
fun HighlightItem(
    title: String,
    percent: Int,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(percent) {
        animatedProgress.animateTo(
            targetValue = percent / 100f,
            animationSpec = tween(durationMillis = 1400, easing = LinearOutSlowInEasing)
        )
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        modifier = modifier.aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(6.dp))

            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
                Canvas(modifier = Modifier.size(120.dp)) {
                    val strokeWidth = 10.dp.toPx()
                    val spacing = strokeWidth * 0.6f
                    val radiusBase = size.minDimension / 2 - strokeWidth / 2

                    repeat(3) { index ->
                        val radius = radiusBase - index * (strokeWidth + spacing)

                        drawArc(
                            color = color.copy(alpha = 0.2f),
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            size = Size(radius * 2, radius * 2),
                            topLeft = Offset(center.x - radius, center.y - radius),
                            style = Stroke(strokeWidth)
                        )

                        drawArc(
                            color = color,
                            startAngle = -90f,
                            sweepAngle = 360f * animatedProgress.value,
                            useCenter = false,
                            size = Size(radius * 2, radius * 2),
                            topLeft = Offset(center.x - radius, center.y - radius),
                            style = Stroke(strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                }

                Text(
                    text = "${percent}%",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}