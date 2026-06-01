package com.yousefalaa.electronicmuezzin.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yousefalaa.electronicmuezzin.data.models.PrayerAzanSettings
import com.yousefalaa.electronicmuezzin.data.models.PrayerNames
import com.yousefalaa.electronicmuezzin.ui.MainViewModel
import com.yousefalaa.electronicmuezzin.ui.theme.*
import com.yousefalaa.electronicmuezzin.utils.PrayerTimesCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val prayerSettings by viewModel.prayerSettings.collectAsState()
    val azanSettings   by viewModel.azanSettings.collectAsState()

    // الصلاة التي يختار المستخدم صوتها الآن (null = لا يوجد dialog مفتوح)
    var pickingSoundFor by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الإعدادات ⚙️", style = MaterialTheme.typography.titleLarge, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D1B2A))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = listOf(Color(0xFF0A1628), Color(0xFF0D1B2A))))
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── الموقع ──────────────────────────────────────────────
            SettingsSection(title = "📍 الموقع الجغرافي") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SettingsInfoRow("المدينة",    prayerSettings.cityName.ifEmpty { "غير محدد" })
                    SettingsInfoRow("خط العرض",  "%.4f°".format(prayerSettings.latitude))
                    SettingsInfoRow("خط الطول",  "%.4f°".format(prayerSettings.longitude))
                    Button(
                        onClick  = { viewModel.fetchLocationAndSave() },
                        modifier = Modifier.fillMaxWidth(),
                        colors   = ButtonDefaults.buttonColors(containerColor = GreenIslamic)
                    ) { Text("تحديث الموقع تلقائياً") }
                }
            }

            // ── طريقة الحساب ────────────────────────────────────────
            SettingsSection(title = "🧮 طريقة حساب المواقيت") {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    PrayerTimesCalculator.CalculationMethod.values().forEach { method ->
                        val selected = prayerSettings.calculationMethod == method.name
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.updateCalcMethod(method.name) }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selected,
                                onClick  = { viewModel.updateCalcMethod(method.name) },
                                colors   = RadioButtonDefaults.colors(
                                    selectedColor   = GoldPrimary,
                                    unselectedColor = Color(0xFF888888)
                                )
                            )
                            Text(
                                text  = method.nameAr,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (selected) GoldPrimary else Color.White
                            )
                        }
                    }
                }
            }

            // ── مذهب العصر ──────────────────────────────────────────
            SettingsSection(title = "🕌 وقت صلاة العصر") {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    PrayerTimesCalculator.AsrMethod.values().forEach { method ->
                        val selected = prayerSettings.asrMethod == method.name
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.updateAsrMethod(method.name) }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selected,
                                onClick  = { viewModel.updateAsrMethod(method.name) },
                                colors   = RadioButtonDefaults.colors(
                                    selectedColor   = GoldPrimary,
                                    unselectedColor = Color(0xFF888888)
                                )
                            )
                            Text(
                                text  = "${method.nameAr} - ${if (method.name == "STANDARD") "ظل مثل" else "ظل مثلين"}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (selected) GoldPrimary else Color.White
                            )
                        }
                    }
                }
            }

            // ── إعدادات الأذان لكل صلاة ─────────────────────────────
            SettingsSection(title = "🔊 إعدادات الأذان") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    val allSoundNames = PrayerNames.getAzanSounds()

                    data class PrayerRow(
                        val name: String,
                        val enabled: Boolean,
                        val sound: String
                    )

                    val rows = listOf(
                        PrayerRow("الفجر",   azanSettings.fajrEnabled,    azanSettings.fajrSound),
                        PrayerRow("الظهر",   azanSettings.dhuhrEnabled,   azanSettings.dhuhrSound),
                        PrayerRow("العصر",   azanSettings.asrEnabled,     azanSettings.asrSound),
                        PrayerRow("المغرب",  azanSettings.maghribEnabled, azanSettings.maghribSound),
                        PrayerRow("العشاء",  azanSettings.ishaEnabled,    azanSettings.ishaSound)
                    )

                    rows.forEach { row ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(12.dp),
                            colors   = CardDefaults.cardColors(containerColor = Color(0xFF0D2535)),
                            border   = BorderStroke(1.dp, GoldDark.copy(alpha = 0.3f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // السطر الأول: اسم الصلاة + مفتاح التفعيل
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Switch(
                                        checked = row.enabled,
                                        onCheckedChange = { viewModel.togglePrayerAzan(row.name, it) },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor  = GoldPrimary,
                                            checkedTrackColor  = GoldDark
                                        )
                                    )
                                    Text(
                                        text  = "أذان صلاة ${row.name}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (row.enabled) Color.White else Color(0xFF666666),
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // السطر الثاني: اختيار الصوت (يظهر فقط إذا الأذان مفعّل)
                                if (row.enabled) {
                                    val currentSoundName = allSoundNames[row.sound]
                                        ?: row.sound

                                    OutlinedButton(
                                        onClick = { pickingSoundFor = row.name },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape  = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = GoldPrimary
                                        ),
                                        border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.5f))
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.KeyboardArrowDown,
                                                contentDescription = null,
                                                tint = GoldPrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text  = currentSoundName,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = GoldLight,
                                                textAlign = TextAlign.End,
                                                modifier = Modifier.weight(1f).padding(end = 8.dp)
                                            )
                                            Icon(
                                                Icons.Default.PlayArrow,
                                                contentDescription = "الصوت",
                                                tint = GoldPrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── معلومات التطبيق ──────────────────────────────────────
            SettingsSection(title = "ℹ️ عن التطبيق") {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("المؤذن الإلكتروني",        color = GoldPrimary, fontWeight = FontWeight.Bold)
                    Text("الإصدار 1.0",              color = Color(0xFF888888), style = MaterialTheme.typography.bodySmall)
                    Text("com.yousefalaa.electronicmuezzin", color = Color(0xFF666666), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }

    // ── Dialog اختيار الصوت ──────────────────────────────────────────
    pickingSoundFor?.let { prayerName ->
        AzanSoundPickerDialog(
            prayerName    = prayerName,
            currentSound  = when (prayerName) {
                "الفجر"  -> azanSettings.fajrSound
                "الظهر"  -> azanSettings.dhuhrSound
                "العصر"  -> azanSettings.asrSound
                "المغرب" -> azanSettings.maghribSound
                "العشاء" -> azanSettings.ishaSound
                else     -> "azan_makkah_sudais"
            },
            onSelect = { soundKey ->
                viewModel.setPrayerAzanSound(prayerName, soundKey)
                pickingSoundFor = null
            },
            onDismiss = { pickingSoundFor = null }
        )
    }
}

// ════════════════════════════════════════════════════════════════════
//  Dialog اختيار صوت الأذان - مجمّع في مجموعات
// ════════════════════════════════════════════════════════════════════
@Composable
fun AzanSoundPickerDialog(
    prayerName   : String,
    currentSound : String,
    onSelect     : (String) -> Unit,
    onDismiss    : () -> Unit
) {
    val groups = PrayerNames.getGroupedSounds()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape  = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2E40))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ── رأس الـ dialog ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF0D3349), Color(0xFF1A4A60), Color(0xFF0D3349))
                            )
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text  = "🔊 اختر صوت أذان $prayerName",
                        style = MaterialTheme.typography.titleMedium,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Divider(color = GoldDark.copy(alpha = 0.4f))

                // ── قائمة الأصوات المجمّعة ──
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 8.dp)
                ) {
                    groups.forEach { (groupTitle, sounds) ->

                        // عنوان المجموعة
                        Text(
                            text     = groupTitle,
                            style    = MaterialTheme.typography.labelLarge,
                            color    = GoldLight,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0D2535))
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            textAlign = TextAlign.End
                        )

                        // عناصر المجموعة
                        sounds.forEach { (key, label) ->
                            val isSelected = key == currentSound
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isSelected)
                                            Brush.horizontalGradient(
                                                colors = listOf(Color(0xFF0D3349), Color(0xFF1A4A60), Color(0xFF0D3349))
                                            )
                                        else
                                            Brush.horizontalGradient(
                                                colors = listOf(Color.Transparent, Color.Transparent)
                                            )
                                    )
                                    .clickable { onSelect(key) }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                // علامة الاختيار
                                if (isSelected) {
                                    Text("✓", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                } else {
                                    Spacer(modifier = Modifier.width(20.dp))
                                }

                                Text(
                                    text  = label,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isSelected) GoldPrimary else Color.White,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    textAlign = TextAlign.End,
                                    modifier  = Modifier.weight(1f)
                                )
                            }
                            Divider(color = Color(0xFF1E3A4A), thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════
//  مكوّنات مساعدة
// ════════════════════════════════════════════════════════════════════
@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = Color(0xFF1A2E40)),
        border   = BorderStroke(1.dp, GoldDark.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = GoldPrimary, fontWeight = FontWeight.Bold)
            Divider(color = GoldDark.copy(alpha = 0.3f))
            content()
        }
    }
}

@Composable
fun SettingsInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = value, color = Color.White,       style = MaterialTheme.typography.bodyMedium)
        Text(text = label, color = Color(0xFF888888), style = MaterialTheme.typography.bodyMedium)
    }
}
