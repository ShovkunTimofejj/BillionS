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

@Composable
fun ConfettiOverlay(currentTheme: Theme?) {
    val colors = remember(currentTheme) {
        when (currentTheme?.id) {
            "dark_lime" -> listOf(
                Color(0xFFB6FE03),
                Color(0xFF9DFF57),
                Color(0xFFD4FF80),
                Color(0xFF76FF00)
            )
            "neon_coral" -> listOf(
                Color(0xFFFF2C52),
                Color(0xFFFF5F7A),
                Color(0xFFFF8FA0),
                Color(0xFFFFB5C0)
            )
            "royal_blue" -> listOf(
                Color(0xFF699BFF),
                Color(0xFF81B4FF),
                Color(0xFF4F7DFF),
                Color(0xFFA7C5FF)
            )
            "graphite_gold" -> listOf(
                Color(0xFFFFD700),
                Color(0xFFFFC000),
                Color(0xFFFFE066),
                Color(0xFFFFF099)
            )
            else -> listOf(
                Color(0xFFB6FE03),
                Color(0xFF00FFAA),
                Color(0xFF66FF99),
                Color(0xFFAAFF66)
            )
        }
    }

    val particles = remember {
        List(150) { ConfettiParticle(color = colors.random()) }
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
    val startX: Float = kotlin.random.Random.nextFloat(),
    val startY: Float = kotlin.random.Random.nextFloat(),
    val drift: Float = (kotlin.random.Random.nextFloat() - 0.5f) * 0.4f,
    val speed: Float = 0.3f + kotlin.random.Random.nextFloat() * 0.7f,
    val width: Float = 8f + kotlin.random.Random.nextFloat() * 6f,
    val height: Float = 8f + kotlin.random.Random.nextFloat() * 6f,
    val alpha: Float = 0.6f + kotlin.random.Random.nextFloat() * 0.4f,
    val spin: Float = 0.3f + kotlin.random.Random.nextFloat() * 1.2f,
    val color: Color,
    val type: ConfettiType = if (kotlin.random.Random.nextBoolean()) ConfettiType.RECTANGLE else ConfettiType.CURLY
)

private enum class ConfettiType { RECTANGLE, CURLY }