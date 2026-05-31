package com.yousefalaa.electronicmuezzin.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yousefalaa.electronicmuezzin.ui.theme.*
import com.yousefalaa.electronicmuezzin.utils.HijriCalendar
import com.yousefalaa.electronicmuezzin.utils.TimeFormatter
import java.util.*

@Composable
fun CalendarScreen() {
    val today = Calendar.getInstance()
    val hijriToday = HijriCalendar.today()

    var selectedGregorianMonth by remember { mutableStateOf(today.get(Calendar.MONTH)) }
    var selectedGregorianYear by remember { mutableStateOf(today.get(Calendar.YEAR)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A1628), Color(0xFF0D1B2A))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // رأس الصفحة
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF0D3349), Color(0xFF091A26))
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "📅 التقويم",
                        style = MaterialTheme.typography.headlineMedium,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = hijriToday.toArabicString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = GoldLight,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // بطاقة اليوم
            TodayCard(hijriToday, today)

            Spacer(modifier = Modifier.height(16.dp))

            // التقويم الشهري
            MonthCalendarCard(
                year = selectedGregorianYear,
                month = selectedGregorianMonth,
                today = today,
                onPrevMonth = {
                    if (selectedGregorianMonth == 0) {
                        selectedGregorianMonth = 11
                        selectedGregorianYear--
                    } else selectedGregorianMonth--
                },
                onNextMonth = {
                    if (selectedGregorianMonth == 11) {
                        selectedGregorianMonth = 0
                        selectedGregorianYear++
                    } else selectedGregorianMonth++
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // المناسبات الإسلامية
            IslamicOccasionsCard(hijriToday)

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TodayCard(hijri: HijriCalendar.HijriDate, gregorian: Calendar) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2E40)),
        border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // الهجري
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${hijri.day}",
                    fontSize = 48.sp,
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = hijri.toArabicString().split(" ").drop(1).joinToString(" "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = GoldLight,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "هـ",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF888888)
                )
            }

            Divider(
                modifier = Modifier
                    .height(80.dp)
                    .width(1.dp),
                color = GoldDark.copy(alpha = 0.4f)
            )

            // الميلادي
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${gregorian.get(Calendar.DAY_OF_MONTH)}",
                    fontSize = 48.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = TimeFormatter.getMonthName(gregorian.get(Calendar.MONTH) + 1),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFCCCCCC),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "${gregorian.get(Calendar.YEAR)} م",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF888888)
                )
            }
        }
    }
}

@Composable
fun MonthCalendarCard(
    year: Int,
    month: Int,
    today: Calendar,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val cal = Calendar.getInstance().apply { set(year, month, 1) }
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) - 1 + 7) % 7 // 0=Sun

    val isCurrentMonth = year == today.get(Calendar.YEAR) && month == today.get(Calendar.MONTH)
    val todayDay = if (isCurrentMonth) today.get(Calendar.DAY_OF_MONTH) else -1

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2E40)),
        border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // التنقل بين الأشهر
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNextMonth) {
                    Text("›", fontSize = 24.sp, color = GoldPrimary)
                }
                Text(
                    text = "${TimeFormatter.getMonthName(month + 1)} $year",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onPrevMonth) {
                    Text("‹", fontSize = 24.sp, color = GoldPrimary)
                }
            }

            // أيام الأسبوع
            val dayNames = listOf("أح", "إث", "ثل", "أر", "خم", "جم", "سب")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                dayNames.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (day == "جم") GoldPrimary else Color(0xFF888888),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // الأيام
            val cells = firstDayOfWeek + daysInMonth
            val rows = (cells + 6) / 7

            for (row in 0 until rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (col in 0..6) {
                        val dayNum = row * 7 + col - firstDayOfWeek + 1
                        val isValid = dayNum in 1..daysInMonth
                        val isToday = dayNum == todayDay

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .background(
                                    color = when {
                                        isToday -> GoldPrimary
                                        else -> Color.Transparent
                                    },
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isValid) {
                                // الهجري
                                val hijri = HijriCalendar.fromGregorian(year, month + 1, dayNum)
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$dayNum",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = when {
                                            isToday -> Color.Black
                                            col == 5 -> GoldPrimary // الجمعة
                                            else -> Color.White
                                        },
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Text(
                                        text = "${hijri.day}",
                                        fontSize = 8.sp,
                                        color = when {
                                            isToday -> Color.Black.copy(alpha = 0.7f)
                                            else -> Color(0xFF666666)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IslamicOccasionsCard(hijri: HijriCalendar.HijriDate) {
    val occasions = listOf(
        Triple(1, 1, "رأس السنة الهجرية"),
        Triple(10, 1, "يوم عاشوراء"),
        Triple(12, 3, "مولد النبي ﷺ"),
        Triple(27, 7, "ليلة الإسراء والمعراج"),
        Triple(15, 8, "ليلة النصف من شعبان"),
        Triple(1, 9, "أول رمضان"),
        Triple(27, 9, "ليلة القدر المرتقبة"),
        Triple(1, 10, "عيد الفطر المبارك"),
        Triple(9, 12, "يوم عرفة"),
        Triple(10, 12, "عيد الأضحى المبارك")
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2E40)),
        border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "🌙 المناسبات الإسلامية",
                style = MaterialTheme.typography.titleMedium,
                color = GoldPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            occasions.forEach { (day, month, name) ->
                val isCurrentMonth = hijri.month == month
                val isPast = isCurrentMonth && hijri.day > day
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$day / $month هـ",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isCurrentMonth && !isPast) GoldPrimary else Color(0xFF666666)
                    )
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isCurrentMonth && !isPast) Color.White else Color(0xFF888888),
                        textAlign = TextAlign.End
                    )
                }
                if (month != occasions.last().second)
                    Divider(color = Color(0xFF2A3F50), thickness = 0.5.dp)
            }
        }
    }
}
