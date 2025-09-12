package org.app.billions.ui.screens.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import billions.composeapp.generated.resources.Res
import billions.composeapp.generated.resources.logo_default
import billions.composeapp.generated.resources.logo_graphite_gold
import billions.composeapp.generated.resources.logo_neon_coral
import billions.composeapp.generated.resources.logo_royal_blue
import org.app.billions.data.model.Theme
import org.jetbrains.compose.resources.painterResource

@Composable
fun EmptyView(
    message: String,
    onAddEntry: () -> Unit,
    currentTheme: Theme?
) {
    val buttonColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFFB6FE03)
        "neon_coral" -> Color(0xFFFF2C52)
        "royal_blue" -> Color(0xFF699BFF)
        "graphite_gold" -> Color(0xFFFFD700)
        else -> Color(0xFFB6FE03)
    }

    val logoRes = when (currentTheme?.id) {
        "dark_lime" -> Res.drawable.logo_default
        "neon_coral" -> Res.drawable.logo_neon_coral
        "royal_blue" -> Res.drawable.logo_royal_blue
        "graphite_gold" -> Res.drawable.logo_graphite_gold
        else -> Res.drawable.logo_default
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(logoRes),
            contentDescription = "Logo",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            message,
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        PrimaryButton(
            text = "Add First Entry",
            onClick = onAddEntry,
            backgroundColor = buttonColor.copy(alpha = 0.2f),
            textColor = buttonColor
        )
    }
}



