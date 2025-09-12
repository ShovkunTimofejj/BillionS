package org.app.billions.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import billions.composeapp.generated.resources.Res
import billions.composeapp.generated.resources.btn_continue_dark_lime
import billions.composeapp.generated.resources.btn_continue_graphite_gold
import billions.composeapp.generated.resources.btn_continue_neon_coral
import billions.composeapp.generated.resources.btn_continue_royal_blue
import billions.composeapp.generated.resources.btn_get_started_dark_lime
import billions.composeapp.generated.resources.btn_get_started_graphite_gold
import billions.composeapp.generated.resources.btn_get_started_neon_coral
import billions.composeapp.generated.resources.btn_get_started_royal_blue
import billions.composeapp.generated.resources.btn_skip_dark_lime
import billions.composeapp.generated.resources.btn_skip_graphite_gold
import billions.composeapp.generated.resources.btn_skip_neon_coral
import billions.composeapp.generated.resources.btn_skip_royal_blue
import billions.composeapp.generated.resources.logo_default
import billions.composeapp.generated.resources.onboarding2_bg_dark_lime
import billions.composeapp.generated.resources.onboarding2_bg_graphite_gold
import billions.composeapp.generated.resources.onboarding2_bg_neon_coral
import billions.composeapp.generated.resources.onboarding2_bg_royal_blue
import billions.composeapp.generated.resources.onboarding3_bg_dark_lime
import billions.composeapp.generated.resources.onboarding3_bg_graphite_gold
import billions.composeapp.generated.resources.onboarding3_bg_neon_coral
import billions.composeapp.generated.resources.onboarding3_bg_royal_blue
import billions.composeapp.generated.resources.onboarding_bg_dark_lime
import billions.composeapp.generated.resources.onboarding_bg_graphite_gold
import billions.composeapp.generated.resources.onboarding_bg_neon_coral
import billions.composeapp.generated.resources.onboarding_bg_royal_blue
import kotlinx.coroutines.launch
import org.app.billions.data.model.Theme
import org.app.billions.ui.screens.Screen
import org.app.billions.ui.screens.viewModel.JournalState
import org.app.billions.ui.screens.viewModel.JournalViewModel
import org.app.billions.ui.screens.viewModel.SplashScreenViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.pow
import kotlin.math.round

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    navController: NavHostController,
    viewModel: SplashScreenViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentTheme = uiState.currentTheme
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    val skipRes = when (currentTheme?.id) {
        "dark_lime" -> Res.drawable.btn_skip_dark_lime
        "neon_coral" -> Res.drawable.btn_skip_neon_coral
        "royal_blue" -> Res.drawable.btn_skip_royal_blue
        "graphite_gold" -> Res.drawable.btn_skip_graphite_gold
        else -> Res.drawable.btn_skip_dark_lime
    }

    val continueRes = when (currentTheme?.id) {
        "dark_lime" -> Res.drawable.btn_continue_dark_lime
        "neon_coral" -> Res.drawable.btn_continue_neon_coral
        "royal_blue" -> Res.drawable.btn_continue_royal_blue
        "graphite_gold" -> Res.drawable.btn_continue_graphite_gold
        else -> Res.drawable.btn_continue_dark_lime
    }

    val getStartedRes = when (currentTheme?.id) {
        "dark_lime" -> Res.drawable.btn_get_started_dark_lime
        "neon_coral" -> Res.drawable.btn_get_started_neon_coral
        "royal_blue" -> Res.drawable.btn_get_started_royal_blue
        "graphite_gold" -> Res.drawable.btn_get_started_graphite_gold
        else -> Res.drawable.btn_get_started_dark_lime
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            when (page) {
                0 -> OnboardingSlideOne(currentTheme)
                1 -> OnboardingSlideTwo(currentTheme)
                2 -> OnboardingSlideThree(currentTheme)
            }
        }

        Image(
            painter = painterResource(skipRes),
            contentDescription = "Skip",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 95.dp, end = 24.dp)
                .size(50.dp)
                .clickable {
                    navController.navigate(Screen.MainMenuScreen.route) {
                        popUpTo(Screen.OnboardingScreen.route) { inclusive = true }
                    }
                }
        )

        val bottomButtonRes = if (pagerState.currentPage < 2) continueRes else getStartedRes
        Image(
            painter = painterResource(bottomButtonRes),
            contentDescription = "Continue or Get Started",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .size(250.dp)
                .clickable {
                    if (pagerState.currentPage < 2) {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        navController.navigate(Screen.MainMenuScreen.route) {
                            popUpTo(Screen.OnboardingScreen.route) { inclusive = true }
                        }
                    }
                }
        )
    }
}

@Composable
fun OnboardingSlideOne(theme: Theme?) {
    val bgRes = when (theme?.id) {
        "dark_lime" -> Res.drawable.onboarding_bg_dark_lime
        "neon_coral" -> Res.drawable.onboarding_bg_neon_coral
        "royal_blue" -> Res.drawable.onboarding_bg_royal_blue
        "graphite_gold" -> Res.drawable.onboarding_bg_graphite_gold
        else -> Res.drawable.onboarding_bg_dark_lime
    }

    Image(
        painter = painterResource(bgRes),
        contentDescription = "Onboarding Slide 1",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun OnboardingSlideTwo(theme: Theme?) {
    val bgRes = when (theme?.id) {
        "dark_lime" -> Res.drawable.onboarding2_bg_dark_lime
        "neon_coral" -> Res.drawable.onboarding2_bg_neon_coral
        "royal_blue" -> Res.drawable.onboarding2_bg_royal_blue
        "graphite_gold" -> Res.drawable.onboarding2_bg_graphite_gold
        else -> Res.drawable.onboarding2_bg_dark_lime
    }

    Image(
        painter = painterResource(bgRes),
        contentDescription = "Onboarding Slide 2",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun OnboardingSlideThree(theme: Theme?) {
    val bgRes = when (theme?.id) {
        "dark_lime" -> Res.drawable.onboarding3_bg_dark_lime
        "neon_coral" -> Res.drawable.onboarding3_bg_neon_coral
        "royal_blue" -> Res.drawable.onboarding3_bg_royal_blue
        "graphite_gold" -> Res.drawable.onboarding3_bg_graphite_gold
        else -> Res.drawable.onboarding3_bg_dark_lime
    }

    Image(
        painter = painterResource(bgRes),
        contentDescription = "Onboarding Slide 3",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}