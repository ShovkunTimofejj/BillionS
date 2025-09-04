package org.app.billions.ui.screens.challenges

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.flow.combine
import org.app.billions.data.model.Challenge
import org.app.billions.data.model.ChallengeStatus
import org.app.billions.data.model.RewardType
import org.app.billions.ui.screens.Screen
import org.app.billions.ui.screens.dashboard.RingView
import org.app.billions.ui.screens.viewModel.ChallengesViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengesScreen(
    navController: NavHostController,
    viewModel: ChallengesViewModel
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val filteredChallenges by viewModel.filteredChallenges.collectAsState()
    LaunchedEffect(filteredChallenges) {
        println("Filtered challenges for tab $selectedTab: ${filteredChallenges.size}")
        filteredChallenges.forEach { println("Challenge: ${it.type}, progress: ${it.progress}") }
    }

    var selectedChallengeTabIndex by remember { mutableStateOf(ChallengeStatus.values().indexOf(selectedTab)) }

    var selectedBottomNavIndex by remember { mutableStateOf(1) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Challenges", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF001F3F))
            )
        },
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            NavigationBar(containerColor = Color(0xFF001F3F)) {
                val navItems = listOf(
                    Screen.MainMenuScreen to Icons.Default.Home,
                    Screen.ChallengesScreen to Icons.Default.Flag,
                    Screen.JournalScreen to Icons.Default.List,
                    Screen.SettingsScreen to Icons.Default.Settings
                )

                navItems.forEach { (screen, icon) ->
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(imageVector = icon, contentDescription = screen.route) },
                        label = { Text(text = screen.route.replaceFirstChar { it.uppercase() }, color = Color.White) }
                    )
                }
            }
        },
        containerColor = Color(0xFF001F3F)
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            val tabTitles = listOf("Active", "Available", "History")
            TabRow(
                selectedTabIndex = selectedChallengeTabIndex,
                containerColor = Color(0xFF001F3F)
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedChallengeTabIndex == index,
                        onClick = {
                            selectedChallengeTabIndex = index
                            viewModel.setTab(ChallengeStatus.values()[index])
                        },
                        text = { Text(title, color = Color.White) }
                    )
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredChallenges) { challenge ->
                    ChallengeCard(challenge) {
                        viewModel.selectChallenge(challenge)
                        navController.navigate("challengeDetail")
                    }
                }
            }
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

            Spacer(modifier = Modifier.height(16.dp))

            RingView(
                progress = challenge.progress.toFloat(),
                label = "${(challenge.progress * 100).toInt()}%",
                color = Color(0xFF00FF00),
                size = 80.dp,
                goalReached = challenge.progress >= 1.0f
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Days left: ${challenge.daysLeft}", color = Color.White)
                val badgeColor = when (challenge.reward) {
                    RewardType.Bronze -> Color(0xFFCD7F32)
                    RewardType.Silver -> Color(0xFFC0C0C0)
                    RewardType.Gold -> Color(0xFFFFD700)
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(badgeColor, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(challenge.reward.name.first().toString(), color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}