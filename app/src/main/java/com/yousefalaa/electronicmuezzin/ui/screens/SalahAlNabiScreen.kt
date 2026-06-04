package com.yousefalaa.electronicmuezzin.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.yousefalaa.electronicmuezzin.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalahAlNabiScreen(navController: NavController) {
    var showSettings     by remember { mutableStateOf(false) }
    var reminderFreq     by remember { mutableStateOf("متوسط") }
    var isEnabled        by remember { mutableStateOf(true) }
    var totalCount       by remember { mutableStateOf(0) }

    val salawat = listOf(
        "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ، كَمَا صَلَّيْتَ عَلَى إِبْرَاهِيمَ وَعَلَى آلِ إِبْرَاهِيمَ، إِنَّكَ حَمِيدٌ مَجِيدٌ، اللَّهُمَّ بَارِكْ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ، كَمَا بَارَكْتَ عَلَى إِبْرَاهِيمَ وَعَلَى آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ",
        "اللَّهُمَّ صَلِّ وَسَلِّمْ وَبَارِكْ عَلَى نَبِيِّنَا مُحَمَّدٍ",
        "صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ",
        "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ وَبَارِكْ وَسَلِّمْ"
    )
    var currentSalah by remember { mutableStateOf(0) }

    if (showSettings) {
        SalahSettingsScreen(
            frequency     = reminderFreq,
            onFreqChange  = { reminderFreq = it },
            isEnabled     = isEnabled,
            onEnabledChange = { isEnabled = it },
            onBack        = { showSettings = false }
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { showSettings = true }) {
                    Icon(Icons.Default.Settings, null, tint = Color.White, modifier = Modifier.size(28.dp))
                }
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White, modifier = Modifier.size(28.dp))
                }
            }

            // العنوان
            Text(
                "مُحَمَّد رَسُول الله",
                fontSize = 30.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text("ﷺ", fontSize = 44.sp, color = Color(0xFFFFD700))

            Spacer(modifier = Modifier.height(12.dp))

            // زر التشغيل/الإيقاف
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        color = if (isEnabled) Color(0xFF555555) else Color(0xFF888888),
                        shape = CircleShape
                    )
                    .clickable { isEnabled = !isEnabled },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (isEnabled) "✓" else "✗",
                    fontSize = 28.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // إعدادات التذكير
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(50))
                    .clickable { showSettings = true }
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "إعدادات الصلاة علي النبي ﷺ",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B7355),
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

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
                                if (reminderFreq == freq) Color.White
                                else Color.White.copy(alpha = 0.3f)
                            )
                            .clickable { reminderFreq = freq }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            freq,
                            color = if (reminderFreq == freq) Color(0xFF8B7355) else Color.White,
                            fontWeight = if (reminderFreq == freq) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // عداد كبير قابل للنقر
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .clickable {
                        totalCount++
                        if (totalCount % 10 == 0)
                            currentSalah = (currentSalah + 1) % salawat.size
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "$totalCount",
                    fontSize = 32.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // نص الصلاة + الآية
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        salawat[currentSalah],
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp,
                        lineHeight = 26.sp
                    )
                    HorizontalDivider(color = Color.White.copy(alpha = 0.3f))
                    Text(
                        "﴿ إِنَّ اللَّهَ وَمَلَائِكَتَهُ يُصَلُّونَ عَلَى النَّبِيِّ ﴾",
                        color = Color(0xFFFFD700),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "الأحزاب (56)",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // زر صفّر العداد
            if (totalCount > 0) {
                TextButton(onClick = { totalCount = 0 }) {
                    Text("صفّر العداد", color = Color.White.copy(alpha = 0.7f))
                }
            }
        }
    }
}

// ════════════════════════════════════════════
//  شاشة إعدادات الصلاة على النبي
// ════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalahSettingsScreen(
    frequency: String,
    onFreqChange: (String) -> Unit,
    isEnabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    val sounds = listOf(
        "الظهور علي الشاشة بدون صوت",
        "صلِّ علي محمد",
        "اللهم صلِّ وسلم علي نبينا محمد",
        "اللهم صلِّ وسلم علي نبينا محمد - علية افضل الصلاة والتسليم",
        "اللهم صلِّ علي محمد و ال محمد",
        "الصلاة والسلام عليك يا رسول الله",
        "إنَّ اللهَ وملئكتهُ يُصَلُّون علَى النَّبِيّ",
        "نذكركم بالصلاة علي الحبيب"
    )
    var selectedSound by remember { mutableStateOf(1) }
    var volume by remember { mutableStateOf(0.5f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "إعدادات الصلاة علي النبي ﷺ",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color(0xFF1A1A1A))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF2F2F7))
                .padding(pad)
        ) {
            // تفعيل
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            checked = isEnabled,
                            onCheckedChange = onEnabledChange,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFFD4573A)
                            )
                        )
                        Text(
                            "تفعيل التذكير",
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1A1A1A),
                            fontSize = 16.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // مستوى الصوت
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(
                            "مستوي الصوت",
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1A1A1A),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFD4573A),
                                activeTrackColor = Color(0xFFD4573A)
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // قائمة الأصوات
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        sounds.forEachIndexed { i, sound ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedSound = i }
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedSound == i,
                                        onClick = { selectedSound = i },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = Color(0xFFD4573A)
                                        )
                                    )
                                    if (i > 0) {
                                        Text(
                                            "▶",
                                            color = Color(0xFFD4573A),
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                                Text(
                                    sound,
                                    color = Color(0xFF1A1A1A),
                                    fontWeight = if (selectedSound == i) FontWeight.Bold else FontWeight.Normal,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                                )
                            }
                            if (i < sounds.size - 1)
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = Color(0xFFEEEEEE)
                                )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

