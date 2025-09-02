package org.app.billions.ui.screens.challenges

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.content.MediaType.Companion.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentDataType.Companion.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.app.billions.data.model.ChallengeStatus
import org.app.billions.data.model.RewardType
import org.app.billions.ui.screens.viewModel.ChallengesViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeDetailScreen(
    navController: NavHostController,
    viewModel: ChallengesViewModel
) {
    val challengeState = viewModel.selectedChallenge.collectAsState()
    val challenge = challengeState.value

    if (challenge == null) {
        Text("Challenge not found", color = Color.White)
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(challenge.type.name, color = Color.White) },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                CircularProgressIndicator(
                    progress = challenge.progress.toFloat(),
                    strokeWidth = 16.dp,
                    color = Color(0xFF00FF00),
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

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(80.dp)
            ) {
                val badgeColor = when(challenge.reward) {
                    RewardType.Bronze -> Color(0xFFCD7F32)
                    RewardType.Silver -> Color(0xFFC0C0C0)
                    RewardType.Gold -> Color(0xFFFFD700)
                }
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(color = badgeColor)
                }
                Text(
                    text = challenge.reward.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (challenge.status == ChallengeStatus.Active) {
                        viewModel.leaveChallenge()
                    } else {
                        viewModel.joinChallenge()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (challenge.status == ChallengeStatus.Active) "Leave" else "Join")
            }
        }
    }
}