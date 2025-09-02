package org.app.billions.ui.screens.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import billions.composeapp.generated.resources.Res
import billions.composeapp.generated.resources.app_logo
import org.jetbrains.compose.resources.painterResource

@Composable
fun EmptyView(message: String, onAddEntry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.app_logo),
            contentDescription = "Monocle Guy",
            modifier = Modifier.size(150.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(message, color = Color.White, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        PrimaryButton("Add First Entry") { onAddEntry() }
    }
}

