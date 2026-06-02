package com.yousefalaa.electronicmuezzin.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yousefalaa.electronicmuezzin.ui.screens.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home")          { HomeScreen(navController) }
        composable("prayer_times")  { PrayerTimesScreen(navController) }
        composable("adhkar")        { AdhkarScreen(navController) }
        composable("qibla")         { QiblaScreen() }
        composable("calendar")      { CalendarScreen() }
        composable("more")          { MoreScreen(navController) }
        composable("tasbih")        { TasbihScreen(navController) }
        composable("ramadan")       { RamadanScreen(navController) }
        composable("quran_khatma")  { QuranSurahIndexScreen(navController) }
        composable("settings")      { SettingsScreen(navController) }
        composable("salah_alnabi")  { SalahAlNabiScreen(navController) }
        composable("adhkar_list/{category}") { backStack ->
            val category = backStack.arguments?.getString("category") ?: "MORNING"
            AdhkarListScreen(category, navController)
        }
    }
}
