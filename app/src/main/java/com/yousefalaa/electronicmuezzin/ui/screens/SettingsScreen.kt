package com.yousefalaa.electronicmuezzin.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yousefalaa.electronicmuezzin.data.models.PrayerNames
import com.yousefalaa.electronicmuezzin.data.models.RamadanSettings
import com.yousefalaa.electronicmuezzin.ui.viewmodels.SettingsViewModel
import com.yousefalaa.electronicmuezzin.utils.PrayerTimesCalculator

val AppRed     = Color(0xFFD4573A)
val AppGray    = Color(0xFFF2F2F7)
val AppDivider = Color(0xFFE0E0E0)

// ════════════════════════════════════════════════════════
//  الشاشة الرئيسية للإعدادات
// ════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    vm: SettingsViewModel = hiltViewModel()
) {
    var screen by remember { mutableStateOf("main") }

    when (screen) {
        "main"         -> SettingsMainScreen(navController, vm) { screen = it }
        "azan_times"   -> AzanTimesScreen(vm)   { screen = "main" }
        "iqama_times"  -> IqamaTimesScreen(vm)  { screen = "main" }
        "approach"     -> ApproachScreen(vm)    { screen = "main" }
        "azan_sound"   -> AzanSoundScreen(vm)   { screen = "main" }
        "azan_screen"  -> AzanScreenSettings(vm){ screen = "main" }
        "more"         -> MoreSettings(vm)      { screen = "main" }
        "ramadan_settings" -> RamadanSettingsScreen(vm) { screen = "main" }
    }
}

// ════════════════════════════════════════════════════════
//  الرئيسية
// ════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsMainScreen(
    navController: NavController,
    vm: SettingsViewModel,
    onNav: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("اعدادات", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowForwardIos, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { pad ->
        LazyColumn(modifier = Modifier.fillMaxSize().background(AppGray).padding(pad)) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                SGroup {
                    SRow("ضبط مواقيت الأذان")    { onNav("azan_times") }
                    SDivider()
                    SRow("ضبط مواقيت الإقامة")   { onNav("iqama_times") }
                    SDivider()
                    SRow("ضبط مواقيت إقتراب الصلاة") { onNav("approach") }
                }
            }
            item {
                Spacer(modifier = Modifier.height(12.dp))
                RedButton("مركز التنبيهات 🔔") { }
            }
            item {
                Spacer(modifier = Modifier.height(12.dp))
                SGroup {
                    SRow("إختيار صوت الأذان")  { onNav("azan_sound") }
                    SDivider()
                    SRow("ضبط شاشة الأذان")    { onNav("azan_screen") }
                }
            }
            item {
                Spacer(modifier = Modifier.height(12.dp))
                RedButton("المـــزيد ⊞") { onNav("more") }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ════════════════════════════════════════════════════════
//  ضبط مواقيت الأذان - مع حفظ فعلي
// ════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzanTimesScreen(vm: SettingsViewModel, onBack: () -> Unit) {
    val azanSettings by vm.azanSettings.collectAsState()
    val prayerSettings by vm.prayerSettings.collectAsState()

    // حالة محلية للتعديلات مربوطة بالـ DataStore
    val offsets = remember(azanSettings) {
        mutableStateListOf(
            azanSettings.fajrOffset,
            azanSettings.sunriseOffset,
            azanSettings.dhuhrOffset,
            azanSettings.asrOffset,
            azanSettings.maghribOffset,
            azanSettings.ishaOffset
        )
    }

    val prayers = listOf(
        "أذان الفجر" to "🌙",
        "الشروق"     to "🌅",
        "أذان الظهر" to "☀️",
        "أذان العصر" to "🌤️",
        "أذان المغرب" to "🌇",
        "أذان العشاء" to "🌙"
    )
    val prayerKeys = listOf("الفجر","الشروق","الظهر","العصر","المغرب","العشاء")

    // حساب الأوقات الفعلية
    val calcMethod = runCatching {
        PrayerTimesCalculator.CalculationMethod.valueOf(prayerSettings.calculationMethod)
    }.getOrDefault(PrayerTimesCalculator.CalculationMethod.EGYPT)

    val asrMethod = runCatching {
        PrayerTimesCalculator.AsrMethod.valueOf(prayerSettings.asrMethod)
    }.getOrDefault(PrayerTimesCalculator.AsrMethod.STANDARD)

    val prayerTimes = if (prayerSettings.latitude != 0.0) {
        val tz = java.util.TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 3600000.0
        PrayerTimesCalculator.calculate(prayerSettings.latitude, prayerSettings.longitude, tz, calcMethod, asrMethod)
    } else null

    val baseTimes = prayerTimes?.let {
        listOf(it.fajr, it.sunrise, it.dhuhr, it.asr, it.maghrib, it.isha)
    } ?: List(6) { 0L }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ضبط مواقيت الأذان", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowForwardIos, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { pad ->
        LazyColumn(modifier = Modifier.fillMaxSize().background(AppGray).padding(pad)) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SGroup {
                    // المذهب
                    var showMathhabDialog by remember { mutableStateOf(false) }
                    SRowWithSub(
                        title = "المذهب المتبع لوقت العصر",
                        sub   = if (prayerSettings.asrMethod == "STANDARD") "شافعي,حنبلي,مالكي" else "حنفي"
                    ) { showMathhabDialog = true }

                    if (showMathhabDialog) {
                        Dialog(onDismissRequest = { showMathhabDialog = false }) {
                            Card(shape = RoundedCornerShape(16.dp)) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("مذهب وقت العصر", fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    listOf("STANDARD" to "شافعي,حنبلي,مالكي (ظل مثل)", "HANAFI" to "حنفي (ظل مثلين)").forEach { (key, label) ->
                                        Row(modifier = Modifier.fillMaxWidth().clickable {
                                            vm.setAsrMethod(key)
                                            showMathhabDialog = false
                                        }.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                            RadioButton(selected = prayerSettings.asrMethod == key, onClick = { vm.setAsrMethod(key); showMathhabDialog = false },
                                                colors = RadioButtonDefaults.colors(selectedColor = AppRed))
                                            Text(label)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    SDivider()

                    // التوقيت الصيفي
                    var summerDialog by remember { mutableStateOf(false) }
                    SRowWithSub(
                        title = "التوقيت الصيفي",
                        sub   = if (prayerSettings.summerTimeOffset == 0) "تم إيقاف التوقيت الصيفي" else "+${prayerSettings.summerTimeOffset} ساعة"
                    ) { summerDialog = true }

                    if (summerDialog) {
                        Dialog(onDismissRequest = { summerDialog = false }) {
                            Card(shape = RoundedCornerShape(16.dp)) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("التوقيت الصيفي", fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    listOf(0 to "إيقاف", 1 to "+1 ساعة", 2 to "+2 ساعة").forEach { (offset, label) ->
                                        Row(modifier = Modifier.fillMaxWidth().clickable {
                                            vm.setSummerOffset(offset)
                                            summerDialog = false
                                        }.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                            RadioButton(selected = prayerSettings.summerTimeOffset == offset, onClick = { vm.setSummerOffset(offset); summerDialog = false },
                                                colors = RadioButtonDefaults.colors(selectedColor = AppRed))
                                            Text(label)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Text("ضبط مواقيت الأذان", modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End, fontWeight = FontWeight.Bold, color = Color(0xFF444444))
                        Spacer(modifier = Modifier.height(4.dp))

                        prayers.forEachIndexed { i, (name, emoji) ->
                            val adjustedTime = baseTimes[i] + offsets[i] * 60000L
                            val timeStr = if (adjustedTime > 0)
                                com.yousefalaa.electronicmuezzin.utils.TimeFormatter.formatTime(adjustedTime)
                            else "--:--"

                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically) {

                                // عداد التعديل
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AdjustButton("-") {
                                        if (offsets[i] > -60) {
                                            offsets[i]--
                                            vm.setAzanOffset(prayerKeys[i], offsets[i])
                                        }
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("${offsets[i]}", fontSize = 24.sp, fontWeight = FontWeight.Bold,
                                            color = if (offsets[i] == 0) Color.Black else AppRed)
                                        Text(timeStr, fontSize = 11.sp, color = Color(0xFF888888))
                                    }
                                    AdjustButton("+") {
                                        if (offsets[i] < 60) {
                                            offsets[i]++
                                            vm.setAzanOffset(prayerKeys[i], offsets[i])
                                        }
                                    }
                                }

                                // اسم الصلاة
                                Row(verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(name, fontWeight = FontWeight.Medium)
                                    Text(emoji, fontSize = 20.sp)
                                }
                            }
                            if (i < prayers.size - 1) SDivider()
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ════════════════════════════════════════════════════════
//  ضبط مواقيت الإقامة
// ════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IqamaTimesScreen(vm: SettingsViewModel, onBack: () -> Unit) {
    val az by vm.azanSettings.collectAsState()
    val prayerSettings by vm.prayerSettings.collectAsState()

    val iqamas = remember(az) {
        mutableStateListOf(az.fajrIqama, az.dhuhrIqama, az.asrIqama, az.maghribIqama, az.ishaIqama)
    }

    val prayers = listOf("أذان الفجر" to "🌙","أذان الظهر" to "☀️","أذان العصر" to "🌤️","أذان المغرب" to "🌇","أذان العشاء" to "🌙")
    val prayerKeys = listOf("الفجر","الظهر","العصر","المغرب","العشاء")

    // حساب أوقات الإقامة الفعلية
    val calcMethod = runCatching { PrayerTimesCalculator.CalculationMethod.valueOf(prayerSettings.calculationMethod) }
        .getOrDefault(PrayerTimesCalculator.CalculationMethod.EGYPT)
    val asrMethod = runCatching { PrayerTimesCalculator.AsrMethod.valueOf(prayerSettings.asrMethod) }
        .getOrDefault(PrayerTimesCalculator.AsrMethod.STANDARD)
    val prayerTimes = if (prayerSettings.latitude != 0.0) {
        val tz = java.util.TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 3600000.0
        PrayerTimesCalculator.calculate(prayerSettings.latitude, prayerSettings.longitude, tz, calcMethod, asrMethod)
    } else null

    val baseTimes = prayerTimes?.let {
        listOf(it.fajr, it.dhuhr, it.asr, it.maghrib, it.isha)
    } ?: List(5) { 0L }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ضبط مواقيت الإقامة", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowForwardIos, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { pad ->
        LazyColumn(modifier = Modifier.fillMaxSize().background(AppGray).padding(pad)) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Text("ضبط مواقيت الإقامة", modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End, fontWeight = FontWeight.Bold, color = Color(0xFF444444))
                        Spacer(modifier = Modifier.height(4.dp))

                        prayers.forEachIndexed { i, (name, emoji) ->
                            val iqamaTime = baseTimes[i] + iqamas[i] * 60000L
                            val timeStr = if (iqamaTime > 0)
                                com.yousefalaa.electronicmuezzin.utils.TimeFormatter.formatTime(iqamaTime)
                            else "--:--"

                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically) {

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AdjustButton("-") {
                                        if (iqamas[i] > 0) {
                                            iqamas[i]--
                                            vm.setIqamaMinutes(prayerKeys[i], iqamas[i])
                                        }
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("${iqamas[i]}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                        Text(timeStr, fontSize = 11.sp, color = Color(0xFF888888))
                                    }
                                    AdjustButton("+") {
                                        if (iqamas[i] < 120) {
                                            iqamas[i]++
                                            vm.setIqamaMinutes(prayerKeys[i], iqamas[i])
                                        }
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(name, fontWeight = FontWeight.Medium)
                                    Text(emoji, fontSize = 20.sp)
                                }
                            }
                            if (i < prayers.size - 1) SDivider()
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ════════════════════════════════════════════════════════
//  ضبط مواقيت اقتراب الصلاة
// ════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApproachScreen(vm: SettingsViewModel, onBack: () -> Unit) {
    val az by vm.azanSettings.collectAsState()
    val prayerSettings by vm.prayerSettings.collectAsState()

    val enabled = remember(az) {
        mutableStateListOf(az.fajrApproachEnabled, az.dhuhrApproachEnabled, az.asrApproachEnabled, az.maghribApproachEnabled, az.ishaApproachEnabled)
    }
    val minutes = remember(az) {
        mutableStateListOf(az.fajrApproachMinutes, az.sunriseApproachMinutes, az.dhuhrApproachMinutes, az.asrApproachMinutes, az.maghribApproachMinutes, az.ishaApproachMinutes)
    }

    val prayers = listOf("قبل أذان الفجر","قبل شروق الشمس","قبل أذان الظهر","قبل أذان العصر","قبل أذان المغرب","قبل أذان العشاء")
    val emojis  = listOf("🌙","🌅","☀️","🌤️","🌇","🌙")
    val enabledKeys = listOf("الفجر","الظهر","العصر","المغرب","العشاء")
    val minuteKeys  = listOf("الفجر","الشروق","الظهر","العصر","المغرب","العشاء")

    // أوقات الأذان الفعلية
    val calcMethod = runCatching { PrayerTimesCalculator.CalculationMethod.valueOf(prayerSettings.calculationMethod) }
        .getOrDefault(PrayerTimesCalculator.CalculationMethod.EGYPT)
    val asrMethod = runCatching { PrayerTimesCalculator.AsrMethod.valueOf(prayerSettings.asrMethod) }
        .getOrDefault(PrayerTimesCalculator.AsrMethod.STANDARD)
    val pt = if (prayerSettings.latitude != 0.0) {
        val tz = java.util.TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 3600000.0
        PrayerTimesCalculator.calculate(prayerSettings.latitude, prayerSettings.longitude, tz, calcMethod, asrMethod)
    } else null

    val baseTimes = pt?.let { listOf(it.fajr, it.sunrise, it.dhuhr, it.asr, it.maghrib, it.isha) } ?: List(6) { 0L }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ضبط مواقيت إقتراب الصلاة", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowForwardIos, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { pad ->
        LazyColumn(modifier = Modifier.fillMaxSize().background(AppGray).padding(pad)) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SGroup {
                    // تنبيه اقتراب الصلاة لكل صلاة
                    var showApproachSoundScreen by remember { mutableStateOf(false) }
                    SRow("اختيار التنبية إقتراب الصلاة") { showApproachSoundScreen = true }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Text("ضبط مواقيت إقتراب الصلاة", modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End, fontWeight = FontWeight.Bold, color = Color(0xFF444444))
                        Spacer(modifier = Modifier.height(4.dp))

                        prayers.forEachIndexed { i, name ->
                            val approachTime = baseTimes[i] - minutes[i] * 60000L
                            val timeStr = if (approachTime > 0)
                                com.yousefalaa.electronicmuezzin.utils.TimeFormatter.formatTime(approachTime)
                            else "--:--"

                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically) {

                                // عداد الدقائق
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AdjustButton("-") {
                                        if (minutes[i] > 0) {
                                            minutes[i]--
                                            vm.setApproachMinutes(minuteKeys[i], minutes[i])
                                        }
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("${minutes[i]}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                        Text(timeStr, fontSize = 11.sp, color = Color(0xFF888888))
                                    }
                                    AdjustButton("+") {
                                        if (minutes[i] < 120) {
                                            minutes[i]++
                                            vm.setApproachMinutes(minuteKeys[i], minutes[i])
                                        }
                                    }
                                }

                                // الاسم + مفتاح (فقط للصلوات الخمس وليس الشروق)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (i != 1) { // الشروق ليس له مفتاح تفعيل منفصل
                                        val ei = if (i > 1) i - 1 else i
                                        Switch(
                                            checked = enabled.getOrElse(ei) { true },
                                            onCheckedChange = { v ->
                                                if (ei < enabled.size) {
                                                    enabled[ei] = v
                                                    vm.setApproachEnabled(enabledKeys.getOrElse(ei) { "الفجر" }, v)
                                                }
                                            },
                                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AppRed)
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(name, fontWeight = FontWeight.Medium, textAlign = TextAlign.End, color = Color(0xFF1A1A1A))
                                        Text(emojis[i], fontSize = 16.sp)
                                    }
                                }
                            }
                            if (i < prayers.size - 1) SDivider()
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ════════════════════════════════════════════════════════
//  اختيار صوت الأذان
// ════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzanSoundScreen(vm: SettingsViewModel, onBack: () -> Unit) {
    val az by vm.azanSettings.collectAsState()
    var pickingFor by remember { mutableStateOf<String?>(null) }

    val prayers = listOf("الفجر","الظهر","العصر","المغرب","العشاء")
    val currentSounds = listOf(az.fajrSound, az.dhuhrSound, az.asrSound, az.maghribSound, az.ishaSound)
    val allSounds = PrayerNames.getAzanSounds()

    if (pickingFor != null) {
        val idx = prayers.indexOf(pickingFor)
        MuezzinPickerScreen(
            prayerName    = "أذان ${pickingFor!!}",
            currentSound  = currentSounds.getOrElse(idx) { "" },
            onSelect      = { sound ->
                vm.setAzanSound(pickingFor!!, sound)
                pickingFor = null
            },
            onBack        = { pickingFor = null }
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
    ) { pad ->
        LazyColumn(modifier = Modifier.fillMaxSize().background(AppGray).padding(pad)) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SGroup {
                    prayers.forEachIndexed { i, prayer ->
                        SRowWithSub(
                            title = "أذان $prayer",
                            sub   = allSounds[currentSounds[i]] ?: currentSounds[i]
                        ) { pickingFor = prayer }
                        if (i < prayers.size - 1) SDivider()
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════
//  اختيار المؤذن لصلاة معينة
// ════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuezzinPickerScreen(
    prayerName  : String,
    currentSound: String,
    onSelect    : (String) -> Unit,
    onBack      : () -> Unit
) {
    val groups = PrayerNames.getGroupedSounds()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(prayerName, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowForwardIos, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { pad ->
        LazyColumn(modifier = Modifier.fillMaxSize().background(AppGray).padding(pad)) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            groups.forEach { (groupTitle, sounds) ->
                item {
                    Text(groupTitle, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                        textAlign = TextAlign.End, color = Color(0xFF888888), fontSize = 13.sp)
                }
                item {
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)) {
                        Column {
                            sounds.forEachIndexed { i, (key, label) ->
                                Row(modifier = Modifier.fillMaxWidth().clickable { onSelect(key) }.padding(horizontal = 16.dp, vertical = 14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(selected = currentSound == key, onClick = { onSelect(key) },
                                            colors = RadioButtonDefaults.colors(selectedColor = AppRed))
                                        Text("▶", color = AppRed, fontSize = 14.sp)
                                    }
                                    Text(label, fontWeight = if (currentSound == key) FontWeight.Bold else FontWeight.Normal,
                                        color = if (currentSound == key) AppRed else Color.Black)
                                }
                                if (i < sounds.size - 1) SDivider()
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

// ════════════════════════════════════════════════════════
//  ضبط شاشة الأذان
// ════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzanScreenSettings(vm: SettingsViewModel, onBack: () -> Unit) {
    val az by vm.azanSettings.collectAsState()
    var showScreen  by remember(az) { mutableStateOf(az.showAzanScreen) }
    var autoStop    by remember(az) { mutableStateOf(az.autoStopAzan) }
    var stopMinutes by remember(az) { mutableStateOf(az.autoStopMinutes) }

    fun save() = vm.setAzanScreenSettings(showScreen, autoStop, stopMinutes)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ضبط شاشة الأذان", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                navigationIcon = { IconButton(onClick = { save(); onBack() }) { Icon(Icons.Default.ArrowForwardIos, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { pad ->
        LazyColumn(modifier = Modifier.fillMaxSize().background(AppGray).padding(pad)) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SGroup {
                    // عرض شاشة الأذان
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = showScreen, onCheckedChange = { showScreen = it; save() },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AppRed))
                        Text("عرض شاشة الأذان كاملة", fontWeight = FontWeight.Medium, color = Color(0xFF1A1A1A))
                    }
                    SDivider()
                    // إيقاف تلقائي
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = autoStop, onCheckedChange = { autoStop = it; save() },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AppRed))
                        Text("إيقاف الأذان تلقائياً", fontWeight = FontWeight.Medium, color = Color(0xFF1A1A1A))
                    }
                    if (autoStop) {
                        SDivider()
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                AdjustButton("-") { if (stopMinutes > 1) { stopMinutes--; save() } }
                                Text("$stopMinutes دقيقة", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                AdjustButton("+") { if (stopMinutes < 30) { stopMinutes++; save() } }
                            }
                            Text("مدة الأذان", fontWeight = FontWeight.Medium, color = Color(0xFF1A1A1A))
                        }
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════
//  المزيد من الإعدادات
// ════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreSettings(vm: SettingsViewModel, onBack: () -> Unit) {
    val ps by vm.prayerSettings.collectAsState()
    var showCalcDialog by remember { mutableStateOf(false) }
    var showRamadan    by remember { mutableStateOf(false) }

    if (showRamadan) {
        RamadanSettingsScreen(vm) { showRamadan = false }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("المزيد", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowForwardIos, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { pad ->
        LazyColumn(modifier = Modifier.fillMaxSize().background(AppGray).padding(pad)) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // المجموعة الأولى
                SGroup {
                    SRowWithSub("أذكار المسلم اليومية",  "") { }
                    SDivider()
                    SRow("قيام الليل")           { }
                    SDivider()
                    SRow("تواشيح الفجر")         { }
                    SDivider()
                    SRow("صيام التطوع")          { }
                    SDivider()
                    SRow("إعدادات يوم الجمعة")   { }
                }
                Spacer(modifier = Modifier.height(12.dp))

                // المجموعة الثانية
                SGroup {
                    SRow("إعدادات رمضان") { showRamadan = true }
                    SDivider()
                    SRow("تكبيرات العيد والعشر من ذي الحجة") { }
                }
                Spacer(modifier = Modifier.height(12.dp))

                // المجموعة الثالثة
                SGroup {
                    SRowWithSub("ضبط التاريخ الهجري", "") { }
                    SDivider()
                    SRow("حجم الخط والالوان") { }
                    SDivider()
                    SRowWithSub("طريقة الحساب", PrayerTimesCalculator.CalculationMethod.valueOf(ps.calculationMethod).nameAr) {
                        showCalcDialog = true
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                // المجموعة الرابعة
                SGroup {
                    SRowWithSub("اللغة", "العربية") { }
                    SDivider()
                    SRow("تغيير الموقع") { }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showCalcDialog) {
        Dialog(onDismissRequest = { showCalcDialog = false }) {
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("طريقة الحساب", fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(8.dp))
                    PrayerTimesCalculator.CalculationMethod.values().forEach { method ->
                        Row(modifier = Modifier.fillMaxWidth().clickable {
                            vm.setCalculationMethod(method.name)
                            showCalcDialog = false
                        }.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            RadioButton(selected = ps.calculationMethod == method.name, onClick = { vm.setCalculationMethod(method.name); showCalcDialog = false },
                                colors = RadioButtonDefaults.colors(selectedColor = AppRed))
                            Text(method.nameAr)
                        }
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════
//  إعدادات رمضان الكاملة
// ════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RamadanSettingsScreen(vm: SettingsViewModel, onBack: () -> Unit) {
    val saved by vm.ramadanSettings.collectAsState()
    var s by remember(saved) { mutableStateOf(saved) }

    fun save() = vm.setRamadanSettings(s)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إعدادات رمضان", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                navigationIcon = { IconButton(onClick = { save(); onBack() }) { Icon(Icons.Default.ArrowForwardIos, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { pad ->
        LazyColumn(modifier = Modifier.fillMaxSize().background(AppGray).padding(pad)) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // مجموعة التنبيهات
                SGroup {
                    SRow("منبة السحور") { }
                    SDivider()
                    SRow("تلاوة قرآنية قبل المغرب") { }
                    SDivider()
                    // مدفع الإفطار
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = s.iftarCannonEnabled,
                            onCheckedChange = { s = s.copy(iftarCannonEnabled = it); save() },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AppRed))
                        Text("مدفع الافطار قبل المغرب", fontWeight = FontWeight.Medium)
                    }
                    SDivider()
                    // دعاء الإفطار
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = s.iftarDuaEnabled,
                            onCheckedChange = { s = s.copy(iftarDuaEnabled = it); save() },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AppRed))
                        Text("دعاء الافطار بعد المغرب", fontWeight = FontWeight.Medium)
                    }
                    SDivider()
                    // صامت أثناء التراويح
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Switch(checked = s.silentDuringTaraweh,
                                onCheckedChange = { s = s.copy(silentDuringTaraweh = it); save() },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AppRed))
                            if (s.silentDuringTaraweh) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AdjustButton("-") { if (s.tarawehSilentMinutes > 10) { s = s.copy(tarawehSilentMinutes = s.tarawehSilentMinutes - 10); save() } }
                                    Text("${s.tarawehSilentMinutes}\nدقيقة", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                    AdjustButton("+") { if (s.tarawehSilentMinutes < 180) { s = s.copy(tarawehSilentMinutes = s.tarawehSilentMinutes + 10); save() } }
                                }
                            }
                        }
                        Text("صامت اثناء صلاة التراويح", fontWeight = FontWeight.Medium)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                // عدد الختمات
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("عدد الختمات في رمضان", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                            AdjustButton("-") { if (s.khatmatCount > 1) { s = s.copy(khatmatCount = s.khatmatCount - 1); save() } }
                            Text("${s.khatmatCount.toString().padStart(2,'0')}", fontSize = 48.sp, fontWeight = FontWeight.Bold)
                            AdjustButton("+") { if (s.khatmatCount < 30) { s = s.copy(khatmatCount = s.khatmatCount + 1); save() } }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                // بدأ القراءة من الفاتحة
                SGroup {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = s.startFromFatiha,
                            onCheckedChange = { s = s.copy(startFromFatiha = it); save() },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AppRed))
                        Text("بدأ القراءة من الفاتحة مع اول رمضان", fontWeight = FontWeight.Medium)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ════════════════════════════════════════════════════════
//  مكوّنات مساعدة
// ════════════════════════════════════════════════════════
@Composable
fun SGroup(content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.fillMaxWidth(), content = content)
    }
}

@Composable
fun SRow(title: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text("‹", fontSize = 20.sp, color = Color(0xFFBBBBBB))
        Text(title, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A1A))
    }
}

@Composable
fun SRowWithSub(title: String, sub: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text("‹", fontSize = 20.sp, color = Color(0xFFBBBBBB))
        Column(horizontalAlignment = Alignment.End) {
            Text(title, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A1A))
            if (sub.isNotEmpty()) Text(sub, color = AppRed, fontSize = 13.sp)
        }
    }
}

@Composable
fun SDivider() = Divider(modifier = Modifier.padding(horizontal = 16.dp), color = AppDivider, thickness = 0.5.dp)

@Composable
fun RedButton(label: String, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
        .background(AppRed, RoundedCornerShape(12.dp))
        .clickable(onClick = onClick).padding(16.dp)) {
        Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp,
            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
    }
}

@Composable
fun AdjustButton(label: String, onClick: () -> Unit) {
    Box(modifier = Modifier.size(42.dp)
        .background(if (label == "-") Color(0xFF888888) else AppRed, RoundedCornerShape(8.dp))
        .clickable(onClick = onClick),
        contentAlignment = Alignment.Center) {
        Text(label, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

