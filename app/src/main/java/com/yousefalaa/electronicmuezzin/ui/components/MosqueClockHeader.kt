package com.yousefalaa.electronicmuezzin.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yousefalaa.electronicmuezzin.ui.theme.*
import com.yousefalaa.electronicmuezzin.utils.HijriCalendar
import com.yousefalaa.electronicmuezzin.utils.TimeFormatter
import java.util.Calendar

@Composable
fun MosqueClockHeader(
    currentTime: Long,
    hijriDate: HijriCalendar.HijriDate,
    cityName: String,
    nextPrayer: Pair<String, Long>?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // اسم التطبيق
        Text(
            text = "المؤذن الإلكتروني",
            style = MaterialTheme.typography.headlineMedium,
            color = GoldPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        // المدينة
        if (cityName.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "📍", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = cityName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFAAAAAA)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // الساعة الأنالوجية مع شكل المسجد
        Box(
            modifier = Modifier
                .size(220.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF0D3349),
                            Color(0xFF091A26)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(2.dp, GoldPrimary.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            AnalogMosqueClock(currentTimeMs = currentTime)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // الوقت الرقمي
        val cal = Calendar.getInstance()
        cal.timeInMillis = currentTime
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val min = cal.get(Calendar.MINUTE)
        val sec = cal.get(Calendar.SECOND)
        val amPm = if (hour < 12) "صباحاً" else "مساءً"
        val h = if (hour > 12) hour - 12 else if (hour == 0) 12 else hour

        Text(
            text = "${h.toString().padStart(2, '0')}:${min.toString().padStart(2, '0')}:${sec.toString().padStart(2, '0')} $amPm",
            style = MaterialTheme.typography.displayMedium,
            color = GoldPrimary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // التاريخ الهجري
        Text(
            text = hijriDate.toArabicString(),
            style = MaterialTheme.typography.titleMedium,
            color = GoldLight,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // الصلاة القادمة
        nextPrayer?.let { (name, time) ->
            val diff = time - currentTime
            if (diff > 0) {
                NextPrayerBanner(
                    prayerName = name,
                    countdown = TimeFormatter.formatCountdown(diff)
                )
            }
        }
    }
}

@Composable
fun AnalogMosqueClock(currentTimeMs: Long) {
    val cal = Calendar.getInstance()
    cal.timeInMillis = currentTimeMs

    val hour = cal.get(Calendar.HOUR).toFloat()
    val minute = cal.get(Calendar.MINUTE).toFloat()
    val second = cal.get(Calendar.SECOND).toFloat()

    val hourAngle = (hour + minute / 60f) * 30f
    val minuteAngle = (minute + second / 60f) * 6f
    val secondAngle = second * 6f

    Canvas(modifier = Modifier.size(180.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2f - 8f

        // خلفية الساعة
        drawCircle(
            color = Color(0xFF0A1F30),
            radius = radius,
            center = center
        )

        // حلقة ذهبية خارجية
        drawCircle(
            color = GoldPrimary,
            radius = radius,
            center = center,
            style = Stroke(width = 3f)
        )

        // نقاط الساعات
        for (i in 0..11) {
            val angle = Math.toRadians((i * 30.0 - 90.0))
            val dotRadius = if (i % 3 == 0) 5f else 3f
            val dotColor = if (i % 3 == 0) GoldPrimary else GoldPrimary.copy(alpha = 0.5f)
            val x = center.x + (radius - 12f) * Math.cos(angle).toFloat()
            val y = center.y + (radius - 12f) * Math.sin(angle).toFloat()
            drawCircle(color = dotColor, radius = dotRadius, center = Offset(x, y))
        }

        // عقرب الساعات
        rotate(degrees = hourAngle, pivot = center) {
            drawLine(
                color = Color.White,
                start = center,
                end = Offset(center.x, center.y - radius * 0.55f),
                strokeWidth = 6f,
                cap = StrokeCap.Round
            )
        }

        // عقرب الدقائق
        rotate(degrees = minuteAngle, pivot = center) {
            drawLine(
                color = Color.White,
                start = center,
                end = Offset(center.x, center.y - radius * 0.75f),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
        }

        // عقرب الثواني
        rotate(degrees = secondAngle, pivot = center) {
            drawLine(
                color = GoldPrimary,
                start = Offset(center.x, center.y + radius * 0.15f),
                end = Offset(center.x, center.y - radius * 0.85f),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )
        }

        // نقطة المركز
        drawCircle(
            color = GoldPrimary,
            radius = 6f,
            center = center
        )
        drawCircle(
            color = Color.Black,
            radius = 3f,
            center = center
        )

        // قبة المسجد (زخرفة)
        drawMosqueDome(center, radius)
    }
}

private fun DrawScope.drawMosqueDome(center: Offset, radius: Float) {
    // خط أفقي رفيع داخلي
    drawLine(
        color = GoldPrimary.copy(alpha = 0.3f),
        start = Offset(center.x - radius * 0.7f, center.y),
        end = Offset(center.x + radius * 0.7f, center.y),
        strokeWidth = 1f
    )
    drawLine(
        color = GoldPrimary.copy(alpha = 0.3f),
        start = Offset(center.x, center.y - radius * 0.7f),
        end = Offset(center.x, center.y + radius * 0.7f),
        strokeWidth = 1f
    )
}

@Composable
fun NextPrayerBanner(prayerName: String, countdown: String) {
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
                shape = RoundedCornerShape(50)
            )
            .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(50))
            .padding(horizontal = 24.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "⏳ $countdown",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "صلاة $prayerName القادمة",
            style = MaterialTheme.typography.bodyLarge,
            color = GoldPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PrayerTimeRow(
    name: String,
    time: String,
    emoji: String,
    isNext: Boolean,
    isPast: Boolean,
    isLast: Boolean
) {
    val bgColor = when {
        isNext -> Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF0D3349),
                Color(0xFF1A4A60),
                Color(0xFF0D3349)
            )
        )
        else -> Brush.horizontalGradient(
            colors = listOf(Color.Transparent, Color.Transparent)
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // الوقت
        Text(
            text = time,
            style = MaterialTheme.typography.titleMedium,
            color = when {
                isNext -> GoldPrimary
                isPast -> Color(0xFF666666)
                else -> Color.White
            },
            fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal
        )

        // الاسم والرمز
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isNext) {
                Text(
                    text = "▶",
                    color = GoldPrimary,
                    fontSize = 10.sp
                )
            }
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                color = when {
                    isNext -> GoldPrimary
                    isPast -> Color(0xFF666666)
                    else -> Color.White
                },
                fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                text = emoji,
                fontSize = 20.sp
            )
        }
    }

    if (!isLast) {
        Divider(
            color = Color(0xFF2A3F50),
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
