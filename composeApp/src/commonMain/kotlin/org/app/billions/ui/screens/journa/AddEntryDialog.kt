package org.app.billions.ui.screens.journa

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.app.billions.data.model.ActivitySample

@Composable
fun AddEntryDialog(
    onSave: (ActivitySample) -> Unit,
    onCancel: () -> Unit
) {
    var type by remember { mutableStateOf("Steps") }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Add Entry") },
        text = {
            Column {
                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Type") }
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val entry = ActivitySample(
                    id = 0L,
                    date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    steps = if (type == "Steps") amount.toLongOrNull() ?: 0 else 0,
                    distanceMeters = if (type == "Distance") amount.toDoubleOrNull() ?: 0.0 else 0.0,
                    activeEnergyKcal = if (type == "Calories") amount.toDoubleOrNull() ?: 0.0 else 0.0,
                    source = "manual"
                )
                onSave(entry)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onCancel) { Text("Cancel") }
        }
    )
}
