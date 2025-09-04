package org.app.billions.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.datetime.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsDetailScreen(
    navController: NavHostController,
    notificationsManager: NotificationsManager
) {
    var dailyReminderTime by remember { mutableStateOf(LocalTime(9, 0)) }
    var challengeReminder by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF001F3F))
            )
        },
        containerColor = Color(0xFF001F3F)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("Daily Reminder", color = Color.White, style = MaterialTheme.typography.bodyLarge)

            Button(
                onClick = {
                    notificationsManager.scheduleDailyReminder(dailyReminderTime.hour, dailyReminderTime.minute)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF00))
            ) {
                Text("Set Time: ${dailyReminderTime.hour}:${dailyReminderTime.minute}", color = Color.Black)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Challenge Reminder", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = challengeReminder,
                    onCheckedChange = {
                        challengeReminder = it
                        notificationsManager.setChallengeReminder(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF00FF00),
                        uncheckedThumbColor = Color.Gray
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF00)),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Notifications", color = Color.Black)
            }
        }
    }
}
