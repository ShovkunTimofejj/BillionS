package org.app.billions.ui.screens.inAppPurchase

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.app.billions.data.model.Theme
import org.app.billions.data.repository.ThemeRepository
import org.app.billions.ui.screens.journa.ConfettiOverlay
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InAppPurchaseScreen(
    navController: NavHostController,
    billingRepository: BillingRepository
) {
    val scope = rememberCoroutineScope()
    var themes by remember { mutableStateOf<List<Theme>>(emptyList()) }
    var selectedTheme by remember { mutableStateOf<Theme?>(null) }
    var purchaseState by remember { mutableStateOf<PurchaseState>(PurchaseState.Idle) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        themes = billingRepository.getThemes()
        selectedTheme = themes.firstOrNull()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Themes Store") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(themes) { theme ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .clickable { selectedTheme = theme },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedTheme?.id == theme.id) MaterialTheme.colorScheme.primaryContainer else Color.White
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier
                                    .size(60.dp)
                                    .background(Color.Gray, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(theme.name.take(1))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(theme.name, style = MaterialTheme.typography.titleMedium)
                                if (theme.isPurchased) {
                                    Text("Purchased", color = Color.Green)
                                } else {
                                    Text("$1.99", color = Color.Red)
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
                                            PurchaseState.Success
                                        }
                                        is PurchaseResult.Failure -> PurchaseState.Failure
                                        is PurchaseResult.Error -> {
                                            errorMessage = result.message
                                            PurchaseState.Failure
                                        }
                                    }
                                }
                            }
                        ) {
                            Text("Buy")
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
                            Text("Restore")
                        }
                    }
                }

                PurchaseState.Loading -> {
                    CircularProgressIndicator(Modifier.padding(16.dp))
                }

                PurchaseState.Success -> {
                    ConfettiOverlay()
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Purchase Successful", style = MaterialTheme.typography.headlineMedium)
                        Text("Theme applied. Premium challenges unlocked.")
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Done")
                        }
                    }
                }

                PurchaseState.Failure -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Purchase Failed", style = MaterialTheme.typography.headlineMedium)
                        Text(errorMessage ?: "Something went wrong.")
                        Row {
                            Button(onClick = { purchaseState = PurchaseState.Idle }) {
                                Text("Try Again")
                            }
                            Spacer(Modifier.width(8.dp))
                            OutlinedButton(onClick = {
                                navController.popBackStack()
                            }) {
                                Text("Close")
                            }
                        }
                    }
                }

                PurchaseState.Restore -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Text("Restoring purchases...")
                    }
                }
            }
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