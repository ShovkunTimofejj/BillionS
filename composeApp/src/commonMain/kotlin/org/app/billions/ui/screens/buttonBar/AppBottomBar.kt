package org.app.billions.ui.screens.buttonBar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import billions.composeapp.generated.resources.Res
import billions.composeapp.generated.resources.ic_challenges_dark_lime
import billions.composeapp.generated.resources.ic_challenges_default
import billions.composeapp.generated.resources.ic_challenges_graphite_gold
import billions.composeapp.generated.resources.ic_challenges_neon_coral
import billions.composeapp.generated.resources.ic_challenges_royal_blue
import billions.composeapp.generated.resources.ic_home_dark_lime
import billions.composeapp.generated.resources.ic_home_default
import billions.composeapp.generated.resources.ic_home_graphite_gold
import billions.composeapp.generated.resources.ic_home_neon_coral
import billions.composeapp.generated.resources.ic_home_royal_blue
import billions.composeapp.generated.resources.ic_journal_dark_lime
import billions.composeapp.generated.resources.ic_journal_default
import billions.composeapp.generated.resources.ic_journal_graphite_gold
import billions.composeapp.generated.resources.ic_journal_neon_coral
import billions.composeapp.generated.resources.ic_journal_royal_blue
import billions.composeapp.generated.resources.ic_settings_dark_lime
import billions.composeapp.generated.resources.ic_settings_default
import billions.composeapp.generated.resources.ic_settings_graphite_gold
import billions.composeapp.generated.resources.ic_settings_neon_coral
import billions.composeapp.generated.resources.ic_settings_royal_blue
import org.app.billions.data.model.Theme
import org.app.billions.ui.screens.Screen
import org.jetbrains.compose.resources.painterResource

@Composable
fun AppBottomBar(
    navController: NavController,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    barColor: Color,
    currentTheme: Theme?
) {
    val activeIcons = remember(currentTheme) {
        when (currentTheme?.id) {
            "dark_lime" -> listOf(
                Res.drawable.ic_home_dark_lime,
                Res.drawable.ic_challenges_dark_lime,
                Res.drawable.ic_journal_dark_lime,
                Res.drawable.ic_settings_dark_lime
            )
            "neon_coral" -> listOf(
                Res.drawable.ic_home_neon_coral,
                Res.drawable.ic_challenges_neon_coral,
                Res.drawable.ic_journal_neon_coral,
                Res.drawable.ic_settings_neon_coral
            )
            "royal_blue" -> listOf(
                Res.drawable.ic_home_royal_blue,
                Res.drawable.ic_challenges_royal_blue,
                Res.drawable.ic_journal_royal_blue,
                Res.drawable.ic_settings_royal_blue
            )
            "graphite_gold" -> listOf(
                Res.drawable.ic_home_graphite_gold,
                Res.drawable.ic_challenges_graphite_gold,
                Res.drawable.ic_journal_graphite_gold,
                Res.drawable.ic_settings_graphite_gold
            )
            else -> listOf(
                Res.drawable.ic_home_dark_lime,
                Res.drawable.ic_challenges_dark_lime,
                Res.drawable.ic_journal_dark_lime,
                Res.drawable.ic_settings_dark_lime
            )
        }
    }

    val defaultIcons = listOf(
        Res.drawable.ic_home_default,
        Res.drawable.ic_challenges_default,
        Res.drawable.ic_journal_default,
        Res.drawable.ic_settings_default
    )

    val routes = listOf(
        Screen.MainMenuScreen.route,
        Screen.ChallengesScreen.route,
        Screen.JournalScreen.route,
        Screen.SettingsScreen.route
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        NavigationBar(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp)),
            containerColor = barColor,
            tonalElevation = 0.dp
        ) {
            routes.forEachIndexed { index, route ->
                val iconRes = if (selectedTabIndex == index)
                    activeIcons[index]
                else
                    defaultIcons[index]

                NavigationBarItem(
                    selected = selectedTabIndex == index,
                    onClick = {
                        onTabSelected(index)
                        navController.navigate(route) {
                            popUpTo(Screen.MainMenuScreen.route)
                        }
                    },
                    icon = {
                        Image(
                            painter = painterResource(iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(60.dp)
                        )
                    },
                    label = null,
                    alwaysShowLabel = false,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Transparent,
                        unselectedIconColor = Color.Transparent,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}
