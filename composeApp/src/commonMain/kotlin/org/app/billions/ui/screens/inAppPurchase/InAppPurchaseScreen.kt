package org.app.billions.ui.screens.inAppPurchase

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import billions.composeapp.generated.resources.theme_dark_lime
import billions.composeapp.generated.resources.theme_graphite_gold
import billions.composeapp.generated.resources.theme_graphite_gold_locked
import billions.composeapp.generated.resources.theme_neon_coral
import billions.composeapp.generated.resources.theme_neon_coral_locked
import billions.composeapp.generated.resources.theme_royal_blue
import billions.composeapp.generated.resources.theme_royal_blue_locked
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.app.billions.data.model.Theme
import org.app.billions.data.repository.ThemeRepository
import org.app.billions.ui.screens.journa.ConfettiOverlay
import org.app.billions.ui.screens.viewModel.SplashScreenViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InAppPurchaseScreen(
    navController: NavHostController,
    billingRepository: BillingRepository,
    themeRepository: ThemeRepository,
    splashScreenViewModel: SplashScreenViewModel
) {
    val scope = rememberCoroutineScope()
    var themes by remember { mutableStateOf<List<Theme>>(emptyList()) }
    var selectedTheme by remember { mutableStateOf<Theme?>(null) }
    var purchaseState by remember { mutableStateOf<PurchaseState>(PurchaseState.Idle) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val uiState by splashScreenViewModel.uiState.collectAsState()
    val currentTheme = uiState.currentTheme

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

    val contentColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFF00FF00)
        "neon_coral" -> Color(0xFFFF8FA0)
        "royal_blue" -> Color(0xFF00BFFF)
        "graphite_gold" -> Color(0xFFB59F00)
        else -> Color(0xFF00FF00)
    }

    LaunchedEffect(Unit) {
        themeRepository.initializeThemes()
        themes = billingRepository.getThemes()
        selectedTheme = currentTheme ?: themes.firstOrNull()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(backgroundRes),
            contentDescription = null,
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
                                "Themes Store",
                                color = Color.White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.Close, null, tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Close, null, tint = Color.Transparent)
                        }
                    }
                )
            },
            containerColor = Color.Transparent
        ) { padding ->

            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(themes) { theme ->

                        val isActive = currentTheme?.id == theme.id

                        val imageRes = when (theme.id) {
                            "dark_lime" -> Res.drawable.theme_dark_lime
                            "neon_coral" -> if (theme.isPurchased) Res.drawable.theme_neon_coral else Res.drawable.theme_neon_coral_locked
                            "royal_blue" -> if (theme.isPurchased) Res.drawable.theme_royal_blue else Res.drawable.theme_royal_blue_locked
                            "graphite_gold" -> if (theme.isPurchased) Res.drawable.theme_graphite_gold else Res.drawable.theme_graphite_gold_locked
                            else -> Res.drawable.theme_dark_lime
                        }

                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .clickable {
                                    selectedTheme = theme
                                    if (theme.isPurchased) {
                                        scope.launch {
                                            themeRepository.setCurrentTheme(theme.id)
                                            splashScreenViewModel.updateTheme(theme.id)
                                        }
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    isActive -> contentColor.copy(alpha = 0.3f)
                                    selectedTheme?.id == theme.id -> contentColor.copy(alpha = 0.2f)
                                    else -> Color.White.copy(alpha = 0.1f)
                                }
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(imageRes),
                                    contentDescription = theme.name,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(theme.name, color = contentColor)
                                    when {
                                        isActive -> Text("Active", color = Color.Red)
                                        theme.isPurchased -> Text("Purchased", color = Color.Green)
                                        else -> Text("$1.99", color = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }

                when (purchaseState) {

                    PurchaseState.Idle -> {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        ) {

                            Button(
                                enabled = selectedTheme != null && !selectedTheme!!.isPurchased,
                                onClick = {
                                    scope.launch {
                                        purchaseState = PurchaseState.Loading
                                        val result = billingRepository.purchaseTheme(selectedTheme!!.id)

                                        purchaseState = when (result) {
                                            is PurchaseResult.Success -> {
                                                themes = billingRepository.getThemes()
                                                themeRepository.setCurrentTheme(selectedTheme!!.id)
                                                splashScreenViewModel.updateTheme(selectedTheme!!.id)
                                                selectedTheme = themes.find { it.id == selectedTheme!!.id }

                                                scope.launch {
                                                    delay(1200)
                                                    purchaseState = PurchaseState.Idle
                                                }

                                                PurchaseState.Success
                                            }

                                            is PurchaseResult.Failure -> PurchaseState.Failure
                                            is PurchaseResult.Error -> {
                                                errorMessage = result.message
                                                PurchaseState.Failure
                                            }
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = contentColor)
                            ) {
                                Text("Buy", color = Color.Black)
                            }

                            Spacer(Modifier.width(12.dp))

                            OutlinedButton(onClick = {
                                scope.launch {
                                    purchaseState = PurchaseState.Restore
                                    delay(1500)
                                    val result = billingRepository.restorePurchases()

                                    purchaseState = when (result) {
                                        is PurchaseResult.Success -> {
                                            themes = billingRepository.getThemes()

                                            scope.launch {
                                                delay(1200)
                                                purchaseState = PurchaseState.Idle
                                            }

                                            PurchaseState.Success
                                        }
                                        is PurchaseResult.Failure -> PurchaseState.Failure
                                        is PurchaseResult.Error -> {
                                            errorMessage = result.message
                                            PurchaseState.Failure
                                        }
                                    }
                                }
                            }) {
                                Text("Restore", color = contentColor)
                            }
                        }
                    }

                    PurchaseState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(16.dp),
                            color = contentColor
                        )
                    }

                    PurchaseState.Success -> {
                    }

                    PurchaseState.Failure -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Purchase Failed",
                                color = contentColor,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                errorMessage ?: "Something went wrong.",
                                color = contentColor,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(Modifier.height(16.dp))

                            Button(
                                onClick = { purchaseState = PurchaseState.Idle },
                                colors = ButtonDefaults.buttonColors(containerColor = contentColor)
                            ) {
                                Text("Try Again", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    PurchaseState.Restore -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = contentColor)
                            Text("Restoring purchases...", color = contentColor)
                        }
                    }
                }
            }
        }

        if (purchaseState == PurchaseState.Success) {
            ConfettiOverlay(currentTheme)
        }
    }
}

sealed class PurchaseState {
    object Idle : PurchaseState()
    object Loading : PurchaseState()
    object Success : PurchaseState()
    object Failure : PurchaseState()
    object Restore : PurchaseState()
}


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun InAppPurchaseScreen(
//    navController: NavHostController,
//    themeRepository: ThemeRepository,
//    splashScreenViewModel: SplashScreenViewModel
//) {
//    val scope = rememberCoroutineScope()
//    var themes by remember { mutableStateOf<List<Theme>>(emptyList()) }
//    var selectedTheme by remember { mutableStateOf<Theme?>(null) }
//    var purchaseState by remember { mutableStateOf<PurchaseState>(PurchaseState.Idle) }
//
//    val uiState by splashScreenViewModel.uiState.collectAsState()
//    val currentTheme = uiState.currentTheme
//
//    val backgroundRes = when (currentTheme?.id) {
//        "dark_lime" -> Res.drawable.bg_dashboard_dark_lime
//        "neon_coral" -> Res.drawable.bg_dashboard_neon_coral
//        "royal_blue" -> Res.drawable.bg_dashboard_royal_blue
//        "graphite_gold" -> Res.drawable.bg_dashboard_graphite_gold
//        else -> Res.drawable.bg_dashboard_dark_lime
//    }
//
//    val barColor = when (currentTheme?.id) {
//        "dark_lime" -> Color(0x801C2A3A)
//        "neon_coral" -> Color(0x80431C2E)
//        "royal_blue" -> Color(0x801D3B5C)
//        "graphite_gold" -> Color(0x80383737)
//        else -> Color(0x801C2A3A)
//    }
//
//    val contentColor = when (currentTheme?.id) {
//        "dark_lime" -> Color(0xFF00FF00)
//        "neon_coral" -> Color(0xFFFF8FA0)
//        "royal_blue" -> Color(0xFF00BFFF)
//        "graphite_gold" -> Color(0xFFB59F00)
//        else -> Color(0xFF00FF00)
//    }
//
//    LaunchedEffect(Unit) {
//        themeRepository.initializeThemes()
//        themes = themeRepository.getThemes()
//        selectedTheme = currentTheme
//    }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//
//        Image(
//            painter = painterResource(backgroundRes),
//            contentDescription = "Background",
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.Crop
//        )
//
//        Scaffold(
//            topBar = {
//                TopAppBar(
//                    title = { Text("Themes Store (Demo)", color = contentColor) },
//                    navigationIcon = {
//                        IconButton(onClick = { navController.popBackStack() }) {
//                            Icon(
//                                Icons.Default.Close,
//                                contentDescription = null,
//                                tint = contentColor
//                            )
//                        }
//                    },
//                    colors = TopAppBarDefaults.topAppBarColors(containerColor = barColor)
//                )
//            },
//            containerColor = Color.Transparent
//        ) { padding ->
//            Column(
//                modifier = Modifier
//                    .padding(padding)
//                    .fillMaxSize(),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                LazyColumn(modifier = Modifier.weight(1f)) {
//                    items(themes) { theme ->
//                        val isActive = currentTheme?.id == theme.id
//
//                        val imageRes = when (theme.id) {
//                            "dark_lime" -> Res.drawable.theme_dark_lime
//                            "neon_coral" -> if (theme.isPurchased) Res.drawable.theme_neon_coral else Res.drawable.theme_neon_coral_locked
//                            "royal_blue" -> if (theme.isPurchased) Res.drawable.theme_royal_blue else Res.drawable.theme_royal_blue_locked
//                            "graphite_gold" -> if (theme.isPurchased) Res.drawable.theme_graphite_gold else Res.drawable.theme_graphite_gold_locked
//                            else -> Res.drawable.theme_dark_lime
//                        }
//
//                        Card(
//                            modifier = Modifier
//                                .padding(8.dp)
//                                .fillMaxWidth()
//                                .clickable {
//                                    if (theme.isPurchased) {
//                                        scope.launch {
//                                            themeRepository.setCurrentTheme(theme.id)
//                                            splashScreenViewModel.updateTheme(theme.id)
//                                            selectedTheme = theme
//                                        }
//                                    } else {
//                                        selectedTheme = theme
//                                    }
//                                },
//                            colors = CardDefaults.cardColors(
//                                containerColor = when {
//                                    isActive -> contentColor.copy(alpha = 0.3f)
//                                    selectedTheme?.id == theme.id -> contentColor.copy(alpha = 0.2f)
//                                    else -> Color.White.copy(alpha = 0.1f)
//                                }
//                            )
//                        ) {
//                            Row(
//                                modifier = Modifier.padding(16.dp),
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Image(
//                                    painter = painterResource(imageRes),
//                                    contentDescription = theme.name,
//                                    modifier = Modifier
//                                        .size(60.dp)
//                                        .clip(RoundedCornerShape(12.dp))
//                                )
//                                Spacer(Modifier.width(12.dp))
//                                Column {
//                                    Text(
//                                        theme.name,
//                                        style = MaterialTheme.typography.titleMedium,
//                                        color = contentColor
//                                    )
//                                    when {
//                                        isActive -> Text("Active", color = Color.Red)
//                                        theme.isPurchased -> Text("Unlocked", color = Color.Green)
//                                        else -> Text("Locked", color = Color.Gray)
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//
//                when (purchaseState) {
//
//                    PurchaseState.Idle,
//                    PurchaseState.Success -> {
//                        Row(
//                            horizontalArrangement = Arrangement.Center,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(16.dp)
//                        ) {
//                            Button(
//                                enabled = selectedTheme != null && !selectedTheme!!.isPurchased,
//                                onClick = {
//                                    scope.launch {
//                                        purchaseState = PurchaseState.Loading
//                                        delay(1000)
//
//                                        selectedTheme?.let { theme ->
//                                            themeRepository.purchaseTheme(theme.id)
//                                            themeRepository.setCurrentTheme(theme.id)
//                                            splashScreenViewModel.updateTheme(theme.id)
//                                            themes = themeRepository.getThemes()
//                                            selectedTheme = themes.find { it.id == theme.id }
//                                        }
//
//                                        purchaseState = PurchaseState.Success
//                                        delay(1500)
//                                        purchaseState = PurchaseState.Idle
//                                    }
//                                },
//                                colors = ButtonDefaults.buttonColors(containerColor = contentColor)
//                            ) {
//                                Text("Unlock", color = Color.Black)
//                            }
//
//                            Spacer(Modifier.width(12.dp))
//
//                            OutlinedButton(onClick = {
//                                scope.launch {
//                                    purchaseState = PurchaseState.Restore
//                                    delay(1000)
//                                    themes.forEach { themeRepository.purchaseTheme(it.id) }
//                                    themes = themeRepository.getThemes()
//                                    purchaseState = PurchaseState.Success
//                                    delay(1500)
//                                    purchaseState = PurchaseState.Idle
//                                }
//                            }) {
//                                Text("Unlock All", color = contentColor)
//                            }
//                        }
//                    }
//
//                    PurchaseState.Loading -> {
//                        CircularProgressIndicator(
//                            modifier = Modifier.padding(16.dp),
//                            color = contentColor
//                        )
//                    }
//
//                    PurchaseState.Failure -> {
//                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                            Text(
//                                "Error",
//                                style = MaterialTheme.typography.headlineMedium,
//                                color = contentColor
//                            )
//                            Button(
//                                onClick = { purchaseState = PurchaseState.Idle },
//                                colors = ButtonDefaults.buttonColors(containerColor = contentColor)
//                            ) {
//                                Text("Try Again", color = Color.Black)
//                            }
//                        }
//                    }
//
//                    PurchaseState.Restore -> {
//                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                            CircularProgressIndicator(color = contentColor)
//                            Text("Unlocking all themes...", color = contentColor)
//                        }
//                    }
//                }
//            }
//        }
//
//        if (purchaseState == PurchaseState.Success) {
//            ConfettiOverlay(currentTheme)
//        }
//    }
//}
//
//sealed class PurchaseState {
//    object Idle : PurchaseState()
//    object Loading : PurchaseState()
//    object Success : PurchaseState()
//    object Failure : PurchaseState()
//    object Restore : PurchaseState()
//}