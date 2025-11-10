package org.app.billions.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import billions.composeapp.generated.resources.Res
import billions.composeapp.generated.resources.bg_dark_lime
import billions.composeapp.generated.resources.bg_graphite_gold
import billions.composeapp.generated.resources.bg_neon_coral
import billions.composeapp.generated.resources.bg_royal_blue
import billions.composeapp.generated.resources.logo_default
import org.app.billions.ui.screens.Screen
import org.app.billions.ui.screens.viewModel.SplashScreenViewModel
import org.jetbrains.compose.resources.painterResource

@Composable
fun SplashScreen(
    navController: NavHostController,
    viewModel: SplashScreenViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    val currentTheme = uiState.currentTheme

    val backgroundRes by remember(currentTheme) {
        derivedStateOf {
            when (currentTheme?.id) {
                "dark_lime" -> Res.drawable.bg_dark_lime
                "neon_coral" -> Res.drawable.bg_neon_coral
                "royal_blue" -> Res.drawable.bg_royal_blue
                "graphite_gold" -> Res.drawable.bg_graphite_gold
                else -> Res.drawable.bg_dark_lime
            }
        }
    }

    LaunchedEffect(currentTheme) {
        println("SplashScreen: currentTheme changed -> $currentTheme")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (currentTheme == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Image(
                painter = painterResource(backgroundRes),
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }

    LaunchedEffect(uiState.isFirstLaunch) {
        viewModel.splashDelay(1L).collect {
            if (uiState.isFirstLaunch) {
                navController.navigate(Screen.OnboardingScreen.route) {
                    popUpTo(Screen.SplashScreen.route) { inclusive = true }
                }
                viewModel.markLaunched()
            } else {
                navController.navigate(Screen.MainMenuScreen.route) {
                    popUpTo(Screen.SplashScreen.route) { inclusive = true }
                }
            }
        }
    }
}