package org.app.billions.ui.screens.challenges

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.combine
import org.app.billions.data.model.Challenge
import org.app.billions.data.model.ChallengeStatus
import org.app.billions.ui.screens.Screen
import org.app.billions.ui.screens.viewModel.ChallengesViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengesScreen(
    navController: NavHostController,
    viewModel: ChallengesViewModel = koinViewModel()
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val challenges by viewModel.challenges.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(1) } // 1 — Challenges

    val filteredChallenges by viewModel.challenges
        .combine(viewModel.selectedTab) { challenges, tab ->
            challenges.filter { it.status == tab }
        }
        .collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Challenges", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF001F3F))
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF001F3F)) {
                NavigationBarItem(
                    selected = selectedTabIndex == 0,
                    onClick = {
                        selectedTabIndex = 0
                        navController.navigate(Screen.MainMenuScreen.route) {
                            popUpTo(Screen.MainMenuScreen.route) { inclusive = true }
                        }
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                    label = { Text("Home", color = Color.White) }
                )
                NavigationBarItem(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    icon = { Icon(Icons.Default.Flag, contentDescription = "Challenges") },
                    label = { Text("Challenges", color = Color.White) }
                )
                NavigationBarItem(
                    selected = selectedTabIndex == 2,
                    onClick = {
                        selectedTabIndex = 2
                        navController.navigate("settings") {
                            popUpTo("dashboard")
                        }
                    },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings", color = Color.White) }
                )
            }
        },
        containerColor = Color(0xFF001F3F)
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TabRow(
                selectedTabIndex = ChallengeStatus.values().indexOf(selectedTab),
                containerColor = Color(0xFF001F3F)
            ) {
                ChallengeStatus.values().forEachIndexed { index, status ->
                    Tab(
                        selected = selectedTab == status,
                        onClick = { viewModel.setTab(status) },
                        text = { Text(status.name, color = Color.White) }
                    )
                }
            }

            LazyColumn {
                items(filteredChallenges) { challenge ->
                    ChallengeCard(challenge) {
                        viewModel.selectChallenge(challenge)
                        navController.navigate("challengeDetail")
                    }
                }
            }
//            LazyColumn {
//                items(viewModel.getFilteredChallenges()) { challenge ->
//                    ChallengeCard(challenge) {
//                        viewModel.selectChallenge(challenge)
//                        navController.navigate("challengeDetail")
//                    }
//                }
//            }
        }
    }
}

@Composable
fun ChallengeCard(challenge: Challenge, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF002A55))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(challenge.type.name, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = challenge.progress.toFloat(),
                modifier = Modifier.fillMaxWidth(),
                color = Color.Green
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Progress: ${(challenge.progress * 100).toInt()}%", color = Color.White)
        }
    }
}