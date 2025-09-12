package org.app.billions.ui.screens.journa

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.app.billions.data.model.ActivitySample
import org.app.billions.ui.screens.viewModel.SplashScreenViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditEntryDialog(
    editing: ActivitySample?,
    onSave: (ActivitySample) -> Unit,
    onCancel: () -> Unit,
    splashScreenViewModel: SplashScreenViewModel
) {
    val uiState by splashScreenViewModel.uiState.collectAsState()
    val currentTheme = uiState.currentTheme

    val dialogBackgroundColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFF1C2A3A)
        "neon_coral" -> Color(0xFF431C2E)
        "royal_blue" -> Color(0xFF1D3B5C)
        "graphite_gold" -> Color(0xFF383737)
        else -> Color(0xFF1C2A3A)
    }

    val fieldBackgroundColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0x80102020)
        "neon_coral" -> Color(0x801A0F14)
        "royal_blue" -> Color(0x800B1C3D)
        "graphite_gold" -> Color(0x80121212)
        else -> Color(0x80102020)
    }

    val saveButtonColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFFB6FE03)
        "neon_coral" -> Color(0xFFFF2C52)
        "royal_blue" -> Color(0xFF699BFF)
        "graphite_gold" -> Color(0xFFFFD700)
        else -> Color(0xFFB6FE03)
    }

    val cancelButtonColor = Color.White

    var type by remember {
        mutableStateOf(
            when {
                editing == null -> "Steps"
                editing.steps > 0 -> "Steps"
                editing.distanceMeters > 0 -> "Distance"
                else -> "Calories"
            }
        )
    }

    var amount by remember {
        mutableStateOf(
            editing?.let {
                when {
                    it.steps > 0 -> it.steps.toString()
                    it.distanceMeters > 0 -> it.distanceMeters.toString()
                    else -> it.activeEnergyKcal.toString()
                }
            } ?: ""
        )
    }

    var note by remember { mutableStateOf(editing?.note ?: "") }
    var date by remember { mutableStateOf(editing?.date ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(if (editing == null) "Add Entry" else "Edit Entry", color = Color.White) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .background(dialogBackgroundColor, shape = RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Steps", "Distance", "Calories").forEach { t ->
                        FilterChip(
                            selected = type == t,
                            onClick = { type = t },
                            label = { Text(t, color = Color.White, fontSize = 13.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = fieldBackgroundColor,
                                containerColor = fieldBackgroundColor.copy(alpha = 0.6f),
                                selectedLabelColor = Color.White,
                                labelColor = Color.White
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(fieldBackgroundColor, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    BasicTextField(
                        value = amount,
                        onValueChange = {
                            amount = it
                            error = null
                        },
                        textStyle = TextStyle(color = Color.White),
                        singleLine = true,
                        cursorBrush = SolidColor(Color.White),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        decorationBox = { innerTextField ->
                            if (amount.isEmpty()) Text("Amount", color = Color.Gray)
                            innerTextField()
                        }
                    )
                }

                if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(fieldBackgroundColor, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    BasicTextField(
                        value = note,
                        onValueChange = { note = it },
                        textStyle = TextStyle(color = Color.White),
                        singleLine = true,
                        cursorBrush = SolidColor(Color.White),
                        decorationBox = { innerTextField ->
                            if (note.isEmpty()) Text("Note", color = Color.Gray)
                            innerTextField()
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amtNum = amount.replace(",", ".").toDoubleOrNull()
                    if (amtNum == null || amtNum <= 0.0) {
                        error = "Amount must be > 0"
                        return@Button
                    }

                    val entry = ActivitySample(
                        id = editing?.id ?: 0L,
                        date = editing?.date ?: date,
                        steps = if (type == "Steps") amtNum.toLong() else 0,
                        distanceMeters = if (type == "Distance") amtNum else 0.0,
                        activeEnergyKcal = if (type == "Calories") amtNum else 0.0,
                        source = "manual",
                        note = note
                    )
                    onSave(entry)
                },
                colors = ButtonDefaults.buttonColors(containerColor = saveButtonColor)
            ) {
                Text(if (editing == null) "Save" else "Update", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = cancelButtonColor)
            ) {
                Text("Cancel", color = Color.Black)
            }
        },
        containerColor = dialogBackgroundColor
    )
}