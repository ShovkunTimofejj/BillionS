package org.app.billions.ui.screens.journa

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
    typeFromViewModel: String?,
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

    var type by remember { mutableStateOf("Steps") }

    LaunchedEffect(editing?.id) {
        type =
            if (editing == null)
                typeFromViewModel?.replaceFirstChar { it.uppercase() } ?: "Steps"
            else if (editing.steps > 0)
                "Steps"
            else if (editing.distanceMeters > 0)
                "Distance"
            else
                "Calories"
    }

    var amount by remember { mutableStateOf("") }

    LaunchedEffect(editing?.id) {
        amount = editing?.let {
            when {
                it.steps > 0 -> it.steps.toString()
                it.distanceMeters > 0 -> it.distanceMeters.toString()
                else -> it.activeEnergyKcal.toString()
            }
        } ?: ""
    }

    var note by remember { mutableStateOf("") }

    LaunchedEffect(editing?.id) {
        note = editing?.note ?: ""
    }
    var date by remember { mutableStateOf(editing?.date ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())) }
    var error by remember { mutableStateOf<String?>(null) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onCancel() },
        contentAlignment = Alignment.Center
    ) {

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(dialogBackgroundColor),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .clickable(enabled = false) {}
        ) {

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    if (editing == null) "Add Entry" else "Edit Entry",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("Steps", "Distance", "Calories").forEach { option ->
                        val isSelected = type == option

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) saveButtonColor else Color.Gray.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .background(
                                    if (isSelected) saveButtonColor.copy(alpha = 0.15f)
                                    else Color.Transparent
                                )
                                .clickable { type = option }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = option,
                                color = if (isSelected) saveButtonColor else Color.White,
                                fontSize = 15.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(fieldBackgroundColor, shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    BasicTextField(
                        value = amount,
                        onValueChange = {
                            amount = it
                            error = null
                        },
                        textStyle = TextStyle(color = Color.White, fontSize = 15.sp),
                        singleLine = true,
                        cursorBrush = SolidColor(Color.White),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        decorationBox = { inner ->
                            if (amount.isEmpty()) Text("Amount", color = Color.Gray)
                            inner()
                        }
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(fieldBackgroundColor, shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    BasicTextField(
                        value = note,
                        onValueChange = { note = it },
                        textStyle = TextStyle(color = Color.White, fontSize = 15.sp),
                        singleLine = true,
                        cursorBrush = SolidColor(Color.White),
                        decorationBox = { inner ->
                            if (note.isEmpty()) Text("Note", color = Color.Gray)
                            inner()
                        }
                    )
                }

                if (error != null) {
                    Text(error!!, color = Color.Red, fontSize = 12.sp)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    Button(
                        onClick = onCancel,
                        modifier = Modifier
                            .height(44.dp)
                            .width(120.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(cancelButtonColor)
                    ) {
                        Text("Cancel", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

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
                        modifier = Modifier
                            .height(44.dp)
                            .width(120.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(saveButtonColor)
                    ) {
                        Text(
                            if (editing == null) "Save" else "Update",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}