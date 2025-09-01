package org.app.billions.ui.screens.journa

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate

@Composable
fun ConfettiOverlay() {
    val particles = remember { List(200) { ConfettiParticle() } }
    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val yAnim = remember { Animatable(0f) }
            val xOffset = remember { particle.startX }

            LaunchedEffect(Unit) {
                yAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = (1200..3000).random(),
                            easing = LinearEasing
                        )
                    )
                )
            }

            Canvas(Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val t = yAnim.value

                val x = (xOffset * w + particle.drift * t * w) % w
                val y = ((particle.startY + t * particle.speed) % 1f) * h
                val rotation = 360f * t * particle.spin

                rotate(rotation, pivot = Offset(x, y)) {
                    drawCircle(
                        color = particle.color.copy(alpha = particle.alpha),
                        radius = particle.size,
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}

private data class ConfettiParticle(
    val startX: Float = kotlin.random.Random.nextFloat(),
    val startY: Float = kotlin.random.Random.nextFloat(),
    val drift: Float = (kotlin.random.Random.nextFloat() - 0.5f) * 0.6f,
    val speed: Float = 0.5f + kotlin.random.Random.nextFloat() * 1.5f,
    val size: Float = 4f + kotlin.random.Random.nextFloat() * 8f,
    val alpha: Float = 0.5f + kotlin.random.Random.nextFloat() * 0.5f,
    val spin: Float = 0.5f + kotlin.random.Random.nextFloat() * 2f,
    val color: Color = Color(
        red = 0.5f + kotlin.random.Random.nextFloat() * 0.5f,
        green = 0.5f + kotlin.random.Random.nextFloat() * 0.5f,
        blue = 0.5f + kotlin.random.Random.nextFloat() * 0.5f
    )
)
