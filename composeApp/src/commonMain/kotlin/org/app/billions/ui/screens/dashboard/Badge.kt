package org.app.billions.ui.screens.dashboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Badge(level: String, size: Dp = 48.dp, animate: Boolean = false) {
    val color = when(level) {
        "bronze" -> Color(0xFFCD7F32)
        "silver" -> Color(0xFFC0C0C0)
        "gold" -> Color(0xFFFFD700)
        else -> Color.Gray
    }

    val scale = remember { Animatable(1f) }

    LaunchedEffect(animate) {
        if (animate) {
            scale.animateTo(1.2f, animationSpec = tween(300))
            scale.animateTo(1f, animationSpec = tween(300))
        }
    }

    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer(scaleX = scale.value, scaleY = scale.value)
            .background(color, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(level.first().uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
    }
}

