package org.app.billions.ui.screens.challenges

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
import org.app.billions.data.model.RewardType
import org.app.billions.ui.screens.Screen
import org.app.billions.ui.screens.viewModel.ChallengesViewModel
import org.app.billions.ui.screens.viewModel.SplashScreenViewModel
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsGalleryScreen(
    navController: NavHostController,
    viewModel: ChallengesViewModel,
    splashScreenViewModel: SplashScreenViewModel
) {
    val rewards = listOf(RewardType.Bronze, RewardType.Silver, RewardType.Gold)
    var selectedReward by remember { mutableStateOf(RewardType.Bronze) }

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

    val cardColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFF334A32)
        "neon_coral" -> Color(0xFF4B2637)
        "royal_blue" -> Color(0xFF1C193C)
        "graphite_gold" -> Color(0xFF3C1919)
        else -> Color(0xFF334A32)
    }

    val highlightColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFF334A32).copy(alpha = 0.5f)
        "neon_coral" -> Color(0xFF4B2637).copy(alpha = 0.5f)
        "royal_blue" -> Color(0xFF1C193C).copy(alpha = 0.5f)
        "graphite_gold" -> Color(0xFF3C1919).copy(alpha = 0.5f)
        else -> Color(0xFF334A32).copy(alpha = 0.5f)
    }

    val contentColor = Color.White

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
                    title = { Text("Rewards Gallery", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->

            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                item {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .height(170.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        userScrollEnabled = false
                    ) {
                        items(rewards) { reward ->
                            val rewardRes = when (reward) {
                                RewardType.Bronze -> Res.drawable.ic_medal_bronze
                                RewardType.Silver -> Res.drawable.ic_medal_silver
                                RewardType.Gold -> Res.drawable.ic_medal_gold
                            }

                            val isSelected = selectedReward == reward
                            val bgColor = if (isSelected) highlightColor else cardColor

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .shadow(
                                        if (isSelected) 12.dp else 4.dp,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .background(bgColor, RoundedCornerShape(12.dp))
                                    .clickable { selectedReward = reward }
                                    .padding(8.dp)
                            ) {
                                Image(
                                    painter = painterResource(rewardRes),
                                    contentDescription = reward.name,
                                    modifier = Modifier.size(if (isSelected) 80.dp else 64.dp)
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    reward.name,
                                    color = contentColor,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(16.dp))

                    val rewardRes = when (selectedReward) {
                        RewardType.Bronze -> Res.drawable.ic_medal_bronze
                        RewardType.Silver -> Res.drawable.ic_medal_silver
                        RewardType.Gold -> Res.drawable.ic_medal_gold
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .background(
                                cardColor.copy(alpha = 0.9f),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(24.dp)
                            .fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(rewardRes),
                            contentDescription = selectedReward.name,
                            modifier = Modifier.size(160.dp)
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            selectedReward.name,
                            color = contentColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )

                        Spacer(Modifier.height(10.dp))

                        Text(
                            text = when (selectedReward) {
                                RewardType.Bronze ->
                                    "Bronze Medal — awarded for your persistence and dedication. " +
                                            "You've proven that consistent effort leads to growth. Keep pushing forward — every small win matters!"

                                RewardType.Silver ->
                                    "Silver Medal — a symbol of your strong performance and steady progress. " +
                                            "You've gone beyond the basics and shown determination, skill, and focus on improvement."

                                RewardType.Gold ->
                                    "Gold Medal — the highest honor for completing the challenge flawlessly! " +
                                            "You've demonstrated mastery, resilience, and excellence!"
                            },
                            color = Color.White,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}
