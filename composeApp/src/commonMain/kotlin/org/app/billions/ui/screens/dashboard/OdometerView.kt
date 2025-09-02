package org.app.billions.ui.screens.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun OdometerView(
    steps: Long = 0L,
    distanceMeters: Double = 0.0,
    calories: Double = 0.0,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (steps > 0) {
            Text(steps.asSteps(), color = color, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        }
        if (distanceMeters > 0) {
            Text(distanceMeters.asKm(), color = color, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        }
        if (calories > 0) {
            Text(calories.asKcal(), color = color, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        }
    }
}

fun Double.asKm(): String {
    val km = this / 1000.0
    val rounded = (km * 100).roundToInt() / 100.0
    return "$rounded km"
}

fun Double.asKcal(): String = "${this.roundToInt()} kcal"
fun Long.asSteps(): String = "$this steps"
