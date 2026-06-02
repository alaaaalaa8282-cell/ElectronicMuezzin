package com.yousefalaa.electronicmuezzin.ui.navigation

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yousefalaa.electronicmuezzin.ui.screens.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("muezzin_prefs", Context.MODE_PRIVATE)
    val isFirstLaunch = prefs.getBoolean("first_launch", true)

    NavHost(
        navController = navController,
        startDestination = if (isFirstLaunch) "onboarding" else "home"
    ) {
        composable("onboarding") {
            OnboardingScreen(navController) {
                prefs.edit().putBoolean("first_launch", false).apply()
                navController.navigate("home") { popUpTo("onboarding") { inclusive = true } }
            }
        }
        composable("home")         { HomeScreen(navController) }
        composable("prayer_times") { PrayerTimesScreen(navController) }
        composable("adhkar")       { AdhkarScreen(navController) }
        composable("qibla")        { QiblaScreen() }
        composable("calendar")     { CalendarScreen() }
        composable("tasbih")       { TasbihScreen(navController) }
        composable("ramadan")      { RamadanScreen(navController) }
        composable("quran_khatma") { QuranSurahIndexScreen(navController) }
        composable("settings")     { SettingsScreen(navController) }
        composable("salah_alnabi") { SalahAlNabiScreen(navController) }
        composable("qiyam")        { QiyamScreen(navController) }
        composable("fasting")      { FastingScreen(navController) }
        composable("friday")       { FridayScreen(navController) }
        composable("eid_takberat") { EidTakberatScreen(navController) }
        composable("adhkar_list/{category}") { backStack ->
            val category = backStack.arguments?.getString("category") ?: "MORNING"
            AdhkarListScreen(category, navController)
        }
    }
}
