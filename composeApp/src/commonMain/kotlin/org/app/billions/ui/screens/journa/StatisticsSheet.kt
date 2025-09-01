package org.app.billions.ui.screens.journa

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import org.app.billions.data.model.ActivitySample
import kotlin.math.pow
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsSheet(entries: List<ActivitySample>, metric: MetricType, onClose: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val points = remember(entries, metric) { buildSeries(entries, metric) }

    ModalBottomSheet(onDismissRequest = onClose, sheetState = sheetState) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text("Statistics", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            LineChart(points = points, height = 220.dp)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Min: ${points.minOfOrNull { it.second } ?: 0.0}")
                Text("Max: ${points.maxOfOrNull { it.second } ?: 0.0}")
                val avg = if (points.isNotEmpty()) points.map { it.second }.average() else 0.0
                Text("Avg: ${avg.format(1)}")
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) { Text("Close") }
            Spacer(Modifier.height(12.dp))
        }
    }
}

private fun buildSeries(entries: List<ActivitySample>, metric: MetricType): List<Pair<LocalDate, Double>> {
    val grouped = entries.groupBy { it.date.date }
    val map = grouped.mapValues { (_, list) ->
        when (metric) {
            MetricType.Steps -> list.sumOf { it.steps }.toDouble()
            MetricType.Distance -> list.sumOf { it.distanceMeters }
            MetricType.Calories -> list.sumOf { it.activeEnergyKcal }
        }
    }
    return map.entries.sortedBy { it.key }.map { it.key to it.value }
}

@Composable
fun LineChart(points: List<Pair<LocalDate, Double>>, height: Dp) {
    if (points.isEmpty()) {
        Box(
            Modifier.fillMaxWidth().height(height),
            contentAlignment = Alignment.Center
        ) {
            Text("No data")
        }
        return
    }

    val padding = 24.dp
    val minY = points.minOf { it.second }
    val maxY = points.maxOf { it.second }.let { if (it == minY) it + 1 else it }

    val lineColor = MaterialTheme.colorScheme.primary

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .padding(horizontal = padding)
    ) {
        val w = size.width
        val h = size.height

        // axes
        drawLine(Color.LightGray, Offset(0f, h), Offset(w, h), strokeWidth = 2f)
        drawLine(Color.LightGray, Offset(0f, 0f), Offset(0f, h), strokeWidth = 2f)

        val stepX = w / (points.size - 1).coerceAtLeast(1)
        fun mapY(v: Double): Float = (h - (v - minY) / (maxY - minY) * h).toFloat()

        var prev: Offset? = null
        points.forEachIndexed { i, p ->
            val x = i * stepX
            val y = mapY(p.second)
            val pt = Offset(x, y)
            if (prev != null) drawLine(lineColor, prev!!, pt, strokeWidth = 4f)
            prev = pt
            drawCircle(color = lineColor, radius = 5f, center = pt)
        }
    }
}

fun Double.format(decimals: Int): String {
    val factor = 10.0.pow(decimals)
    val rounded = round(this * factor) / factor
    return rounded.toString()
}