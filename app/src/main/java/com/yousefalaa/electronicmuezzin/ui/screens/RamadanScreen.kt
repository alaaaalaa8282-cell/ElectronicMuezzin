package com.yousefalaa.electronicmuezzin.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yousefalaa.electronicmuezzin.ui.MainViewModel
import com.yousefalaa.electronicmuezzin.ui.theme.*
import com.yousefalaa.electronicmuezzin.utils.HijriCalendar
import com.yousefalaa.electronicmuezzin.utils.TimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RamadanScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val hijriToday = HijriCalendar.today()
    val isRamadan = hijriToday.isRamadan()
    val prayerTimes by viewModel.prayerTimes.collectAsState()
    val currentTime by viewModel.currentTime.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("شهر رمضان المبارك 🌙", style = MaterialTheme.typography.titleLarge, color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D1B2A))
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0A1628), Color(0xFF0D1B2A))
                    )
                )
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // بطاقة اليوم الهجري
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isRamadan) Color(0xFF1A3A1A) else Color(0xFF1A2E40)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isRamadan) GreenLight.copy(alpha = 0.5f) else GoldDark.copy(alpha = 0.4f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🌙", fontSize = 48.sp)

                        if (isRamadan) {
                            Text(
                                text = "رمضان مبارك 🌙",
                                style = MaterialTheme.typography.headlineMedium,
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "اليوم ${hijriToday.day} من رمضان",
                                style = MaterialTheme.typography.titleLarge,
                                color = GreenLight
                            )
                            Text(
                                text = "تقبل الله منا ومنكم",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFAAAAAA)
                            )
                        } else {
                            // حساب الأيام حتى رمضان
                            val daysToRamadan = calcDaysToRamadan(hijriToday)
                            Text(
                                text = "رمضان قادم",
                                style = MaterialTheme.typography.headlineMedium,
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "باقي $daysToRamadan يوم",
                                style = MaterialTheme.typography.displayMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = hijriToday.toArabicString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF888888)
                            )
                        }
                    }
                }

                if (isRamadan && prayerTimes != null) {
                    val times = prayerTimes!!

                    // وقت السحور (قبل الفجر بـ 30 دقيقة)
                    val suhoorTime = times.fajr - 30 * 60 * 1000L
                    // وقت الإفطار = المغرب
                    val iftarTime = times.maghrib

                    // بطاقة السحور
                    RamadanTimeCard(
                        emoji = "🌙",
                        title = "السحور",
                        time = TimeFormatter.formatTime(suhoorTime),
                        subtitle = "آخر وقت للسحور",
                        isPast = suhoorTime < currentTime,
                        color = Color(0xFF1A2E40),
                        borderColor = GoldPrimary
                    )

                    // بطاقة الإفطار
                    RamadanTimeCard(
                        emoji = "🌅",
                        title = "الإفطار",
                        time = TimeFormatter.formatTime(iftarTime),
                        subtitle = "موعد الإفطار - أذان المغرب",
                        isPast = iftarTime < currentTime,
                        color = Color(0xFF1A2E40),
                        borderColor = GreenLight
                    )

                    // دعاء الإفطار
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2E40)),
                        border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "دعاء الإفطار",
                                style = MaterialTheme.typography.titleMedium,
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Divider(color = GoldDark.copy(alpha = 0.3f))
                            Text(
                                text = "اللَّهُمَّ لَكَ صُمْتُ وَعَلَى رِزْقِكَ أَفْطَرْتُ",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                lineHeight = 36.sp
                            )
                            Text(
                                text = "رواه أبو داود",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF888888)
                            )
                        }
                    }
                }

                // فضائل رمضان
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2E40)),
                    border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "✨ فضائل رمضان",
                            style = MaterialTheme.typography.titleMedium,
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Divider(color = GoldDark.copy(alpha = 0.3f))
                        Text(
                            text = "شَهْرُ رَمَضَانَ الَّذِي أُنزِلَ فِيهِ الْقُرْآنُ هُدًى لِّلنَّاسِ وَبَيِّنَاتٍ مِّنَ الْهُدَىٰ وَالْفُرْقَانِ",
                            style = MaterialTheme.typography.bodyLarge,
                            color = GoldLight,
                            textAlign = TextAlign.Center,
                            lineHeight = 30.sp
                        )
                        Text(
                            text = "سورة البقرة: 185",
                            style = MaterialTheme.typography.bodySmall,
                            color = GreenLight
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RamadanTimeCard(
    emoji: String,
    title: String,
    time: String,
    subtitle: String,
    isPast: Boolean,
    color: Color,
    borderColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        border = BorderStroke(1.dp, if (isPast) Color(0xFF444444) else borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = time,
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (isPast) Color(0xFF666666) else borderColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF888888)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isPast) Color(0xFF666666) else Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(text = emoji, fontSize = 32.sp)
            }
        }
    }
}

private fun calcDaysToRamadan(hijri: HijriCalendar.HijriDate): Int {
    // رمضان هو الشهر 9
    val currentMonth = hijri.month
    val currentDay = hijri.day
    return if (currentMonth < 9) {
        val daysLeft = (9 - currentMonth) * 29 - currentDay + 1
        daysLeft.coerceAtLeast(1)
    } else {
        val daysLeft = (12 - currentMonth + 9) * 29 - currentDay + 1
        daysLeft.coerceAtLeast(1)
    }
}
