package com.yousefalaa.electronicmuezzin.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yousefalaa.electronicmuezzin.ui.screens.*
import com.yousefalaa.electronicmuezzin.ui.theme.GoldPrimary

sealed class Screen(val route: String, val titleAr: String, val icon: ImageVector) {
    object PrayerTimes : Screen("prayer_times", "الصلاة", Icons.Default.AccessTime)
    object Adhkar : Screen("adhkar", "الأذكار", Icons.Default.MenuBook)
    object Qibla : Screen("qibla", "القبلة", Icons.Default.Explore)
    object Calendar : Screen("calendar", "التقويم", Icons.Default.CalendarMonth)
    object More : Screen("more", "المزيد", Icons.Default.GridView)
}

val bottomNavItems = listOf(
    Screen.PrayerTimes,
    Screen.Adhkar,
    Screen.Qibla,
    Screen.Calendar,
    Screen.More
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDest = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = androidx.compose.ui.graphics.Color(0xFF1A2E40),
                contentColor = GoldPrimary
            ) {
                bottomNavItems.forEach { screen ->
                    val selected = currentDest?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        icon = {
                            Icon(
                                screen.icon,
                                contentDescription = screen.titleAr
                            )
                        },
                        label = {
                            Text(
                                screen.titleAr,
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GoldPrimary,
                            selectedTextColor = GoldPrimary,
                            unselectedIconColor = androidx.compose.ui.graphics.Color(0xFF888888),
                            unselectedTextColor = androidx.compose.ui.graphics.Color(0xFF888888),
                            indicatorColor = androidx.compose.ui.graphics.Color(0xFF0D3349)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.PrayerTimes.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.PrayerTimes.route) {
                PrayerTimesScreen(navController)
            }
            composable(Screen.Adhkar.route) {
                AdhkarScreen(navController)
            }
            composable(Screen.Qibla.route) {
                QiblaScreen()
            }
            composable(Screen.Calendar.route) {
                CalendarScreen()
            }
            composable(Screen.More.route) {
                MoreScreen(navController)
            }
            composable("adhkar_list/{category}") { backStack ->
                val category = backStack.arguments?.getString("category") ?: "MORNING"
                AdhkarListScreen(category, navController)
            }
            composable("tasbih") {
                TasbihScreen(navController)
            }
            composable("ramadan") {
                RamadanScreen(navController)
            }
            composable("quran_khatma") {
                QuranKhatmaScreen(navController)
            }
            composable("settings") {
                SettingsScreen(navController)
            }
        }
    }
}
