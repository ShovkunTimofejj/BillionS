package org.app.billions.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import billions.composeapp.generated.resources.Res
import billions.composeapp.generated.resources.bg_dashboard_dark_lime
import billions.composeapp.generated.resources.bg_dashboard_graphite_gold
import billions.composeapp.generated.resources.bg_dashboard_neon_coral
import billions.composeapp.generated.resources.bg_dashboard_royal_blue
import billions.composeapp.generated.resources.logo_default
import billions.composeapp.generated.resources.logo_graphite_gold
import billions.composeapp.generated.resources.logo_neon_coral
import billions.composeapp.generated.resources.logo_royal_blue
import org.app.billions.ui.screens.viewModel.SplashScreenViewModel
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    navController: NavHostController,
    splashScreenViewModel: SplashScreenViewModel
) {

    val uiState by splashScreenViewModel.uiState.collectAsState()
    val currentTheme = uiState.currentTheme
    val uriHandler = LocalUriHandler.current

    val backgroundRes = when (currentTheme?.id) {
        "dark_lime" -> Res.drawable.bg_dashboard_dark_lime
        "neon_coral" -> Res.drawable.bg_dashboard_neon_coral
        "royal_blue" -> Res.drawable.bg_dashboard_royal_blue
        "graphite_gold" -> Res.drawable.bg_dashboard_graphite_gold
        else -> Res.drawable.bg_dashboard_dark_lime
    }

    val barColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0x801C2A3A)
        "neon_coral" -> Color(0x80431C2E)
        "royal_blue" -> Color(0x801D3B5C)
        "graphite_gold" -> Color(0x80383737)
        else -> Color(0x801C2A3A)
    }

    val sectionContentColor = Color(0xFFF6E19F)

    val logoRes = Res.drawable.logo_default

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
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "About",
                                color = Color.White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    },

                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },

                    actions = {
                        IconButton(onClick = {}) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = null,
                                tint = Color.Transparent
                            )
                        }
                    }
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 28.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                Spacer(Modifier.height(40.dp))

                Image(
                    painter = painterResource(logoRes),
                    contentDescription = "Logo",
                    modifier = Modifier.size(150.dp)
                )

                Spacer(Modifier.height(32.dp))

                Text(
                    "Golden Rush Fitness Tracker",
                    color = sectionContentColor,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Version 1.0",
                    color = sectionContentColor.copy(alpha = 0.7f),
                    fontSize = 18.sp
                )

                Spacer(Modifier.height(14.dp))

                Text(
                    "Created by Golden Rush",
                    color = sectionContentColor,
                    fontSize = 18.sp
                )

                Spacer(Modifier.height(35.dp))

                Text(
                    text = "Privacy Policy",
                    color = Color(0xFF4FC3F7),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clickable {
                            uriHandler.openUri("https://swiftbuilds.top/NMmVPK")
                        }
                        .padding(4.dp)
                )
            }
        }
    }
}
