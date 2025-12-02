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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import org.app.billions.data.model.Theme
import kotlin.random.Random

@Composable
fun ConfettiOverlay(currentTheme: Theme?) {
    val colors = Color(0xFFF6E19F)

    val particles = remember {
        List(150) { ConfettiParticle.random(colors) }
    }

    Box(Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val yAnim = remember { Animatable(0f) }

            LaunchedEffect(Unit) {
                yAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = (2000..4000).random(),
                        easing = LinearEasing
                    )
                )
            }

            Canvas(Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val t = yAnim.value

                val x = (particle.startX * w + particle.drift * t * w) % w
                val y = particle.startY * h + t * particle.speed * h

                val rotation = 360f * t * particle.spin

                rotate(rotation, pivot = Offset(x, y)) {
                    when (particle.type) {
                        ConfettiType.RECTANGLE -> {
                            drawRect(
                                color = particle.color.copy(alpha = particle.alpha),
                                topLeft = Offset(x - particle.width / 2, y - particle.height / 2),
                                size = Size(particle.width, particle.height)
                            )
                        }
                        ConfettiType.CURLY -> {
                            val path = Path().apply {
                                moveTo(x, y)
                                quadraticBezierTo(
                                    x + particle.width / 2, y + particle.height / 2,
                                    x + particle.width, y
                                )
                            }
                            drawPath(
                                path = path,
                                color = particle.color.copy(alpha = particle.alpha),
                                style = Stroke(width = 2f)
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class ConfettiParticle(
    val startX: Float = Random.nextFloat(),
    val startY: Float = Random.nextFloat(),
    val drift: Float = (Random.nextFloat() - 0.5f) * 0.4f,
    val speed: Float = 0.2f + Random.nextFloat() * 0.5f,
    val width: Float,
    val height: Float,
    val alpha: Float = 0.8f,
    val spin: Float = 0.5f + Random.nextFloat() * 1.2f,
    val color: Color,
    val type: ConfettiType
) {
    companion object {
        fun random(color: Color): ConfettiParticle {
            val type = if (Random.nextFloat() < 0.75f) ConfettiType.RECTANGLE else ConfettiType.CURLY

            return when (type) {
                ConfettiType.RECTANGLE -> ConfettiParticle(
                    color = color,
                    type = type,
                    width = 20f + Random.nextFloat() * 15f,
                    height = 20f + Random.nextFloat() * 15f
                )

                ConfettiType.CURLY -> ConfettiParticle(
                    color = color,
                    type = type,
                    width = 25f + Random.nextFloat() * 20f,
                    height = 60f + Random.nextFloat() * 40f
                )
            }
        }
    }
}

private enum class ConfettiType { RECTANGLE, CURLY }