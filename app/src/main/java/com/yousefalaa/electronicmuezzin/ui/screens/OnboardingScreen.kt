package com.yousefalaa.electronicmuezzin.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

data class OnboardingPage(
    val emoji: String,
    val title: String,
    val description: String,
    val permission: String? = null
)

@Composable
fun OnboardingScreen(navController: NavController, onFinish: () -> Unit) {
    val context = LocalContext.current
    var currentPage by remember { mutableStateOf(0) }

    val pages = listOf(
        OnboardingPage(
            emoji = "📍",
            title = "الوصول الي موقعك",
            description = "السماح للتطبيق بجمع بيانات الموقع لتحديد اوقات الصلاة بناء علي المدينة الحالية",
            permission = Manifest.permission.ACCESS_FINE_LOCATION
        ),
        OnboardingPage(
            emoji = "📞",
            title = "حالة الاتصال",
            description = "السماح للتطبيق بمعرفة اذا كان هناك مكالمة اثناء الاذان حتي يوقف الاذان لمنع تداخل الاصوات ويكون هناك تنبية بعد انتهاء المكالمة",
            permission = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
                Manifest.permission.READ_PHONE_STATE else null
        ),
        OnboardingPage(
            emoji = "📱",
            title = "الظهور فوق التطبيقات",
            description = "السماح لشاشة الأذان بالظهور فوق التطبيقات الاخري",
            permission = null // SYSTEM_ALERT_WINDOW - special permission
        )
    )

    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> if (currentPage < pages.size - 1) currentPage++ else onFinish() }

    val phoneLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> if (currentPage < pages.size - 1) currentPage++ else onFinish() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // تخطي
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onFinish) {
                        Text("تخطي", color = Color(0xFFD4573A), fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // الأيقونة
                Text(pages[currentPage].emoji, fontSize = 80.sp)

                Spacer(modifier = Modifier.height(32.dp))

                // العنوان
                Text(
                    text = pages[currentPage].title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF1A1A1A)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // الوصف
                Text(
                    text = pages[currentPage].description,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF666666),
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                // نقاط التقدم
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    pages.indices.forEach { i ->
                        Box(
                            modifier = Modifier
                                .size(if (i == currentPage) 12.dp else 8.dp)
                                .background(
                                    color = if (i == currentPage) Color(0xFFD4573A) else Color(0xFFCCCCCC),
                                    shape = CircleShape
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // زر التفعيل
                Button(
                    onClick = {
                        when (currentPage) {
                            0 -> locationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            1 -> {
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                    phoneLauncher.launch(Manifest.permission.READ_PHONE_STATE)
                                } else {
                                    if (currentPage < pages.size - 1) currentPage++ else onFinish()
                                }
                            }
                            2 -> {
                                // SYSTEM_ALERT_WINDOW
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                                    !Settings.canDrawOverlays(context)) {
                                    val intent = Intent(
                                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:${context.packageName}")
                                    )
                                    context.startActivity(intent)
                                }
                                onFinish()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4573A))
                ) {
                    Text("تفعيـــيل", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

