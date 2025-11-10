package org.app.billions.ui.screens.journa

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
        "dark_lime" -> Color(0xFF1C2A3A)
        "neon_coral" -> Color(0xFF431C2E)
        "royal_blue" -> Color(0xFF1D3B5C)
        "graphite_gold" -> Color(0xFF383737)
        else -> Color(0xFF1C2A3A)
    }

    val chipBackgroundColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0x8010FF10)
        "neon_coral" -> Color(0x80FF6070)
        "royal_blue" -> Color(0x8069BFFF)
        "graphite_gold" -> Color(0x80FFD700)
        else -> Color(0x8010FF10)
    }

    val saveButtonColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFFB6FE03)
        "neon_coral" -> Color(0xFFFF2C52)
        "royal_blue" -> Color(0xFF699BFF)
        "graphite_gold" -> Color(0xFFFFD700)
        else -> Color(0xFFB6FE03)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Filters",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier
                    .background(chipBackgroundColor, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterType.values().forEach { f ->
                    FilterChip(
                        selected = currentFilter == f,
                        onClick = {
                            onSelectFilter(f)
                            onDismiss()
                        },
                        label = {
                            Text(
                                f.name,
                                color = if (currentFilter == f) Color.White else Color.Black
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = chipBackgroundColor.copy(alpha = 0.4f),
                            labelColor = Color.Black,
                            selectedContainerColor = saveButtonColor,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Text(
                "Metric",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier
                    .background(chipBackgroundColor, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricType.values().forEach { m ->
                    FilterChip(
                        selected = currentMetric == m,
                        onClick = {
                            onSelectMetric(m)
                            onDismiss()
                        },
                        label = {
                            Text(
                                m.name,
                                color = if (currentMetric == m) Color.White else Color.Black
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = chipBackgroundColor.copy(alpha = 0.4f),
                            labelColor = Color.Black,
                            selectedContainerColor = saveButtonColor,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = saveButtonColor)
            ) {
                Text("Done", color = Color.Black)
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}