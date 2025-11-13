package org.app.billions.ui.screens.settings

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import billions.composeapp.generated.resources.Res
import billions.composeapp.generated.resources.bg_dashboard_dark_lime
import billions.composeapp.generated.resources.bg_dashboard_graphite_gold
import billions.composeapp.generated.resources.bg_dashboard_neon_coral
import billions.composeapp.generated.resources.bg_dashboard_royal_blue
import billions.composeapp.generated.resources.ic_logo_dark_lime
import billions.composeapp.generated.resources.ic_logo_graphite_gold
import billions.composeapp.generated.resources.ic_logo_neon_coral
import billions.composeapp.generated.resources.ic_logo_royal_blue
import billions.composeapp.generated.resources.ic_minus
import billions.composeapp.generated.resources.ic_plus
import billions.composeapp.generated.resources.theme_dark_lime
import billions.composeapp.generated.resources.theme_graphite_gold
import billions.composeapp.generated.resources.theme_graphite_gold_locked
import billions.composeapp.generated.resources.theme_neon_coral
import billions.composeapp.generated.resources.theme_neon_coral_locked
import billions.composeapp.generated.resources.theme_royal_blue
import billions.composeapp.generated.resources.theme_royal_blue_locked
import kotlinx.coroutines.launch
import org.app.billions.data.model.DailyGoals
import org.app.billions.data.model.Subscription
import org.app.billions.data.model.Theme
import org.app.billions.data.model.Units
import org.app.billions.data.model.User
import org.app.billions.data.repository.ThemeRepository
import org.app.billions.data.repository.UserRepository
import org.app.billions.notifications.NotificationsManager
import org.app.billions.ui.screens.Screen
import org.app.billions.ui.screens.buttonBar.AppBottomBar
import org.app.billions.ui.screens.viewModel.JournalViewModel
import org.app.billions.ui.screens.viewModel.SplashScreenViewModel
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToLong
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    splashScreenViewModel: SplashScreenViewModel,
    userRepository: UserRepository = koinInject(),
    themeRepository: ThemeRepository = koinInject(),
    journalViewModel: JournalViewModel = koinInject()
) {

    val scope = rememberCoroutineScope()
    var user by remember { mutableStateOf<User?>(null) }
    var showResetDialog by remember { mutableStateOf(false) }

    val uiState by splashScreenViewModel.uiState.collectAsState()
    val currentTheme = uiState.currentTheme
    val themes = uiState.themes
    var stepGoal by remember { mutableStateOf(journalViewModel.dailyGoals.value.stepGoal) }
    var distanceGoal by remember { mutableStateOf(journalViewModel.dailyGoals.value.distanceGoal) }
    var calorieGoal by remember { mutableStateOf(journalViewModel.dailyGoals.value.calorieGoal) }
    val notificationsManager: NotificationsManager = koinInject()
    var showTimePicker by remember { mutableStateOf(false) }

    val challengeEnabled = uiState.challengeReminderEnabled
    val dailyTime = formatTime(uiState.dailyReminderHour, uiState.dailyReminderMinute)

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

    val barColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0x801C2A3A)
        "neon_coral" -> Color(0x80431C2E)
        "royal_blue" -> Color(0x801D3B5C)
        "graphite_gold" -> Color(0x80383737)
        else -> Color(0x801C2A3A)
    }

    val cardColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFF1C2A3A)
        "neon_coral" -> Color(0xFF431C2E)
        "royal_blue" -> Color(0xFF1D3B5C)
        "graphite_gold" -> Color(0xFF383737)
        else -> Color(0xFF1C2A3A)
    }

    val contentColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFF00FF00)
        "neon_coral" -> Color(0xFFFF8FA0)
        "royal_blue" -> Color(0xFF00BFFF)
        "graphite_gold" -> Color(0xFFB59F00)
        else -> Color(0xFF00FF00)
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var selectedBottomNavIndex by remember { mutableStateOf(3) }

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
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Settings",
                                color = Color.White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                )
            },
            bottomBar = {
                AppBottomBar(
                    navController = navController,
                    selectedTabIndex = selectedBottomNavIndex,
                    onTabSelected = { selectedBottomNavIndex = it },
                    barColor = barColor,
                    currentTheme = currentTheme

                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                item {
                    SettingsSection("Appearance", Color.White) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            themes.forEach { theme ->
                                val imageRes = when (theme.id) {
                                    "dark_lime" -> Res.drawable.theme_dark_lime
                                    "neon_coral" -> if (theme.isPurchased) Res.drawable.theme_neon_coral else Res.drawable.theme_neon_coral_locked
                                    "royal_blue" -> if (theme.isPurchased) Res.drawable.theme_royal_blue else Res.drawable.theme_royal_blue_locked
                                    "graphite_gold" -> if (theme.isPurchased) Res.drawable.theme_graphite_gold else Res.drawable.theme_graphite_gold_locked
                                    else -> Res.drawable.theme_dark_lime
                                }

                                Image(
                                    painter = painterResource(imageRes),
                                    contentDescription = "Theme ${theme.id}",
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            if (theme.isPurchased) {
                                                scope.launch {
                                                    splashScreenViewModel.updateTheme(theme.id)
                                                }
                                            } else {
                                                navController.navigate(Screen.InAppPurchaseScreen.route)
                                            }
                                        }
                                )
                            }
                        }
                    }
                }

                val sectionBackgroundColor = when (currentTheme?.id) {
                    "dark_lime" -> Color(0xFF0F1A2A)
                    "neon_coral" -> Color(0xFF331226)
                    "royal_blue" -> Color(0xFF0D223C)
                    "graphite_gold" -> Color(0xFF2C2A2A)
                    else -> Color(0xFF0F1A2A)
                }

                val sectionContentColor = when (currentTheme?.id) {
                    "dark_lime" -> Color(0xFF00FF00)
                    "neon_coral" -> Color(0xFFFF8FA0)
                    "royal_blue" -> Color(0xFF00BFFF)
                    "graphite_gold" -> Color(0xFFB59F00)
                    else -> Color(0xFF00FF00)
                }

                item {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {

                        Text(
                            text = "Daily Goals:",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(sectionBackgroundColor, RoundedCornerShape(16.dp))
                                .padding(horizontal = 20.dp, vertical = 22.dp)
                        ) {

                            Column(verticalArrangement = Arrangement.spacedBy(22.dp)) {

                                GoalRowWithCircle(
                                    label = "Steps",
                                    value = stepGoal,
                                    suffix = "",
                                    min = 1000,
                                    max = 50000,
                                    step = 100,
                                    contentColor = sectionContentColor
                                ) {
                                    stepGoal = it
                                    journalViewModel.updateGoals(
                                        DailyGoals(stepGoal, distanceGoal, calorieGoal)
                                    )
                                }

                                GoalRowWithCircle(
                                    label = "Distance",
                                    value = distanceGoal,
                                    suffix = " km",
                                    min = 100,
                                    max = 50000,
                                    step = 100,
                                    contentColor = sectionContentColor
                                ) {
                                    distanceGoal = it
                                    journalViewModel.updateGoals(
                                        DailyGoals(stepGoal, distanceGoal, calorieGoal)
                                    )
                                }

                                GoalRowWithCircle(
                                    label = "Calories",
                                    value = calorieGoal,
                                    suffix = " kcal",
                                    min = 100,
                                    max = 10000,
                                    step = 50,
                                    contentColor = sectionContentColor
                                ) {
                                    calorieGoal = it
                                    journalViewModel.updateGoals(
                                        DailyGoals(stepGoal, distanceGoal, calorieGoal)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {

                        Text(
                            text = "Notifications:",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Text(
                                text = "Daily Reminder",
                                color = Color.White,
                                fontSize = 16.sp
                            )

                            Box(
                                modifier = Modifier
                                    .background(Color.Transparent, RoundedCornerShape(10.dp))
                                    .clickable {
                                        showTimePicker = true
                                    }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = dailyTime,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        if (showTimePicker) {
                            DailyReminderTimePicker(
                                currentTheme = currentTheme,
                                initialHour = uiState.dailyReminderHour,
                                initialMinute = uiState.dailyReminderMinute,
                                onConfirm = { h, m ->
                                    splashScreenViewModel.updateDailyReminder(h, m)
                                    showTimePicker = false
                                },
                                onDismiss = { showTimePicker = false }
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Text(
                                text = "Challenge Reminder",
                                color = Color.White,
                                fontSize = 16.sp
                            )

                            Box(
                                modifier = Modifier
                                    .background(
                                        color = if (challengeEnabled) Color(0xFF7EB1FF) else Color.Gray.copy(alpha = 0.35f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        splashScreenViewModel.toggleChallengeReminder()
                                    }
                                    .padding(horizontal = 20.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = if (challengeEnabled) "ON" else "OFF",
                                    color = if (challengeEnabled) Color.Black else Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }


                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent, RoundedCornerShape(12.dp))
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    ) {
                        Column {

                            Text(
                                text = "Data",
                                color = Color.White,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(20.dp)
                            ) {

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(Color.White)
                                        .clickable { journalViewModel.exportCsv() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Export CSV",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(Color.Red)
                                        .clickable { showResetDialog = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Reset All Data",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent, RoundedCornerShape(12.dp))
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val logoRes = when (currentTheme?.id) {
                                "dark_lime" -> Res.drawable.ic_logo_dark_lime
                                "neon_coral" -> Res.drawable.ic_logo_neon_coral
                                "royal_blue" -> Res.drawable.ic_logo_royal_blue
                                "graphite_gold" -> Res.drawable.ic_logo_graphite_gold
                                else -> Res.drawable.ic_logo_dark_lime
                            }

                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.Transparent)
                                    .clickable { navController.navigate(Screen.AboutScreen.route) }
                                    .padding(horizontal = 20.dp, vertical = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                Image(
                                    painter = painterResource(logoRes),
                                    contentDescription = "App logo",
                                    modifier = Modifier
                                        .size(70.dp)
                                        .padding(bottom = 4.dp)
                                )

                                Text(
                                    text = "App Version 1.1",
                                    color = sectionContentColor,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            if (showResetDialog) {
                AlertDialog(
                    onDismissRequest = { showResetDialog = false },

                    containerColor = cardColor,
                    tonalElevation = 0.dp,
                    shape = RoundedCornerShape(16.dp),

                    title = {
                        Text(
                            text = "Are you sure you want to delete this data?",
                            color = Color.White,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },

                    text = {
                        Text(
                            text = "This action cannot be undone.",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        )
                    },

                    confirmButton = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {

                            Box(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(45.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .clickable { showResetDialog = false },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Cancel",
                                    color = Color.Black,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Box(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(45.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(contentColor)
                                    .clickable {
                                        scope.launch {
                                            userRepository.saveUser(
                                                User(
                                                    id = 0L,
                                                    nickname = "User",
                                                    avatar = "",
                                                    units = Units.Metric,
                                                    timezone = "UTC",
                                                    subscription = Subscription.Free
                                                )
                                            )
                                            journalViewModel.state.value.entries.forEach { entry ->
                                                journalViewModel.deleteEntry(entry.id)
                                            }
                                        }
                                        showResetDialog = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Delete",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    },

                    dismissButton = {}
                )
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    contentColor: Color,
    content: @Composable () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            color = contentColor
        )
        content()
    }
}

@Composable
fun GoalRowWithCircle(
    label: String,
    value: Long,
    suffix: String = "",
    min: Long,
    max: Long,
    step: Long,
    contentColor: Color,
    onChange: (Long) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = label,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(
                text = value.toString() + suffix,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 16.dp)
            )

            MiniRingSlider(
                value = value,
                range = min..max,
                step = step,
                color = contentColor,
                onValueChange = onChange
            )
        }
    }
}

@Composable
fun MiniRingSlider(
    value: Long,
    range: LongRange,
    step: Long,
    color: Color,
    size: Dp = 48.dp,
    onValueChange: (Long) -> Unit
) {
    val min = range.first
    val max = range.last

    var progress by remember {
        mutableStateOf(
            ((value.coerceIn(min, max) - min).toFloat() / (max - min))
        )
    }

    LaunchedEffect(value, min, max) {
        progress = ((value.coerceIn(min, max) - min).toFloat() / (max - min))
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val center = Offset(size.toPx() / 2, size.toPx() / 2)
                    val touch = change.position - center

                    val angle = atan2(touch.y, touch.x) * 180f / PI.toFloat()
                    val normalized = ((angle + 450f) % 360f) / 360f

                    progress = normalized

                    val raw = min + progress * (max - min)

                    val stepsFromMin = ((raw - min) / step.toFloat()).roundToLong()
                    val snapped = (min + stepsFromMin * step)
                        .coerceIn(min, max)

                    progress = (snapped - min).toFloat() / (max - min)

                    onValueChange(snapped)
                }
            }
    ) {
        Canvas(modifier = Modifier.size(size)) {

            val strokeWidth = 6.dp.toPx()
            val radius = (size.toPx() - strokeWidth) / 2f
            val center = Offset(size.toPx() / 2, size.toPx() / 2)

            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360 * progress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            val angleDeg = progress * 360 - 90
            val angleRad = angleDeg * PI.toFloat() / 180f

            val endX = center.x + cos(angleRad) * radius
            val endY = center.y + sin(angleRad) * radius

            val offsetShift = strokeWidth / 2f
            val end = Offset(
                endX + cos(angleRad) * offsetShift,
                endY + sin(angleRad) * offsetShift
            )

            val handleLength = 12.dp.toPx()
            val p1 = Offset(end.x - handleLength / 2, end.y)
            val p2 = Offset(end.x + handleLength / 2, end.y)

            drawLine(
                color = color,
                start = p1,
                end = p2,
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

fun Double.toRadians(): Double = this * PI / 180

fun formatTime(hour: Int, minute: Int): String {
    val h = if (hour < 10) "0$hour" else "$hour"
    val m = if (minute < 10) "0$minute" else "$minute"
    return "$h : $m"
}

@Composable
fun DailyReminderTimePicker(
    currentTheme: Theme?,
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var hour by remember { mutableStateOf(initialHour) }
    var minute by remember { mutableStateOf(initialMinute) }

    val cardColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFF1C2A3A)
        "neon_coral" -> Color(0xFF431C2E)
        "royal_blue" -> Color(0xFF1D3B5C)
        "graphite_gold" -> Color(0xFF383737)
        else -> Color(0xFF1C2A3A)
    }

    val contentColor = when (currentTheme?.id) {
        "dark_lime" -> Color(0xFF00FF00)
        "neon_coral" -> Color(0xFFFF8FA0)
        "royal_blue" -> Color(0xFF00BFFF)
        "graphite_gold" -> Color(0xFFB59F00)
        else -> Color(0xFF00FF00)
    }

    val gradient = when (currentTheme?.id) {
        "dark_lime" -> listOf(Color(0xFF0F1C27), Color(0xFF1C2A3A))
        "neon_coral" -> listOf(Color(0xFF2A0F1B), Color(0xFF431C2E))
        "royal_blue" -> listOf(Color(0xFF112237), Color(0xFF1D3B5C))
        "graphite_gold" -> listOf(Color(0xFF1F1E1E), Color(0xFF383737))
        else -> listOf(Color(0xFF0F1C27), Color(0xFF1C2A3A))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.background(Color.Transparent),
        containerColor = Color.Transparent,
        title = {},
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(gradient),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(18.dp)
            ) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(
                        text = "Choose Time",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = "${hour.toString().padStart(2, '0')} : ${minute.toString().padStart(2, '0')}",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Hour", color = Color.White)
                            Row(verticalAlignment = Alignment.CenterVertically) {

                                IconButton(onClick = {
                                    hour = (hour + 23) % 24
                                }) { Text("-", color = Color.White) }

                                Text(
                                    hour.toString().padStart(2, '0'),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )

                                IconButton(onClick = {
                                    hour = (hour + 1) % 24
                                }) { Text("+", color = Color.White) }
                            }
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Minute", color = Color.White)
                            Row(verticalAlignment = Alignment.CenterVertically) {

                                IconButton(onClick = {
                                    minute = (minute + 59) % 60
                                }) { Text("-", color = Color.White) }

                                Text(
                                    minute.toString().padStart(2, '0'),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )

                                IconButton(onClick = {
                                    minute = (minute + 1) % 60
                                }) { Text("+", color = Color.White) }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Box(
                modifier = Modifier
                    .height(50.dp)
                    .width(120.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White)
                    .clickable { onConfirm(hour, minute) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "OK",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },

        dismissButton = {
            Box(
                modifier = Modifier
                    .height(50.dp)
                    .width(140.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White)
                    .clickable(onClick = onDismiss)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Cancel",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    )
}
