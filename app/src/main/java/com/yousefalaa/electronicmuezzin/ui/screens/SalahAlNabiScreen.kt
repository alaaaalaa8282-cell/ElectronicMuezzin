package com.yousefalaa.electronicmuezzin.ui.screens

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.yousefalaa.electronicmuezzin.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalahAlNabiScreen(navController: NavController) {
    var showSettings by remember { mutableStateOf(false) }
    var reminderFrequency by remember { mutableStateOf("متوسط") }
    var isEnabled by remember { mutableStateOf(true) }
    var totalCount by remember { mutableStateOf(0) }

    val salawat = listOf(
        "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ، كَمَا صَلَّيْتَ عَلَى إِبْرَاهِيمَ وَعَلَى آلِ إِبْرَاهِيمَ، إِنَّكَ حَمِيدٌ مَجِيدٌ",
        "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ وَبَارِكْ وَسَلِّمْ",
        "صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ",
        "اللَّهُمَّ صَلِّ وَسَلِّمْ وَبَارِكْ عَلَى نَبِيِّنَا مُحَمَّدٍ"
    )
    var currentSalah by remember { mutableStateOf(0) }
    var count by remember { mutableStateOf(0) }

    if (showSettings) {
        SalahSettingsSheet(
            frequency = reminderFrequency,
            onFrequencyChange = { reminderFrequency = it },
            isEnabled = isEnabled,
            onEnabledChange = { isEnabled = it },
            onDismiss = { showSettings = false }
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF8B7355), Color(0xFFBFA882), Color(0xFF8B7355))
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // رأس الصفحة
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                }
                IconButton(
                    onClick = { showSettings = true },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White)
                }
            }

            // العنوان الخطي
            Text(
                text = "مُحَمَّد رَسُول الله",
                fontSize = 36.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Text(
                text = "ﷺ",
                fontSize = 48.sp,
                color = Color(0xFFFFD700),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // زر التشغيل/الإيقاف
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = if (isEnabled) Color(0xFF555555) else Color(0xFF888888),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
                    .clickable { isEnabled = !isEnabled },
                contentAlignment = Alignment.Center
            ) {
                Text("⏻", fontSize = 36.sp, color = if (isEnabled) Color.White else Color(0xFFAAAAAA))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // إعدادات التذكير
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .clickable { showSettings = true },
                shape = RoundedCornerShape(50),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                Text(
                    text = "إعدادات الصلاة علي النبي ﷺ",
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B7355),
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // تكرار التذكير
            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("عالي", "متوسط", "منخفض", "نادراً").forEach { freq ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (reminderFrequency == freq)
                                    Color.White
                                else Color.White.copy(alpha = 0.3f)
                            )
                            .clickable { reminderFrequency = freq }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            freq,
                            color = if (reminderFrequency == freq) Color(0xFF8B7355) else Color.White,
                            fontWeight = if (reminderFrequency == freq) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // وقت محدد
            OutlinedButton(
                onClick = { },
                modifier = Modifier.fillMaxWidth(0.7f),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
            ) { Text("وقت محدد", color = Color.White) }

            Spacer(modifier = Modifier.height(16.dp))

            // عداد الصلاة
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
                    .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (totalCount == 0) "⏻" else "$totalCount",
                    fontSize = if (totalCount == 0) 36.sp else 28.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // الآية الكريمة
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clickable {
                        count++
                        totalCount++
                        if (count >= 10) {
                            count = 0
                            currentSalah = (currentSalah + 1) % salawat.size
                        }
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = salawat[currentSalah],
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        lineHeight = 28.sp
                    )
                    if (count > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$count مرة",
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "﴿ إِنَّ اللَّهَ وَمَلَائِكَتَهُ يُصَلُّونَ عَلَى النَّبِيِّ ﴾",
                        color = Color(0xFFFFD700),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "الأحزاب (56)",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SalahSettingsSheet(
    frequency: String,
    onFrequencyChange: (String) -> Unit,
    isEnabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val soundOptions = listOf(
        "الظهور علي الشاشة بدون صوت",
        "إختيار متعدد",
        "صلِّ علي محمد",
        "اللهم صلِّ وسلم علي نبينا محمد",
        "اللهم صلِّ وسلم علي نبينا محمد - علية افضل الصلاة والتسليم",
        "اللهم صلِّ علي محمد و ال محمد",
        "الصلاة والسلام عليك يا رسول الله",
        "إنَّ اللهَ وملئكتهُ يُصَلُّون علَى النَّبِيّ",
        "يَأَيُّهَا الَّذِيُن ءَامَنُواْ صَلُّواْ عَلَيْهِ وَسَلِّمُواْ تَسْلِيماً",
        "نذكركم بالصلاة علي الحبيب"
    )
    var selectedSound by remember { mutableStateOf(2) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // رأس الصفحة
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
            Text(
                "إعدادات الصلاة علي النبي ﷺ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // أصوات الصلاة
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("‹", fontSize = 20.sp, color = Color(0xFFD4573A))
                        Text("أصوات الصلاة علي النبي ﷺ", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // مستوى الصوت
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("مستوي الصوت", textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                        var volume by remember { mutableStateOf(0.3f) }
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            colors = SliderDefaults.colors(thumbColor = Color(0xFFD4573A), activeTrackColor = Color(0xFFD4573A))
                        )
                    }
                }
            }

            // تفعيل الإشعار قبل التنبية
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column {
                        listOf(
                            "تفعيل إشعار قبل التنبية بدون صوت الإشعارات",
                            "تفعيل إشعار قبل التنبية مع صوت الإشعارات",
                            "إلغاء الإشعار قبل التنبية"
                        ).forEachIndexed { i, option ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = i == 0,
                                    onClick = {},
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFD4573A))
                                )
                                Text(option, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                            }
                            if (i < 2) Divider()
                        }
                    }
                }
            }

            // قائمة الأصوات
            items(soundOptions.drop(2).mapIndexed { i, s -> i + 2 to s }) { (index, sound) ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("▶", color = Color(0xFFD4573A), fontSize = 18.sp)
                            Text("⬇", color = Color(0xFFD4573A), fontSize = 18.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(sound, textAlign = TextAlign.End, fontWeight = FontWeight.Medium)
                            if (index > 3) Text(
                                "علية افضل الصلاة والتسليم",
                                color = Color(0xFFD4573A),
                                fontSize = 12.sp
                            )
                        }
                        RadioButton(
                            selected = selectedSound == index,
                            onClick = { selectedSound = index },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFD4573A))
                        )
                    }
                }
            }
        }
    }
}
