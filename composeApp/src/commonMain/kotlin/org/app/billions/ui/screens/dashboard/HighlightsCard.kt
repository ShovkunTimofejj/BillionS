package org.app.billions.ui.screens.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.billions.data.model.ActivitySample

@Composable
fun HighlightsCard(
    entries: List<ActivitySample>,
    contentColor: Color,
    cardBackground: Color,
    onClick: () -> Unit
) {
    val bestDay = entries.maxByOrNull { it.steps }?.steps ?: 0
    val weeklyTarget = (entries.sumOf { it.steps } / 70000.0 * 100).toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Highlights", color = contentColor, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            Text("Best day this week: $bestDay steps", color = contentColor)
            Text("X% to weekly target: $weeklyTarget%", color = contentColor)
            Spacer(Modifier.height(8.dp))
            PrimaryButton(
                text = "View Comparison",
                backgroundColor = contentColor.copy(alpha = 0.2f),
                textColor = contentColor,
                onClick = onClick
            )
        }
    }
}