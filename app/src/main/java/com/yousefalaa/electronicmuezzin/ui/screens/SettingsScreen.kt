package com.yousefalaa.electronicmuezzin.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
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
    val azanSettings by viewModel.azanSettings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("الإعدادات ⚙️", style = MaterialTheme.typography.titleLarge, color = Color.White)
                },
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
            // الموقع
            SettingsSection(title = "📍 الموقع الجغرافي") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SettingsInfoRow("المدينة", prayerSettings.cityName.ifEmpty { "غير محدد" })
                    SettingsInfoRow("خط العرض", "%.4f°".format(prayerSettings.latitude))
                    SettingsInfoRow("خط الطول", "%.4f°".format(prayerSettings.longitude))
                    Button(
                        onClick = { viewModel.fetchLocationAndSave() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenIslamic)
                    ) {
                        Text("تحديث الموقع تلقائياً")
                    }
                }
            }

            // طريقة الحساب
            SettingsSection(title = "🧮 طريقة حساب المواقيت") {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    PrayerTimesCalculator.CalculationMethod.values().forEach { method ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateCalcMethod(method.name)
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = prayerSettings.calculationMethod == method.name,
                                onClick = { viewModel.updateCalcMethod(method.name) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = GoldPrimary,
                                    unselectedColor = Color(0xFF888888)
                                )
                            )
                            Text(
                                text = method.nameAr,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (prayerSettings.calculationMethod == method.name)
                                    GoldPrimary else Color.White
                            )
                        }
                    }
                }
            }

            // مذهب العصر
            SettingsSection(title = "🕌 وقت صلاة العصر") {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    PrayerTimesCalculator.AsrMethod.values().forEach { method ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.updateAsrMethod(method.name) }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = prayerSettings.asrMethod == method.name,
                                onClick = { viewModel.updateAsrMethod(method.name) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = GoldPrimary,
                                    unselectedColor = Color(0xFF888888)
                                )
                            )
                            Text(
                                text = "${method.nameAr} - ${if (method.name == "STANDARD") "ظل مثل" else "ظل مثلين"}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (prayerSettings.asrMethod == method.name)
                                    GoldPrimary else Color.White
                            )
                        }
                    }
                }
            }

            // إعدادات الأذان لكل صلاة
            SettingsSection(title = "🔊 إعدادات الأذان") {
                val prayers = listOf(
                    Triple("الفجر", azanSettings.fajrEnabled, azanSettings.fajrSound),
                    Triple("الظهر", azanSettings.dhuhrEnabled, azanSettings.dhuhrSound),
                    Triple("العصر", azanSettings.asrEnabled, azanSettings.asrSound),
                    Triple("المغرب", azanSettings.maghribEnabled, azanSettings.maghribSound),
                    Triple("العشاء", azanSettings.ishaEnabled, azanSettings.ishaSound)
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    prayers.forEach { (name, enabled, sound) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Switch(
                                checked = enabled,
                                onCheckedChange = { viewModel.togglePrayerAzan(name, it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = GoldPrimary,
                                    checkedTrackColor = GoldDark
                                )
                            )
                            Text(
                                text = "أذان $name",
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (enabled) Color.White else Color(0xFF666666)
                            )
                        }
                    }
                }
            }

            // معلومات التطبيق
            SettingsSection(title = "ℹ️ عن التطبيق") {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("المؤذن الإلكتروني", color = GoldPrimary, fontWeight = FontWeight.Bold)
                    Text("الإصدار 1.0", color = Color(0xFF888888), style = MaterialTheme.typography.bodySmall)
                    Text("com.yousefalaa.electronicmuezzin", color = Color(0xFF666666), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2E40)),
        border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = GoldPrimary,
                fontWeight = FontWeight.Bold
            )
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
        Text(text = value, color = Color.White, style = MaterialTheme.typography.bodyMedium)
        Text(text = label, color = Color(0xFF888888), style = MaterialTheme.typography.bodyMedium)
    }
}
