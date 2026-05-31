package com.yousefalaa.electronicmuezzin.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.yousefalaa.electronicmuezzin.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbihScreen(navController: NavController) {
    val tasbihat = listOf(
        "سُبْحَانَ اللَّهِ" to 33,
        "الْحَمْدُ لِلَّهِ" to 33,
        "اللَّهُ أَكْبَرُ" to 34,
        "لَا إِلَهَ إِلَّا اللَّهُ" to 100,
        "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ" to 100,
        "سُبْحَانَ اللَّهِ الْعَظِيمِ" to 100,
        "أَسْتَغْفِرُ اللَّهَ" to 100,
        "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ" to 100
    )

    var selectedIndex by remember { mutableStateOf(0) }
    var count by remember { mutableStateOf(0) }
    var totalCount by remember { mutableStateOf(0) }
    val haptic = LocalHapticFeedback.current

    val target = tasbihat[selectedIndex].second
    val progress = (count.toFloat() / target).coerceIn(0f, 1f)

    // تأثير النقر
    val scale = remember { Animatable(1f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("عداد التسبيح", style = MaterialTheme.typography.titleLarge, color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        count = 0
                        totalCount = 0
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "إعادة", tint = GoldPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D1B2A))
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0A1628), Color(0xFF0D1B2A))
                    )
                )
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // اختيار الذكر
                ScrollableTabRow(
                    selectedTabIndex = selectedIndex,
                    containerColor = Color(0xFF1A2E40),
                    contentColor = GoldPrimary,
                    edgePadding = 8.dp
                ) {
                    tasbihat.forEachIndexed { index, (text, _) ->
                        Tab(
                            selected = index == selectedIndex,
                            onClick = {
                                selectedIndex = index
                                count = 0
                            },
                            text = {
                                Text(
                                    text = text.split(" ").take(2).joinToString(" "),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (index == selectedIndex) GoldPrimary else Color(0xFF888888)
                                )
                            }
                        )
                    }
                }

                // نص الذكر الكامل
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2E40)),
                    border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = tasbihat[selectedIndex].first,
                        style = MaterialTheme.typography.headlineMedium,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        lineHeight = 40.sp
                    )
                }

                // شريط التقدم
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = GoldPrimary,
                    trackColor = Color(0xFF1A2E40)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "المجموع الكلي: $totalCount",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF888888)
                    )
                    Text(
                        text = "الهدف: $target",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF888888)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // زر العداد الكبير
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .scale(scale.value)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    if (count >= target) GreenIslamic else Color(0xFF1A4A60),
                                    if (count >= target) Color(0xFF1B5E20) else Color(0xFF0D2535)
                                )
                            ),
                            shape = CircleShape
                        )
                        .border(
                            3.dp,
                            if (count >= target) GreenLight else GoldPrimary,
                            CircleShape
                        )
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (count < target) {
                                count++
                                totalCount++
                            } else {
                                count = 0
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$count",
                            fontSize = 72.sp,
                            color = if (count >= target) Color.White else GoldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        if (count >= target) {
                            Text(
                                text = "✓ اكتمل",
                                style = MaterialTheme.typography.titleMedium,
                                color = GreenLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // أزرار التحكم
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = { if (count > 0) { count--; totalCount-- } },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF888888))
                    ) {
                        Text("تراجع -1")
                    }

                    Button(
                        onClick = { count = 0 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8B0000).copy(alpha = 0.7f)
                        )
                    ) {
                        Text("صفر الذكر")
                    }
                }
            }
        }
    }
}
