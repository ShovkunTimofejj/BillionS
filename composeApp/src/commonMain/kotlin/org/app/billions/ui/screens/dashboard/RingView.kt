package org.app.billions.ui.screens.dashboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun RingView(
    progress: Float,
    label: String,
    color: Color,
    size: Dp = 80.dp,
    goalReached: Boolean = false,
    labelColor: Color = Color.White,
    labelColorProgress: Color = Color.White
) {
    var animatedProgress by remember { mutableStateOf(0f) }
    val progressAnimated by animateFloatAsState(
        targetValue = animatedProgress,
        animationSpec = tween(durationMillis = 1500, easing = LinearOutSlowInEasing)
    )

    val glow = remember { Animatable(0f) }

    LaunchedEffect(goalReached) {
        if (goalReached) {
            glow.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
            glow.animateTo(0f, animationSpec = tween(500))
        }
    }

    LaunchedEffect(progress) {
        animatedProgress = 0f
        delay(200)
        animatedProgress = progress.coerceIn(0f, 1f)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(size)) {
            Canvas(modifier = Modifier.size(size)) {
                drawCircle(Color.Gray.copy(alpha = 0.2f), style = Stroke(12f))
                drawArc(
                    color,
                    startAngle = -90f,
                    sweepAngle = 360 * progressAnimated,
                    useCenter = false,
                    style = Stroke(12f, cap = StrokeCap.Round)
                )
                if (glow.value > 0f) {
                    drawCircle(color.copy(alpha = glow.value), radius = size.toPx() / 2)
                }
            }

            Text(
                text = "${(progressAnimated * 100).toInt()}%",
                color = labelColorProgress,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = label,
            color = labelColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}