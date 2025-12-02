package org.app.billions.ui.screens.challenges

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import billions.composeapp.generated.resources.Res
import billions.composeapp.generated.resources.bg_dashboard_dark_lime
import billions.composeapp.generated.resources.bg_dashboard_graphite_gold
import billions.composeapp.generated.resources.bg_dashboard_neon_coral
import billions.composeapp.generated.resources.bg_dashboard_royal_blue
import billions.composeapp.generated.resources.ic_medal_bronze
import billions.composeapp.generated.resources.ic_medal_gold
import billions.composeapp.generated.resources.ic_medal_silver
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import org.app.billions.data.model.Challenge
import org.app.billions.data.model.ChallengeStatus
import org.app.billions.data.model.ChallengeType
import org.app.billions.data.model.RewardType
import org.app.billions.data.model.Theme
import org.app.billions.ui.screens.Screen
import org.app.billions.ui.screens.buttonBar.AppBottomBar
import org.app.billions.ui.screens.dashboard.RingView
import org.app.billions.ui.screens.viewModel.ChallengesViewModel
import org.app.billions.ui.screens.viewModel.SplashScreenViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengesScreen(
    navController: NavHostController,
    viewModel: ChallengesViewModel,
    splashScreenViewModel: SplashScreenViewModel
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val filteredChallenges by viewModel.filteredChallenges.collectAsState()
    val uiState by splashScreenViewModel.uiState.collectAsState()
    val currentTheme = uiState.currentTheme

    val backgroundRes by remember(currentTheme) {
        derivedStateOf {
            when (currentTheme?.id) {
                "dark_lime" -> Res.drawable.bg_dashboard_dark_lime
                "neon_coral" -> Res.drawable.bg_dashboard_neon_coral
                "royal_blue" -> Res.drawable.bg_dashboard_royal_blue
                "graphite_gold" -> Res.drawable.bg_dashboard_graphite_gold
                else -> Res.drawable.bg_dashboard_dark_lime
            }
        }
    }

    val barColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFF1F2D1E)
        "neon_coral" -> Color(0xFF2A151E)
        "royal_blue" -> Color(0xFF110E32)
        "graphite_gold" -> Color(0xFF320F0E)
        else -> Color(0xFF1F2D1E)
    }

    val cardColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFF334A32)
        "neon_coral" -> Color(0xFF4B2637)
        "royal_blue" -> Color(0xFF1C193C)
        "graphite_gold" -> Color(0xFF3C1919)
        else -> Color(0xFF334A32)
    }

    val contentColor = Color.White

    var selectedChallengeTabIndex by remember { mutableStateOf(ChallengeStatus.values().indexOf(selectedTab)) }
    var selectedBottomNavIndex by remember { mutableStateOf(1) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(backgroundRes),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    title = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Challenges",
                                color = Color.White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                )
            },
            bottomBar = {
                AppBottomBar(
                    navController = navController,
                    selectedTabIndex = selectedBottomNavIndex,
                    onTabSelected = { selectedBottomNavIndex = it },
                    barColor = barColor,
                    currentTheme = currentTheme
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                ChallengeTabs(
                    selectedIndex = selectedChallengeTabIndex,
                    onTabSelected = { index ->
                        selectedChallengeTabIndex = index
                        viewModel.setTab(ChallengeStatus.values()[index])
                    },
                    currentTheme = currentTheme
                )

                if (filteredChallenges.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val message = when (selectedTab) {
                            ChallengeStatus.Active ->
                                "You don’t have any active challenges yet.\nGo to Available to start one."
                            ChallengeStatus.Available ->
                                "No available challenges left.\nYou can restart a completed challenge."
                            ChallengeStatus.Completed ->
                                "You don’t have any completed challenges yet."
                        }

                        Text(
                            text = message,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        items(filteredChallenges) { challenge ->
                            ChallengeCard(
                                challenge = challenge,
                                onClick = {
                                    viewModel.selectChallenge(challenge)
                                    navController.navigate("challengeDetail")
                                },
                                cardColor = cardColor,
                                contentColor = contentColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChallengeCard(
    challenge: Challenge,
    onClick: () -> Unit,
    cardColor: Color,
    contentColor: Color
) {
    val reward = when (challenge.type) {
        ChallengeType.Sprint7 -> RewardType.Bronze
        ChallengeType.StreakBuilder14 -> RewardType.Silver
        ChallengeType.Marathon30 -> RewardType.Gold
    }

    val totalDays = when (challenge.type) {
        ChallengeType.Marathon30 -> 30
        ChallengeType.Sprint7 -> 7
        ChallengeType.StreakBuilder14 -> 14
    }

    val totalSteps = challenge.goal.toInt()
    val currentSteps = (challenge.progress * challenge.goal).toInt()

    val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date

    val startDate = if (challenge.status == ChallengeStatus.Available) {
        today
    } else {
        Instant.fromEpochMilliseconds(challenge.startDate)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
    }

    val rawDaysPassed = if (challenge.status == ChallengeStatus.Available) 0
    else startDate.daysUntil(today).coerceAtLeast(0)

    val daysPassed = rawDaysPassed.coerceAtMost(totalDays)

    val progress = (currentSteps.toFloat() / totalSteps.toFloat())
        .coerceIn(0f, 1f)

    val rewardRes = when (reward) {
        RewardType.Bronze -> Res.drawable.ic_medal_bronze
        RewardType.Silver -> Res.drawable.ic_medal_silver
        RewardType.Gold -> Res.drawable.ic_medal_gold
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = challenge.type.displayName,
                    color = Color(0xFFF6E19F),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "$totalDays days $totalSteps steps",
                    color = Color.White,
                    fontSize = 16.sp
                )

                Spacer(Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.Black.copy(alpha = 0.4f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(12.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFFF6E19F))
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$currentSteps / $totalSteps",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )

                    Text(
                        text = "$daysPassed / $totalDays days",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }
            }

            Image(
                painter = painterResource(rewardRes),
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .padding(start = 12.dp)
            )
        }
    }
}

@Composable
fun ChallengeTabs(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    currentTheme: Theme?
) {
    val activeBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF6E19F),
            Color(0xFF90845D)
        )
    )

    val inactiveColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFF1F2D1E)
        "neon_coral" -> Color(0xFF2A151E)
        "royal_blue" -> Color(0xFF110E32)
        "graphite_gold" -> Color(0xFF320F0E)
        else -> Color(0xFF1F2D1E)
    }

    val titles = listOf("Active", "Available", "History")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp), clip = false)
            .background(
                color = inactiveColor.copy(alpha = 0.9f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            titles.forEachIndexed { index, title ->
                val isSelected = selectedIndex == index

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = if (isSelected) activeBrush else SolidColor(inactiveColor),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onTabSelected(index) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) Color.Black else Color.White,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}