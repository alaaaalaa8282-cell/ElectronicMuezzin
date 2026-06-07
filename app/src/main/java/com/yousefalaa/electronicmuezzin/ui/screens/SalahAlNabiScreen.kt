package com.yousefalaa.electronicmuezzin.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.yousefalaa.electronicmuezzin.data.models.SalahSoundCatalog
import com.yousefalaa.electronicmuezzin.ui.components.SoundPickerScreen
import com.yousefalaa.electronicmuezzin.ui.theme.*
import com.yousefalaa.electronicmuezzin.utils.SoundManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalahAlNabiScreen(navController: NavController) {
    val context      = LocalContext.current
    var showSettings by remember { mutableStateOf(false) }
    var reminderFreq by remember { mutableStateOf("متوسط") }
    var isEnabled    by remember { mutableStateOf(true) }
    var totalCount   by remember { mutableStateOf(0) }
    var selectedSound by remember { mutableStateOf("salah_salli") }

    val salawat = listOf(
        "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ، كَمَا صَلَّيْتَ عَلَى إِبْرَاهِيمَ وَعَلَى آلِ إِبْرَاهِيمَ، إِنَّكَ حَمِيدٌ مَجِيدٌ",
        "اللَّهُمَّ صَلِّ وَسَلِّمْ وَبَارِكْ عَلَى نَبِيِّنَا مُحَمَّدٍ",
        "صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ",
        "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ وَبَارِكْ وَسَلِّمْ"
    )
    var currentSalah by remember { mutableStateOf(0) }

    if (showSettings) {
        SoundPickerScreen(
            title      = "أصوات الصلاة علي النبي ﷺ",
            sounds     = SalahSoundCatalog.all,
            currentKey = selectedSound,
            onSelect   = { selectedSound = it; showSettings = false },
            onBack     = { showSettings = false }
        )
        return
    }

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(colors = listOf(Color(0xFF8B7355), Color(0xFFBFA882), Color(0xFF8B7355)))
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { showSettings = true }) {
                    Icon(Icons.Default.Settings, null, tint = Color.White, modifier = Modifier.size(28.dp))
                }
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White, modifier = Modifier.size(28.dp))
                }
            }

            Text("مُحَمَّد رَسُول الله", fontSize = 30.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Text("ﷺ", fontSize = 44.sp, color = Color(0xFFFFD700))
            Spacer(modifier = Modifier.height(12.dp))

            // زر تشغيل/إيقاف التذكير
            Box(
                modifier = Modifier.size(72.dp)
                    .background(if (isEnabled) Color(0xFF555555) else Color(0xFF888888), CircleShape)
                    .clickable { isEnabled = !isEnabled },
                contentAlignment = Alignment.Center
            ) {
                Text(if (isEnabled) "✓" else "✗", fontSize = 28.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // إعدادات الصوت
            Box(
                modifier = Modifier.fillMaxWidth(0.85f)
                    .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(50))
                    .clickable { showSettings = true }
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                val soundLabel = SalahSoundCatalog.all.find { it.key == selectedSound }?.label ?: "اختر الصوت"
                Text(soundLabel, fontWeight = FontWeight.Bold, color = Color(0xFF8B7355), fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // تكرار التذكير
            Row(modifier = Modifier.fillMaxWidth(0.9f), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf("عالي", "متوسط", "منخفض", "نادراً").forEach { freq ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (reminderFreq == freq) Color.White else Color.White.copy(alpha = 0.3f))
                            .clickable { reminderFreq = freq }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(freq,
                            color = if (reminderFreq == freq) Color(0xFF8B7355) else Color.White,
                            fontWeight = if (reminderFreq == freq) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // عداد - اضغط للتسبيح وتشغيل الصوت
            Box(
                modifier = Modifier.size(90.dp)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .clickable {
                        totalCount++
                        if (totalCount % 10 == 0) {
                            currentSalah = (currentSalah + 1) % salawat.size
                            // شغّل الصوت كل 10 مرات
                            if (selectedSound.isNotEmpty()) SoundManager.play(context, selectedSound)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("$totalCount", fontSize = 32.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // نص الصلاة
            Box(
                modifier = Modifier.fillMaxWidth(0.9f)
                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(salawat[currentSalah], color = Color.White, textAlign = TextAlign.Center, fontSize = 15.sp, lineHeight = 26.sp)
                    HorizontalDivider(color = Color.White.copy(alpha = 0.3f))
                    Text("﴿ إِنَّ اللَّهَ وَمَلَائِكَتَهُ يُصَلُّونَ عَلَى النَّبِيِّ ﴾", color = Color(0xFFFFD700), fontSize = 14.sp, textAlign = TextAlign.Center)
                    Text("الأحزاب (56)", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
            }

            if (totalCount > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { totalCount = 0; SoundManager.stop() }) {
                    Text("صفّر العداد", color = Color.White.copy(alpha = 0.7f))
                }
            }
        }
    }
}

