package org.app.billions.ui.screens.challenges

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.app.billions.data.model.RewardType
import org.app.billions.ui.screens.Screen
import org.app.billions.ui.screens.viewModel.ChallengesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsGalleryScreen(navController: NavHostController, viewModel: ChallengesViewModel) {
    val rewards = listOf(RewardType.Bronze, RewardType.Silver, RewardType.Gold)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rewards Gallery", color = Color.White) },
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.padding(paddingValues)
        ) {
            items(rewards) { reward ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            navController.navigate(Screen.ChallengesScreen.route)
                        }
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = reward.name,
                        tint = when(reward) {
                            RewardType.Bronze -> Color(0xFFCD7F32)
                            RewardType.Silver -> Color(0xFFC0C0C0)
                            RewardType.Gold -> Color(0xFFFFD700)
                        },
                        modifier = Modifier.size(64.dp)
                    )
                    Text(reward.name, color = Color.White)
                }
            }
        }
    }
}

