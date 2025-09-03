package org.app.billions.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import billions.composeapp.generated.resources.Res
import billions.composeapp.generated.resources.app_logo
import kotlinx.coroutines.launch
import org.app.billions.ui.screens.Screen
import org.app.billions.ui.screens.viewModel.JournalState
import org.app.billions.ui.screens.viewModel.JournalViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.pow
import kotlin.math.round

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    navController: NavHostController,
    viewModel: JournalViewModel = koinViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val state by viewModel.state

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF001F3F), Color.Black)
                )
            )
    ) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            when (page) {
                0 -> OnboardingSlideOne()
                1 -> OnboardingSlideTwo(state)
                2 -> OnboardingSlideThree(state)
            }
        }

        TextButton(
            onClick = { navController.navigate(Screen.MainMenuScreen.route) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text("Skip", color = Color.White)
        }

        Button(
            onClick = {
                if (pagerState.currentPage < 2) {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    navController.navigate(Screen.MainMenuScreen.route)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (pagerState.currentPage < 2) "Continue" else "Get Started"
            )
        }
    }
}

@Composable
fun OnboardingSlideOne() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Count your way to a Billion",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(24.dp))
        Image(
            painter = painterResource(Res.drawable.app_logo),
            contentDescription = "Odometer",
            modifier = Modifier.size(150.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(Res.drawable.app_logo),
            contentDescription = "Monocle Guy",
            modifier = Modifier.size(150.dp)
        )
    }
}

@Composable
fun OnboardingSlideTwo(state: JournalState) {
    val steps = state.entries.sumOf { it.steps }
    val distance = state.entries.sumOf { it.distanceMeters } / 1000
    val calories = state.entries.sumOf { it.activeEnergyKcal }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Manual first",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Steps: $steps", color = Color.White)
                Text("Distance: ${distance.format(2)} km", color = Color.White)
                Text("Calories: ${calories.format(0)} kcal", color = Color.White)
            }
        }
    }
}
fun Double.format(decimals: Int): String {
    val factor = 10.0.pow(decimals)
    val rounded = round(this * factor) / factor
    return rounded.toString()
}
@Composable
fun OnboardingSlideThree(state: JournalState) {
    val steps = state.entries.sumOf { it.steps }
    val calories = state.entries.sumOf { it.activeEnergyKcal }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Start small",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Mini goal: Walk ${(steps + 1000)} steps today", color = Color.White)
                Text("Mini goal: Burn ${(calories + 50).toInt()} kcal today", color = Color.White)
            }
        }
    }
}