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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
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
    cardColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    swipeThreshold: Float = 140f
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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        scope.launch {
                            when {
                                offsetX.value < -swipeThreshold -> {
                                    offsetX.animateTo(-300f, tween(250))
                                    onDelete()
                                    offsetX.animateTo(0f, tween(200))
                                }
                                offsetX.value > swipeThreshold -> {
                                    offsetX.animateTo(300f, tween(250))
                                    onEdit()
                                    offsetX.animateTo(0f, tween(200))
                                }
                                else -> offsetX.animateTo(0f, tween(200))
                            }
                        }
                    },
                    onHorizontalDrag = { _, drag ->
                        scope.launch {
                            offsetX.snapTo((offsetX.value + drag).coerceIn(-300f, 300f))
                        }
                    }
                )
            }
    ) {
        Row(Modifier.matchParentSize()) {
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.Transparent),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color.White,
                    modifier = Modifier.padding(start = 24.dp)
                )
            }
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.Transparent),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier.padding(end = 24.dp)
                )
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .clickable(onClick = onClick),
            colors = CardDefaults.cardColors(
                containerColor = cardColor,
                contentColor = contentColor
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                val iconPainter = when {
                    entry.steps > 0 -> painterResource(stepsIconRes)
                    entry.distanceMeters > 0 -> painterResource(distanceIconRes)
                    else -> painterResource(caloriesIconRes)
                }

                Image(
                    painter = iconPainter,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        entry.date.date.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor.copy(alpha = 0.7f)
                    )
                    Text(
                        buildString {
                            if (entry.steps > 0) append(entry.steps.asSteps())
                            if (entry.distanceMeters > 0) append(" · ${entry.distanceMeters.asKm()}")
                            if (entry.activeEnergyKcal > 0) append(" · ${entry.activeEnergyKcal.asKcal()}")
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = contentColor
                    )
                    Text(
                        entry.source,
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor.copy(alpha = 0.5f)
                    )
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = contentColor)
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