package org.app.billions.ui.screens.journa

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.app.billions.data.model.ActivitySample

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import billions.composeapp.generated.resources.Res
import billions.composeapp.generated.resources.ic_calories_dark_lime
import billions.composeapp.generated.resources.ic_calories_graphite_gold
import billions.composeapp.generated.resources.ic_calories_neon_coral
import billions.composeapp.generated.resources.ic_calories_royal_blue
import billions.composeapp.generated.resources.ic_distance_dark_lime
import billions.composeapp.generated.resources.ic_distance_graphite_gold
import billions.composeapp.generated.resources.ic_distance_neon_coral
import billions.composeapp.generated.resources.ic_distance_royal_blue
import billions.composeapp.generated.resources.ic_steps_dark_lime
import billions.composeapp.generated.resources.ic_steps_graphite_gold
import billions.composeapp.generated.resources.ic_steps_neon_coral
import billions.composeapp.generated.resources.ic_steps_royal_blue
import kotlinx.coroutines.launch
import org.app.billions.data.model.Theme
import org.jetbrains.compose.resources.painterResource
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt

@Composable
fun JournalItem(
    entry: ActivitySample,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    currentTheme: Theme?,
    cardColor: Color,
    contentColor: Color,
    swipeThreshold: Float = 180f
) {
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }

    val stepsIconRes = when (currentTheme?.id) {
        "dark_lime" -> Res.drawable.ic_steps_dark_lime
        "neon_coral" -> Res.drawable.ic_steps_neon_coral
        "royal_blue" -> Res.drawable.ic_steps_royal_blue
        "graphite_gold" -> Res.drawable.ic_steps_graphite_gold
        else -> Res.drawable.ic_steps_dark_lime
    }

    val distanceIconRes = when (currentTheme?.id) {
        "dark_lime" -> Res.drawable.ic_distance_dark_lime
        "neon_coral" -> Res.drawable.ic_distance_neon_coral
        "royal_blue" -> Res.drawable.ic_distance_royal_blue
        "graphite_gold" -> Res.drawable.ic_distance_graphite_gold
        else -> Res.drawable.ic_distance_dark_lime
    }

    val caloriesIconRes = when (currentTheme?.id) {
        "dark_lime" -> Res.drawable.ic_calories_dark_lime
        "neon_coral" -> Res.drawable.ic_calories_neon_coral
        "royal_blue" -> Res.drawable.ic_calories_royal_blue
        "graphite_gold" -> Res.drawable.ic_calories_graphite_gold
        else -> Res.drawable.ic_calories_dark_lime
    }

    val accentColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFFB6FE03)
        "neon_coral" -> Color(0xFFFF2C52)
        "royal_blue" -> Color(0xFF699BFF)
        "graphite_gold" -> Color(0xFFFFD700)
        else -> Color(0xFFB6FE03)
    }

    val editColor = Color(0xFF2E3A47)
    val deleteColor = Color(0xFFD32F2F)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(horizontal = 18.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(20.dp))
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        scope.launch {
                            when {
                                offsetX.value < -swipeThreshold -> {
                                    offsetX.animateTo(-400f, tween(250))
                                    onDelete()
                                    offsetX.animateTo(0f, tween(200))
                                }
                                offsetX.value > swipeThreshold -> {
                                    offsetX.animateTo(400f, tween(250))
                                    onEdit()
                                    offsetX.animateTo(0f, tween(200))
                                }
                                else -> offsetX.animateTo(0f, tween(200))
                            }
                        }
                    },
                    onHorizontalDrag = { _, drag ->
                        scope.launch {
                            offsetX.snapTo((offsetX.value + drag).coerceIn(-400f, 400f))
                        }
                    }
                )
            }
    ) {
        Row(
            Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(20.dp))
        ) {
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(editColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Edit",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(deleteColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Delete",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }

        Card(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .fillMaxSize(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .clickable(onClick = onClick)
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = entry.date.date.toString(),
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )

                Spacer(Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val iconPainter = when {
                        entry.steps > 0 -> painterResource(stepsIconRes)
                        entry.distanceMeters > 0 -> painterResource(distanceIconRes)
                        else -> painterResource(caloriesIconRes)
                    }

                    Image(
                        painter = iconPainter,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(Modifier.width(18.dp))

                    Column {
                        Text(
                            text = when {
                                entry.steps > 0 -> entry.steps.asSteps()
                                entry.distanceMeters > 0 -> entry.distanceMeters.asKm()
                                else -> entry.activeEnergyKcal.asKcal()
                            },
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (entry.note.isNotBlank()) {
                            Text(
                                text = entry.note,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
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