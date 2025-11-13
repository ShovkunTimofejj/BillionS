package org.app.billions.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun OdometerView(
    steps: Long = 0L,
    distanceMeters: Double = 0.0,
    calories: Double = 0.0,
    color: Color
) {
    val value = when {
        steps > 0 -> steps.toString()
        distanceMeters > 0 -> (distanceMeters / 1000).roundToInt().toString()
        calories > 0 -> calories.roundToInt().toString()
        else -> "0"
    }

    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .horizontalScroll(scrollState)
            .background(Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        value.forEach { digit ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .size(width = 34.dp, height = 46.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF0C0C0C))
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(6.dp),
                        ambientColor = Color.Black.copy(alpha = 0.8f),
                        spotColor = Color.Black.copy(alpha = 0.8f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = digit.toString(),
                    color = color,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
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
