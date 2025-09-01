package org.app.billions.ui.screens.journa

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.app.billions.data.model.ActivitySample

@Composable
fun AddEditEntryDialog(
    editing: ActivitySample?,
    onSave: (ActivitySample) -> Unit,
    onCancel: () -> Unit
) {
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

    var date by remember {
        mutableStateOf(editing?.date ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()))
    }

    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(if (editing == null) "Add Entry" else "Edit Entry") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Steps", "Distance", "Calories").forEach { t ->
                        FilterChip(
                            selected = type == t,
                            onClick = { type = t },
                            label = { Text(t) }
                        )
                    }
                }

                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                        error = null
                    },
                    label = { Text("Amount (> 0)") },
                    keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                )

                if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note") }
                )

                Text("Date: $date", fontSize = 12.sp)
            }
        },
        confirmButton = {
            Button(onClick = {
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
            }) {
                Text(if (editing == null) "Save" else "Update")
            }
        },
        dismissButton = {
            Button(onClick = onCancel) { Text("Cancel") }
        }
    )
}
