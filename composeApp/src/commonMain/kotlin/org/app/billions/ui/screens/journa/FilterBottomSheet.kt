package org.app.billions.ui.screens.journa

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.billions.data.model.Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    currentFilter: FilterType,
    currentMetric: MetricType,
    currentTheme: Theme?,
    onSelectFilter: (FilterType) -> Unit,
    onSelectMetric: (MetricType) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val dialogBackgroundColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFF1F2D1E)
        "neon_coral" -> Color(0xFF2A151E)
        "royal_blue" -> Color(0xFF0C0E3D)
        "graphite_gold" -> Color(0xFF470C0C)
        else -> Color(0xFF1F2D1E)
    }

    val chipBackgroundColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFF334A32)
        "neon_coral" -> Color(0xFF4B2637)
        "royal_blue" -> Color(0xFF1C193C)
        "graphite_gold" -> Color(0xFF3C1919)
        else -> Color(0xFF334A32)
    }

    val saveButtonColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFFB6FE03)
        "neon_coral" -> Color(0xFFFF2C52)
        "royal_blue" -> Color(0xFF699BFF)
        "graphite_gold" -> Color(0xFFFFD700)
        else -> Color(0xFFB6FE03)
    }

    val gradientBorder = Brush.linearGradient(
        colors = listOf(
            Color(0xFFF6E19F),
            Color(0xFF90845D)
        )
    )

    @Composable
    fun FilterItem(
        text: String,
        selected: Boolean,
        onClick: () -> Unit
    ) {
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (selected)
                        saveButtonColor.copy(alpha = 0.25f)
                    else
                        chipBackgroundColor
                )
                .then(
                    if (selected)
                        Modifier.border(
                            width = 2.dp,
                            brush = gradientBorder,
                            shape = RoundedCornerShape(8.dp)
                        )
                    else Modifier
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 16.sp,
                color = if (selected) Color(0xFFF6E19F) else Color.White
            )
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = dialogBackgroundColor,
        tonalElevation = 8.dp
    ) {

        Column(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Text(
                "Filters",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Text(
                "Period:",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterType.values().forEach { f ->
                    FilterItem(
                        text = f.name.replace("_", " "),
                        selected = currentFilter == f,
                        onClick = { onSelectFilter(f) }
                    )
                }
            }

            Text(
                "Stats:",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricType.values().forEach { m ->
                    FilterItem(
                        text = m.name,
                        selected = currentMetric == m,
                        onClick = { onSelectMetric(m) }
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        brush = gradientBorder,
                        shape = RoundedCornerShape(10.dp)
                    ),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    "Done",
                    color = Color(0xFFF6E19F),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(Modifier.height(10.dp))
        }
    }
}
