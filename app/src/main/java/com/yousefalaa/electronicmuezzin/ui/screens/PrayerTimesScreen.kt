package com.yousefalaa.electronicmuezzin.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yousefalaa.electronicmuezzin.data.models.PrayerTimesModel
import com.yousefalaa.electronicmuezzin.ui.MainViewModel
import com.yousefalaa.electronicmuezzin.ui.components.MosqueClockHeader
import com.yousefalaa.electronicmuezzin.ui.components.PrayerTimeRow
import com.yousefalaa.electronicmuezzin.ui.theme.*
import com.yousefalaa.electronicmuezzin.utils.HijriCalendar
import com.yousefalaa.electronicmuezzin.utils.TimeFormatter
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PrayerTimesScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val prayerTimes by viewModel.prayerTimes.collectAsState()
    val currentTime by viewModel.currentTime.collectAsState()
    val hijriDate by viewModel.hijriDate.collectAsState()
    val nextPrayer by viewModel.nextPrayer.collectAsState()
    val isLocationSet by viewModel.isLocationSet.collectAsState()
    val prayerSettings by viewModel.prayerSettings.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A1628),
                        Color(0xFF0D1B2A),
                        Color(0xFF111F30)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // رأس الصفحة - ساعة المسجد
            MosqueClockHeader(
                currentTime = currentTime,
                hijriDate = hijriDate,
                cityName = prayerSettings.cityName,
                nextPrayer = nextPrayer
            )

            Spacer(modifier = Modifier.height(12.dp))

            // التاريخ الميلادي
            val dateFormat = SimpleDateFormat("EEEE، d MMMM yyyy", Locale("ar"))
            Text(
                text = dateFormat.format(Date(currentTime)),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFAAAAAA),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // بطاقة مواقيت الصلاة
            if (!isLocationSet) {
                LocationPromptCard(viewModel)
            } else {
                PrayerTimesCard(
                    prayerTimes = prayerTimes,
                    currentTime = currentTime,
                    nextPrayer = nextPrayer
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PrayerTimesCard(
    prayerTimes: PrayerTimesModel?,
    currentTime: Long,
    nextPrayer: Pair<String, Long>?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A2E40)
        ),
        border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            // عنوان البطاقة
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF0D3349),
                                Color(0xFF1A4A60),
                                Color(0xFF0D3349)
                            )
                        ),
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🕌  مواقيت الصلاة  🕌",
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            // فاصل ذهبي
            Divider(color = GoldPrimary.copy(alpha = 0.4f), thickness = 1.dp)

            if (prayerTimes == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GoldPrimary)
                }
            } else {
                val prayers = listOf(
                    Triple("الفجر", prayerTimes.fajr, "🌙"),
                    Triple("الشروق", prayerTimes.sunrise, "🌄"),
                    Triple("الظهر", prayerTimes.dhuhr, "☀️"),
                    Triple("العصر", prayerTimes.asr, "🌤️"),
                    Triple("المغرب", prayerTimes.maghrib, "🌅"),
                    Triple("العشاء", prayerTimes.isha, "🌙")
                )

                prayers.forEachIndexed { index, (name, time, emoji) ->
                    val isNext = nextPrayer?.first == name
                    val isPast = time < currentTime && name != "الشروق"

                    PrayerTimeRow(
                        name = name,
                        time = TimeFormatter.formatTime(time),
                        emoji = emoji,
                        isNext = isNext,
                        isPast = isPast,
                        isLast = index == prayers.size - 1
                    )
                }
            }
        }
    }
}

@Composable
fun LocationPromptCard(viewModel: MainViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2E40)),
        border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "🕌",
                fontSize = 48.sp
            )
            Text(
                text = "يحتاج التطبيق إلى موقعك",
                style = MaterialTheme.typography.titleMedium,
                color = GoldPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "لحساب مواقيت الصلاة الدقيقة لمنطقتك",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFAAAAAA),
                textAlign = TextAlign.Center
            )
            Button(
                onClick = { viewModel.fetchLocationAndSave() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    "تحديد موقعي الآن",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
