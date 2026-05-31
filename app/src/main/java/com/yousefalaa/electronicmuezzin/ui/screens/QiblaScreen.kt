package com.yousefalaa.electronicmuezzin.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yousefalaa.electronicmuezzin.ui.MainViewModel
import com.yousefalaa.electronicmuezzin.ui.theme.*
import kotlin.math.*

@Composable
fun QiblaScreen(viewModel: MainViewModel = hiltViewModel()) {
    val prayerSettings by viewModel.prayerSettings.collectAsState()
    val isLocationSet by viewModel.isLocationSet.collectAsState()

    val qiblaDirection = remember(prayerSettings) {
        if (prayerSettings.latitude != 0.0)
            viewModel.getQiblaDirection()
        else 0.0
    }

    val distanceMakkah = remember(prayerSettings) {
        if (prayerSettings.latitude != 0.0)
            viewModel.getDistanceToMakkah()
        else 0.0
    }

    // تدوير البوصلة بسلاسة
    val animatedAngle by animateFloatAsState(
        targetValue = qiblaDirection.toFloat(),
        animationSpec = tween(durationMillis = 600, easing = EaseInOutSine),
        label = "qibla_rotation"
    )

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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // العنوان
            Text(
                text = "🕋 اتجاه القبلة",
                style = MaterialTheme.typography.headlineMedium,
                color = GoldPrimary,
                fontWeight = FontWeight.Bold
            )

            if (!isLocationSet) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2E40))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "يرجى تحديد موقعك أولاً من شاشة الصلاة",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFFAAAAAA),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // بوصلة القبلة
                Box(
                    modifier = Modifier.size(280.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // الحلقات الخارجية
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val outerR = size.minDimension / 2f
                        val innerR = outerR - 20f

                        // الحلقة الخارجية الذهبية
                        drawCircle(
                            color = GoldPrimary,
                            radius = outerR,
                            center = center,
                            style = Stroke(width = 3f)
                        )

                        // الحلقة الداخلية
                        drawCircle(
                            color = GoldDark,
                            radius = innerR,
                            center = center,
                            style = Stroke(width = 1f)
                        )

                        // خلفية البوصلة
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF1A3A50), Color(0xFF0D1B2A)),
                                center = center
                            ),
                            radius = innerR,
                            center = center
                        )

                        // علامات الاتجاهات
                        for (i in 0..359 step 30) {
                            val angle = Math.toRadians(i.toDouble() - 90)
                            val tickLen = if (i % 90 == 0) 20f else 10f
                            val startX = center.x + (innerR - tickLen) * cos(angle).toFloat()
                            val startY = center.y + (innerR - tickLen) * sin(angle).toFloat()
                            val endX = center.x + innerR * cos(angle).toFloat()
                            val endY = center.y + innerR * sin(angle).toFloat()
                            drawLine(
                                color = if (i % 90 == 0) GoldPrimary else GoldPrimary.copy(alpha = 0.4f),
                                start = Offset(startX, startY),
                                end = Offset(endX, endY),
                                strokeWidth = if (i % 90 == 0) 3f else 1.5f
                            )
                        }
                    }

                    // مؤشر القبلة المتحرك
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(animatedAngle)
                    ) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val arrowLen = size.minDimension / 2f - 30f

                        // السهم نحو القبلة (أخضر)
                        val path = Path().apply {
                            moveTo(center.x, center.y - arrowLen)
                            lineTo(center.x - 18f, center.y - arrowLen + 50f)
                            lineTo(center.x, center.y - arrowLen + 30f)
                            lineTo(center.x + 18f, center.y - arrowLen + 50f)
                            close()
                        }
                        drawPath(path, color = GreenLight)

                        // الجهة المعاكسة
                        val pathBack = Path().apply {
                            moveTo(center.x, center.y + arrowLen)
                            lineTo(center.x - 12f, center.y + arrowLen - 35f)
                            lineTo(center.x, center.y + arrowLen - 20f)
                            lineTo(center.x + 12f, center.y + arrowLen - 35f)
                            close()
                        }
                        drawPath(pathBack, color = Color(0xFF888888))

                        // خط البوصلة
                        drawLine(
                            color = GreenLight,
                            start = Offset(center.x, center.y - arrowLen + 30f),
                            end = center,
                            strokeWidth = 4f
                        )
                        drawLine(
                            color = Color(0xFF888888),
                            start = center,
                            end = Offset(center.x, center.y + arrowLen - 20f),
                            strokeWidth = 4f
                        )

                        // نقطة المركز
                        drawCircle(color = GoldPrimary, radius = 10f, center = center)
                        drawCircle(color = Color.Black, radius = 5f, center = center)
                    }

                    // أيقونة الكعبة في المركز
                    Text(text = "🕋", fontSize = 24.sp)
                }

                // النصوص الإرشادية (الشمال / الجنوب / إلخ)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CompassLabel("ش", 0f)
                    CompassLabel("ق", 90f)
                    CompassLabel("ج", 180f)
                    CompassLabel("غ", 270f)
                }

                // بطاقة المعلومات
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2E40)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldDark.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            InfoItem(
                                label = "اتجاه القبلة",
                                value = "${qiblaDirection.toInt()}°"
                            )
                            InfoItem(
                                label = "المسافة للمكة",
                                value = "${(distanceMakkah / 1000).toInt()} كم"
                            )
                        }

                        Divider(color = GoldDark.copy(alpha = 0.3f))

                        Text(
                            text = "وَمِنْ حَيْثُ خَرَجْتَ فَوَلِّ وَجْهَكَ شَطْرَ الْمَسْجِدِ الْحَرَامِ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GoldLight,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "سورة البقرة: 150",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF888888)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CompassLabel(label: String, angle: Float) {
    Text(
        text = label,
        color = if (label == "ش") GoldPrimary else Color(0xFF888888),
        fontWeight = if (label == "ش") FontWeight.Bold else FontWeight.Normal,
        fontSize = 14.sp
    )
}

@Composable
fun InfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = GoldPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF888888)
        )
    }
}
