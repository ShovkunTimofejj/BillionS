package org.app.billions.ui.screens.dashboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RingView(progress: Float, label: String, color: Color, size: Dp = 80.dp, goalReached: Boolean = false) {
    val glow = remember { Animatable(0f) }

    LaunchedEffect(goalReached) {
        if (goalReached) {
            glow.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
            glow.animateTo(0f, animationSpec = tween(500))
        }
    }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(size)) {
        Canvas(modifier = Modifier.size(size)) {
            drawCircle(Color.Gray.copy(alpha = 0.2f), style = Stroke(16f))
            drawArc(
                color,
                startAngle = -90f,
                sweepAngle = 360 * progress,
                useCenter = false,
                style = Stroke(16f, cap = StrokeCap.Round)
            )
            if (glow.value > 0f) {
                drawCircle(color.copy(alpha = glow.value), radius = size.toPx() / 2)
            }
        }
        Text(label, color = color, fontSize = 14.sp)
    }
}

