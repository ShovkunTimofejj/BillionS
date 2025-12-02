package org.app.billions.ui.screens.challenges

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.content.MediaType.Companion.Text
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentDataType.Companion.Text
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import billions.composeapp.generated.resources.Res
import billions.composeapp.generated.resources.bg_dashboard_dark_lime
import billions.composeapp.generated.resources.bg_dashboard_graphite_gold
import billions.composeapp.generated.resources.bg_dashboard_neon_coral
import billions.composeapp.generated.resources.bg_dashboard_royal_blue
import billions.composeapp.generated.resources.ic_medal_bronze
import billions.composeapp.generated.resources.ic_medal_gold
import billions.composeapp.generated.resources.ic_medal_silver
import org.app.billions.data.model.ChallengeStatus
import org.app.billions.data.model.ChallengeType
import org.app.billions.data.model.RewardType
import org.app.billions.ui.screens.viewModel.ChallengesViewModel
import org.app.billions.ui.screens.viewModel.SplashScreenViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeDetailScreen(
    navController: NavHostController,
    viewModel: ChallengesViewModel,
    splashScreenViewModel: SplashScreenViewModel
) {
    val challengeState = viewModel.selectedChallenge.collectAsState()
    val challenge = challengeState.value
    val uiState by splashScreenViewModel.uiState.collectAsState()
    val currentTheme = uiState.currentTheme

    if (challenge == null) {
        Text("Challenge not found", color = Color.White)
        return
    }

    val autoReward: RewardType = when (challenge.type) {
        ChallengeType.Sprint7 -> RewardType.Bronze
        ChallengeType.StreakBuilder14 -> RewardType.Silver
        ChallengeType.Marathon30 -> RewardType.Gold
    }

    var selectedReward by remember { mutableStateOf<RewardType?>(null) }

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
        "dark_lime" -> Color(0xFF334A32)
        "neon_coral" -> Color(0xFF4B2637)
        "royal_blue" -> Color(0xFF1C193C)
        "graphite_gold" -> Color(0xFF3C1919)
        else -> Color(0xFF334A32)
    }

    val cardColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFF334A32)
        "neon_coral" -> Color(0xFF4B2637)
        "royal_blue" -> Color(0xFF1C193C)
        "graphite_gold" -> Color(0xFF3C1919)
        else -> Color(0xFF334A32)
    }

    val contentColor = Color(0xFFF6E19F)

    val rewardRes = when (autoReward) {
        RewardType.Bronze -> Res.drawable.ic_medal_bronze
        RewardType.Silver -> Res.drawable.ic_medal_silver
        RewardType.Gold -> Res.drawable.ic_medal_gold
    }

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
                    title = { Text(challenge.type.displayName, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
                    CircularProgressIndicator(
                        progress = challenge.progress.toFloat(),
                        strokeWidth = 16.dp,
                        color = contentColor,
                        trackColor = barColor,
                        modifier = Modifier.fillMaxSize()
                    )
                    Text(
                        text = "${(challenge.progress * 100).toInt()}%",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Days left: ${challenge.daysLeft}",
                    color = Color.White,
                    fontSize = 18.sp
                )

                Spacer(Modifier.height(16.dp))
                Text(
                    "Reward",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {
                            selectedReward =
                                if (selectedReward == autoReward) null else autoReward
                        }
                        .shadow(8.dp, RoundedCornerShape(12.dp))
                        .background(cardColor, RoundedCornerShape(12.dp))
                        .padding(8.dp)
                ) {
                    Image(
                        painter = painterResource(rewardRes),
                        contentDescription = "${autoReward.name} medal",
                        modifier = Modifier.size(96.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        autoReward.name,
                        color = contentColor,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                }

                Spacer(Modifier.height(16.dp))

                selectedReward?.let { reward ->
                    val rewardIcon = when (reward) {
                        RewardType.Bronze -> Res.drawable.ic_medal_bronze
                        RewardType.Silver -> Res.drawable.ic_medal_silver
                        RewardType.Gold -> Res.drawable.ic_medal_gold
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(cardColor.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Image(
                            painter = painterResource(rewardIcon),
                            contentDescription = "${reward.name} medal",
                            modifier = Modifier
                                .size(160.dp)
                                .shadow(16.dp, RoundedCornerShape(16.dp))
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            reward.name,
                            color = contentColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = when (reward) {
                                RewardType.Bronze ->
                                    "Awarded for completing the Sprint 7 challenge."
                                RewardType.Silver ->
                                    "Awarded for completing the StreakBuilder 14 challenge."
                                RewardType.Gold ->
                                    "Awarded for completing the Marathon 30 challenge."
                            },
                            color = Color.White,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate("rewards") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = cardColor)
                ) {
                    Text("View Rewards Gallery", color = Color.White)
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        when (challenge.status) {
                            ChallengeStatus.Active -> viewModel.leaveChallenge()
                            ChallengeStatus.Available -> viewModel.joinChallenge()
                            ChallengeStatus.Completed -> viewModel.restartChallenge()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = cardColor)
                ) {
                    Text(
                        when (challenge.status) {
                            ChallengeStatus.Active -> "Leave"
                            ChallengeStatus.Available -> "Join"
                            ChallengeStatus.Completed -> "Restart"
                        },
                        color = Color.White
                    )
                }
            }
        }
    }
}
