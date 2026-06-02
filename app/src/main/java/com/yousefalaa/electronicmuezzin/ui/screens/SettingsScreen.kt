package com.yousefalaa.electronicmuezzin.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
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
import com.yousefalaa.electronicmuezzin.utils.TimeFormatter

val AppRed = Color(0xFFD4573A)
val AppGray = Color(0xFFF5F5F5)
val AppDivider = Color(0xFFE0E0E0)

// ════════════════════════════════════════════════
//  شاشة الإعدادات الرئيسية
// ════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: MainViewModel = hiltViewModel()) {
    var currentScreen by remember { mutableStateOf("main") }

    when (currentScreen) {
        "main"          -> SettingsMain(navController) { currentScreen = it }
        "azan_times"    -> AzanTimesScreen { currentScreen = "main" }
        "iqama_times"   -> IqamaTimesScreen { currentScreen = "main" }
        "approach"      -> ApproachTimesScreen { currentScreen = "main" }
        "azan_sound"    -> AzanSoundSelectionScreen { currentScreen = "main" }
        "approach_sound"-> ApproachSoundScreen { currentScreen = "main" }
        "azan_screen"   -> AzanScreenSettingsScreen { currentScreen = "main" }
        "more_settings" -> MoreSettingsScreen(navController) { currentScreen = "main" }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsMain(navController: NavController, onNavigate: (String) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("اعدادات", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowForwardIos, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(AppGray).padding(padding)
        ) {
            // المجموعة الأولى
            item {
                SettingsGroup {
                    SettingsItem("ضبط مواقيت الأذان") { onNavigate("azan_times") }
                    Divider(color = AppDivider)
                    SettingsItem("ضبط مواقيت الإقامة") { onNavigate("iqama_times") }
                    Divider(color = AppDivider)
                    SettingsItem("ضبط مواقيت إقتراب الصلاة") { onNavigate("approach") }
                }
            }

            // مركز التنبيهات
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .background(AppRed, RoundedCornerShape(12.dp))
                        .clickable { }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("مركز التنبيهات", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("🔔", fontSize = 20.sp)
                    }
                }
            }

            // المجموعة الثانية
            item {
                Spacer(modifier = Modifier.height(12.dp))
                SettingsGroup {
                    SettingsItem("إختيار صوت الأذان") { onNavigate("azan_sound") }
                    Divider(color = AppDivider)
                    SettingsItem("ضبط صوت الأذان") { onNavigate("approach_sound") }
                    Divider(color = AppDivider)
                    SettingsItem("ضبط شاشة الأذان") { onNavigate("azan_screen") }
                }
            }

            // المزيد
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .background(AppRed, RoundedCornerShape(12.dp))
                        .clickable { onNavigate("more_settings") }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("المـــزيد", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("⊞", fontSize = 20.sp, color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ════════════════════════════════════════════════
//  ضبط مواقيت الأذان
// ════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzanTimesScreen(onBack: () -> Unit) {
    val prayers = listOf(
        "أذان الفجر" to "🌙",
        "الشروق" to "🌅",
        "أذان الظهر" to "☀️",
        "أذان العصر" to "🌤️",
        "أذان المغرب" to "🌇",
        "أذان العشاء" to "🌙"
    )
    val adjustments = remember { mutableStateListOf(0, 0, 0, 0, 0, 0) }
    val times = listOf("07:39", "08:54", "02:57", "06:23", "09:01", "10:30")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ضبط مواقيت الأذان", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowForwardIos, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().background(AppGray).padding(padding)) {
            // المذهب والتوقيت الصيفي
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SettingsGroup {
                    SettingsItemWithSub("المذهب المتبع لتحديد وقت صلاة العصر", "شافعي,حنبلي,مالكي") { }
                    Divider(color = AppDivider)
                    SettingsItemWithSub("التوقيت الصيفي", "تم إيقاف التوقيت الصيفي") { }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ضبط مواقيت الأذان
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Text(
                            "ضبط مواقيت الأذان",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF444444)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        prayers.forEachIndexed { i, (name, emoji) ->
                            PrayerTimeAdjustRow(
                                name = name,
                                emoji = emoji,
                                time = times[i],
                                value = adjustments[i],
                                onMinus = { if (adjustments[i] > -60) adjustments[i]-- },
                                onPlus = { if (adjustments[i] < 60) adjustments[i]++ }
                            )
                            if (i < prayers.size - 1) Divider(color = AppDivider)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ════════════════════════════════════════════════
//  ضبط مواقيت الإقامة
// ════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IqamaTimesScreen(onBack: () -> Unit) {
    val prayers = listOf(
        "أذان الفجر" to "🌙",
        "الشروق" to "🌅",
        "أذان الظهر" to "☀️",
        "أذان العصر" to "🌤️",
        "أذان المغرب" to "🌇",
        "أذان العشاء" to "🌙"
    )
    val adjustments = remember { mutableStateListOf(20, 15, 10, 10, 5, 15) }
    val times = listOf("07:59", "09:09", "03:07", "06:33", "09:06", "10:45") 

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ضبط مواقيت الإقامة", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowForwardIos, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().background(AppGray).padding(padding)) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Text("ضبط مواقيت الإقامة", modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End, fontWeight = FontWeight.Bold, color = Color(0xFF444444))
                        Spacer(modifier = Modifier.height(8.dp))
                        prayers.forEachIndexed { i, (name, emoji) ->
                            PrayerTimeAdjustRow(
                                name = name, emoji = emoji, time = times[i],
                                value = adjustments[i],
                                onMinus = { if (adjustments[i] > 0) adjustments[i]-- },
                                onPlus = { if (adjustments[i] < 120) adjustments[i]++ }
                            )
                            if (i < prayers.size - 1) Divider(color = AppDivider)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ════════════════════════════════════════════════
//  ضبط مواقيت اقتراب الصلاة
// ════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApproachTimesScreen(onBack: () -> Unit) {
    val prayers = listOf(
        Triple("قبل أذان الفجر", "🌙", 20),
        Triple("قبل شروق الشمس", "🌅", 10),
        Triple("قبل أذان الظهر", "☀️", 10),
        Triple("قبل أذان العصر", "🌤️", 10),
        Triple("قبل أذان المغرب", "🌇", 15),
        Triple("قبل أذان العشاء", "🌙", 15)
    )
    val enabled = remember { mutableStateListOf(true, true, true, true, true, true) }
    val values = remember { mutableStateListOf(20, 10, 10, 10, 15, 15) }
    val times = listOf("07:19", "08:44", "02:47", "06:13", "08:46", "10:15")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ضبط مواقيت إقتراب الصلاة", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowForwardIos, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().background(AppGray).padding(padding)) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SettingsGroup {
                    SettingsItem("اختيار التنبية إقتراب الصلاة") { }
                    Divider(color = AppDivider)
                    SettingsItem("مستوي صوت إقتراب الاوقات") { }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Text("ضبط مواقيت إقتراب الصلاة", modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End, fontWeight = FontWeight.Bold, color = Color(0xFF444444))
                        Spacer(modifier = Modifier.height(8.dp))
                        prayers.forEachIndexed { i, (name, emoji, _) ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // عداد - قيمة +
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier.size(40.dp).background(Color(0xFF888888), RoundedCornerShape(8.dp)).clickable { if (values[i] > 0) values[i]-- },
                                        contentAlignment = Alignment.Center
                                    ) { Text("-", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("${values[i]}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                        Text(times[i], fontSize = 11.sp, color = Color(0xFF888888))
                                    }
                                    Box(
                                        modifier = Modifier.size(40.dp).background(AppRed, RoundedCornerShape(8.dp)).clickable { if (values[i] < 120) values[i]++ },
                                        contentAlignment = Alignment.Center
                                    ) { Text("+", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                                }
                                // اسم + مفتاح
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Switch(
                                        checked = enabled[i], onCheckedChange = { enabled[i] = it },
                                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AppRed)
                                    )
                                    Text(name, textAlign = TextAlign.End)
                                }
                            }
                            if (i < prayers.size - 1) Divider(color = AppDivider)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ════════════════════════════════════════════════
//  اختيار صوت الأذان لكل صلاة
// ════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzanSoundSelectionScreen(onBack: () -> Unit) {
    val prayers = listOf("أذان الفجر", "أذان الظهر", "أذان العصر", "أذان المغرب", "أذان العشاء")
    val selectedSounds = remember { mutableStateListOf("إختيار متعدد", "إختيار متعدد", "إختيار متعدد", "إختيار متعدد", "إختيار متعدد") }
    var showSoundPicker by remember { mutableStateOf<Int?>(null) }

    if (showSoundPicker != null) {
        AzanMuezzinPicker(
            prayerName = prayers[showSoundPicker!!],
            currentSound = selectedSounds[showSoundPicker!!],
            onSelect = { sound ->
                selectedSounds[showSoundPicker!!] = sound
                showSoundPicker = null
            },
            onBack = { showSoundPicker = null }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("اختيار المؤذن", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowForwardIos, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().background(AppGray).padding(padding)) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                SettingsGroup {
                    prayers.forEachIndexed { i, prayer ->
                        SettingsItemWithSub(prayer, selectedSounds[i]) { showSoundPicker = i }
                        if (i < prayers.size - 1) Divider(color = AppDivider)
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════
//  اختيار المؤذن لصلاة معينة
// ════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzanMuezzinPicker(prayerName: String, currentSound: String, onSelect: (String) -> Unit, onBack: () -> Unit) {
    val muezzins = listOf(
        "الوضع الصامت" to "",
        "إختيار متعدد" to "",
        "علي بن أحمد الملا" to "مؤذن المسجد الحرام",
        "عبدالباسط عبدالصمد" to "",
        "محمد علي البنا" to "",
        "ياسر الدوسري" to "",
        "محمد رفعت" to "",
        "عبدالمجيد السريحي" to "",
        "ماهر المعيقلي" to "مؤذن المسجد الحرام",
        "عبدالرحمن السديس" to "مؤذن المسجد الحرام",
        "سعود الشريم" to "مؤذن المسجد الحرام",
        "علي الحذيفي" to "مؤذن المسجد النبوي",
        "عبدالمحسن القاسم" to "مؤذن المسجد النبوي",
        "إختيار من الهاتف" to ""
    )
    var selected by remember { mutableStateOf(currentSound) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("أذان $prayerName", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowForwardIos, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().background(AppGray).padding(padding)) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column {
                        muezzins.forEachIndexed { i, (name, sub) ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable {
                                    selected = name
                                    onSelect(name)
                                }.padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    if (i > 1) {
                                        Text("▶", color = AppRed, fontSize = 18.sp)
                                        if (i < muezzins.size - 1)
                                            Text("🎬", fontSize = 18.sp)
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(name, fontWeight = FontWeight.Medium)
                                    if (sub.isNotEmpty())
                                        Text(sub, color = AppRed, fontSize = 12.sp)
                                }
                                RadioButton(
                                    selected = selected == name,
                                    onClick = { selected = name; onSelect(name) },
                                    colors = RadioButtonDefaults.colors(selectedColor = AppRed)
                                )
                            }
                            if (i < muezzins.size - 1) Divider(color = AppDivider)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ════════════════════════════════════════════════
//  ضبط صوت الأذان (مستوى الصوت لكل صلاة)
// ════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApproachSoundScreen(onBack: () -> Unit) {
    val prayers = listOf(
        "قبل أذان الفجر" to "🌙",
        "قبل أذان الظهر" to "☀️",
        "قبل أذان العصر" to "🌤️",
        "قبل أذان المغرب" to "🌇",
        "قبل أذان العشاء" to "🌙"
    )
    val volumes = remember { mutableStateListOf(0.3f, 0.4f, 0.4f, 0.4f, 0.4f) }
    val usePhone = remember { mutableStateListOf(false, false, false, false, false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("مستوي صوت إقتراب الاوقات", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowForwardIos, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().background(AppGray).padding(padding)) {
            items(prayers.size) { i ->
                val (name, emoji) = prayers[i]
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("$name  $emoji", fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text("🔊", fontSize = 24.sp)
                            Text("〰", fontSize = 24.sp)
                            Text("📱", fontSize = 24.sp)
                        }
                        Slider(
                            value = volumes[i],
                            onValueChange = { volumes[i] = it },
                            colors = SliderDefaults.colors(thumbColor = AppRed, activeTrackColor = AppRed)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("مستوي صوت الهاتف", fontSize = 13.sp, color = Color(0xFF666666))
                            Spacer(modifier = Modifier.width(8.dp))
                            RadioButton(
                                selected = usePhone[i],
                                onClick = { usePhone[i] = !usePhone[i] },
                                colors = RadioButtonDefaults.colors(selectedColor = AppRed)
                            )
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

// ════════════════════════════════════════════════
//  ضبط شاشة الأذان
// ════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzanScreenSettingsScreen(onBack: () -> Unit) {
    var showOnTop by remember { mutableStateOf(true) }
    var autoStop by remember { mutableStateOf(false) }
    var stopMinutes by remember { mutableStateOf(5) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ضبط شاشة الأذان", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowForwardIos, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().background(AppGray).padding(padding)) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SettingsGroup {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(checked = showOnTop, onCheckedChange = { showOnTop = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AppRed))
                        Text("عرض شاشة الأذان فوق التطبيقات", fontWeight = FontWeight.Medium)
                    }
                    Divider(color = AppDivider)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(checked = autoStop, onCheckedChange = { autoStop = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AppRed))
                        Text("إيقاف الأذان تلقائياً", fontWeight = FontWeight.Medium)
                    }
                    if (autoStop) {
                        Divider(color = AppDivider)
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(Modifier.size(36.dp).background(Color(0xFF888888), RoundedCornerShape(8.dp)).clickable { if (stopMinutes > 1) stopMinutes-- }, contentAlignment = Alignment.Center) {
                                    Text("-", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Text("$stopMinutes دقيقة", fontWeight = FontWeight.Bold)
                                Box(Modifier.size(36.dp).background(AppRed, RoundedCornerShape(8.dp)).clickable { if (stopMinutes < 30) stopMinutes++ }, contentAlignment = Alignment.Center) {
                                    Text("+", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text("مدة الأذان", fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════
//  المزيد من الإعدادات
// ════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreSettingsScreen(navController: NavController, onBack: () -> Unit) {
    var darkMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("المزيد", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowForwardIos, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().background(AppGray).padding(padding)) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SettingsGroup {
                    SettingsItem("ضبط التاريخ الهجري") { }
                    Divider(color = AppDivider)
                    SettingsItem("حجم الخط والالوان") { }
                    Divider(color = AppDivider)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(checked = darkMode, onCheckedChange = { darkMode = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AppRed))
                        Text("الوضع المظلم", fontWeight = FontWeight.Medium)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                SettingsGroup {
                    SettingsItemWithSub("اللغة", "العربية") { }
                    Divider(color = AppDivider)
                    SettingsItem("تغيير الموقع") { }
                }
                Spacer(modifier = Modifier.height(12.dp))
                SettingsGroup {
                    SettingsItem("إلغاء الاعلانات") { }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ════════════════════════════════════════════════
//  مكوّنات مساعدة
// ════════════════════════════════════════════════
@Composable
fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth(), content = content)
    }
}

@Composable
fun SettingsItem(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("‹", fontSize = 20.sp, color = Color(0xFF888888))
        Text(title, fontWeight = FontWeight.Medium, textAlign = TextAlign.End)
    }
}

@Composable
fun SettingsItemWithSub(title: String, sub: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("‹", fontSize = 20.sp, color = Color(0xFF888888))
        Column(horizontalAlignment = Alignment.End) {
            Text(title, fontWeight = FontWeight.Medium)
            Text(sub, color = AppRed, fontSize = 13.sp)
        }
    }
}

@Composable
fun PrayerTimeAdjustRow(name: String, emoji: String, time: String, value: Int, onMinus: () -> Unit, onPlus: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.size(42.dp).background(Color(0xFF888888), RoundedCornerShape(8.dp)).clickable(onClick = onMinus), contentAlignment = Alignment.Center) {
                Text("-", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$value", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text(time, fontSize = 11.sp, color = Color(0xFF888888))
            }
            Box(Modifier.size(42.dp).background(AppRed, RoundedCornerShape(8.dp)).clickable(onClick = onPlus), contentAlignment = Alignment.Center) {
                Text("+", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(name, textAlign = TextAlign.End)
            Text(emoji, fontSize = 20.sp)
        }
    }
}
