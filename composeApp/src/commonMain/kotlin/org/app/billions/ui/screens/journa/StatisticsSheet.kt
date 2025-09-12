package org.app.billions.ui.screens.journa

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import org.app.billions.data.model.ActivitySample
import org.app.billions.data.model.Theme
import kotlin.math.pow
import kotlin.math.round
import androidx.compose.ui.graphics.drawscope.Stroke


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsSheet(
    entries: List<ActivitySample>,
    metric: MetricType,
    currentTheme: Theme?,
    onClose: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val points = remember(entries, metric) { buildSeries(entries, metric) }

    val dialogBackgroundColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFF1C2A3A)
        "neon_coral" -> Color(0xFF431C2E)
        "royal_blue" -> Color(0xFF1D3B5C)
        "graphite_gold" -> Color(0xFF383737)
        else -> Color(0xFF1C2A3A)
    }

    val fieldBackgroundColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0x80102020)
        "neon_coral" -> Color(0x801A0F14)
        "royal_blue" -> Color(0x800B1C3D)
        "graphite_gold" -> Color(0x80121212)
        else -> Color(0x80102020)
    }

    val saveButtonColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFFB6FE03)
        "neon_coral" -> Color(0xFFD71C3F)
        "royal_blue" -> Color(0xFF447ACC)
        "graphite_gold" -> Color(0xFFC9A100)
        else -> Color(0xFFB6FE03)
    }

    val textColor = Color.White
    val lineColor = saveButtonColor

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = dialogBackgroundColor,
        tonalElevation = 8.dp
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Statistics",
                style = MaterialTheme.typography.titleLarge,
                color = textColor,
                modifier = Modifier
                    .background(fieldBackgroundColor, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            Spacer(Modifier.height(8.dp))

            LineChart(points = points, height = 220.dp, lineColor = lineColor, currentTheme = currentTheme)

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Min: ${points.minOfOrNull { it.second } ?: 0.0}", color = textColor)
                Text("Max: ${points.maxOfOrNull { it.second } ?: 0.0}", color = textColor)
                val avg = if (points.isNotEmpty()) points.map { it.second }.average() else 0.0
                Text("Avg: ${avg.format(1)}", color = textColor)
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = saveButtonColor)
            ) {
                Text("Close", color = textColor)
            }

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
fun LineChart(
    points: List<Pair<LocalDate, Double>>,
    height: Dp,
    lineColor: Color,
    currentTheme: Theme?
) {
    if (points.isEmpty()) {
        Box(
            Modifier.fillMaxWidth().height(height),
            contentAlignment = Alignment.Center
        ) {
            Text("No data", color = Color.White)
        }
        return
    }

    val padding = 24.dp
    val minY = points.minOf { it.second }
    val maxY = points.maxOf { it.second }.let { if (it == minY) it + 1 else it }

    val axesColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFF88FF33)
        "neon_coral" -> Color(0xFFFF7A8A)
        "royal_blue" -> Color(0xFF8DBEFF)
        "graphite_gold" -> Color(0xFFFFC700)
        else -> Color(0xFFFFFFFF)
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .padding(horizontal = padding)
    ) {
        val w = size.width
        val h = size.height

        drawLine(axesColor.copy(alpha = 0.5f), Offset(0f, h), Offset(w, h), strokeWidth = 2f)
        drawLine(axesColor.copy(alpha = 0.5f), Offset(0f, 0f), Offset(0f, h), strokeWidth = 2f)

        val stepX = w / (points.size - 1).coerceAtLeast(1)
        fun mapY(v: Double): Float = (h - (v - minY) / (maxY - minY) * h).toFloat()

        val path = Path()
        points.forEachIndexed { i, p ->
            val x = i * stepX
            val y = mapY(p.second)
            if (i == 0) path.moveTo(x, y)
            else {
                val prevX = (i - 1) * stepX
                val prevY = mapY(points[i - 1].second)
                val cx1 = prevX + (x - prevX) / 2
                val cy1 = prevY
                val cx2 = prevX + (x - prevX) / 2
                val cy2 = y
                path.cubicTo(cx1, cy1, cx2, cy2, x, y)
            }
        }

        val fillPath = Path().apply {
            moveTo(0f, h)
            points.forEachIndexed { i, p ->
                val x = i * stepX
                val y = mapY(p.second)
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            lineTo(w, h)
            close()
        }
        drawPath(fillPath, color = lineColor.copy(alpha = 0.2f))

        drawPath(path, color = lineColor, style = Stroke(width = 4f, cap = StrokeCap.Round))

        points.forEachIndexed { i, p ->
            val x = i * stepX
            val y = mapY(p.second)
            drawCircle(color = lineColor, radius = 5f, center = Offset(x, y))
        }
    }
}
fun Double.format(decimals: Int): String {
    val factor = 10.0.pow(decimals)
    val rounded = round(this * factor) / factor
    return rounded.toString()
}