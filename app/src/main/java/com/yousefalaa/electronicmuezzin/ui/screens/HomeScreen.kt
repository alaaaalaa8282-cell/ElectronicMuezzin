package com.yousefalaa.electronicmuezzin.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yousefalaa.electronicmuezzin.ui.MainViewModel
import com.yousefalaa.electronicmuezzin.utils.TimeFormatter
import java.util.Calendar

val GoldBg    = Color(0xFFD4A843)
val GoldCard  = Color(0xFFCB9A2E)
val GoldBorder2 = Color(0xFF8B6914)

data class HomeMenuItem(val emoji: String, val title: String, val route: String)

@Composable
fun HomeScreen(navController: NavController, viewModel: MainViewModel = hiltViewModel()) {
    val currentTime    by viewModel.currentTime.collectAsState()
    val hijriDate      by viewModel.hijriDate.collectAsState()
    val nextPrayer     by viewModel.nextPrayer.collectAsState()
    val prayerSettings by viewModel.prayerSettings.collectAsState()

    val cal = Calendar.getInstance().apply { timeInMillis = currentTime }
    val h   = cal.get(Calendar.HOUR).let { if (it == 0) 12 else it }
    val m   = cal.get(Calendar.MINUTE).toString().padStart(2, '0')
    val s   = cal.get(Calendar.SECOND).toString().padStart(2, '0')
    val ap  = if (cal.get(Calendar.HOUR_OF_DAY) < 12) "ص" else "م"

    val menuItems = listOf(
        HomeMenuItem("🕌", "مواقيت الصلاة",    "prayer_times"),
        HomeMenuItem("🧭", "اتجاة القبلة",     "qibla"),
        HomeMenuItem("📍", "اوقات الصلاة",     "prayer_times"),
        HomeMenuItem("🤲", "أذكار المسلم",     "adhkar"),
        HomeMenuItem("📖", "خاتم القران",       "quran_khatma"),
        HomeMenuItem("💝", "صدقة جارية",       "sadaqa"),
        HomeMenuItem("📿", "تسبيح",            "tasbih"),
        HomeMenuItem("🕋", "الصلاة على النبي", "salah_alnabi"),
        HomeMenuItem("🤲", "أذكار الصلاة",     "adhkar_list/AFTER_PRAYER"),
        HomeMenuItem("📤", "مشاركة التطبيق",   "share"),
        HomeMenuItem("⭐", "تقيم التطبيق",     "rate"),
        HomeMenuItem("⚙️", "اعدادات",          "settings")
    )

    Column(modifier = Modifier.fillMaxSize().background(GoldBg)) {

        // ── رأس الصفحة ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(colors = listOf(Color(0xFFB8860B), Color(0xFFD4A843))))
                .padding(vertical = 12.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$h:$m:$s $ap", fontSize = 32.sp, color = Color.White, fontWeight = FontWeight.Bold)
                Text(hijriDate.toArabicString(), fontSize = 14.sp, color = Color(0xFFFFF8DC))
                nextPrayer?.let { (name, time) ->
                    val diff = time - currentTime
                    if (diff > 0) Text(
                        "صلاة $name بعد ${TimeFormatter.formatCountdown(diff)}",
                        fontSize = 13.sp, color = Color(0xFFFFF0A0), fontWeight = FontWeight.Bold
                    )
                }
                if (prayerSettings.cityName.isNotEmpty())
                    Text("📍 ${prayerSettings.cityName}", fontSize = 12.sp, color = Color(0xFFFFE57F))
            }
        }

        // ── شبكة الأزرار ──
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize().padding(8.dp),
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(menuItems) { item ->
                HomeMenuCard(item = item, onClick = {
                    if (item.route !in listOf("share","rate","sadaqa"))
                        navController.navigate(item.route)
                })
            }
        }
    }
}

@Composable
fun HomeMenuCard(item: HomeMenuItem, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(0.9f)
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.verticalGradient(colors = listOf(Color(0xFFF5D080), GoldCard)))
            .border(
                width = 2.dp,
                brush = Brush.verticalGradient(colors = listOf(Color(0xFFFFE0A0), GoldBorder2)),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.verticalGradient(colors = listOf(Color(0xFFE8C060), Color(0xFFB8900A))))
                    .border(1.dp, GoldBorder2, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(item.emoji, fontSize = 32.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFB8860B))
                    .padding(horizontal = 4.dp, vertical = 3.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.title,
                    fontSize = 11.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

