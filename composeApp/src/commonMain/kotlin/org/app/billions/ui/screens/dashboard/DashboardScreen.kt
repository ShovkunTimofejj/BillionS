package org.app.billions.ui.screens.dashboard  /* TODO: открыть AddEntry */

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import billions.composeapp.generated.resources.Res
import billions.composeapp.generated.resources.app_logo
import org.jetbrains.compose.resources.painterResource
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.app.billions.ui.screens.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavHostController) {
    val Lime = Color(0xFF00FF00)
    var selectedTabIndex by remember { mutableStateOf(0) } // 0 - Dashboard, 1 - Challenges, 2 - Settings

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF001F3F),
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { /* TODO: открыть AddEntry */ }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Entry",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF001F3F)) {
                NavigationBarItem(
                    selected = selectedTabIndex == 0,
                    onClick = {
                        selectedTabIndex = 0
                        navController.navigate(Screen.MainMenuScreen.route) {
                            popUpTo(Screen.MainMenuScreen.route) { inclusive = true }
                        }
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                    label = { Text("Home", color = Color.White) }
                )
                NavigationBarItem(
                    selected = selectedTabIndex == 1,
                    onClick = {
                        selectedTabIndex = 1
                        navController.navigate("challenges") {
                            popUpTo(Screen.MainMenuScreen.route)
                        }
                    },
                    icon = { Icon(Icons.Default.Flag, contentDescription = "Challenges") },
                    label = { Text("Challenges", color = Color.White) }
                )
                NavigationBarItem(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings", color = Color.White) }
                )
            }
        },
        containerColor = Color(0xFF001F3F)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF002A55))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Lifetime Counter", color = Color.White, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("0 to a Billion", color = Lime, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Button(onClick = { /* TODO: share progress */ }) {
                            Text("Share Progress")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = { /* TODO: show stats */ }) {
                            Text("All-time Stats")
                        }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF002A55))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Today Rings", color = Color.White, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Steps", color = Color.White)
                            Text("0", color = Lime, fontSize = 20.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Distance", color = Color.White)
                            Text("0 km", color = Lime, fontSize = 20.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Calories", color = Color.White)
                            Text("0 kcal", color = Lime, fontSize = 20.sp)
                        }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF002A55))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Quick Add", color = Color.White, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = { /* TODO: add Steps */ }) { Text("+Steps") }
                        Button(onClick = { /* TODO: add Distance */ }) { Text("+Distance") }
                        Button(onClick = { /* TODO: add Calories */ }) { Text("+Calories") }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF002A55))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Highlights", color = Color.White, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Best day this week: 0 steps", color = Lime)
                    Text("X% to weekly target: 0%", color = Lime)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(Res.drawable.app_logo),
                    contentDescription = "Monocle Guy",
                    modifier = Modifier.size(150.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Let's add your first entry", color = Color.White)
            }
        }
    }
}

