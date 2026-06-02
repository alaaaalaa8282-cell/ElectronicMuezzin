package com.yousefalaa.electronicmuezzin.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.yousefalaa.electronicmuezzin.ui.theme.*

// ════════════════════════════════
//  يوم الجمعة
// ════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FridayScreen(navController: NavController) {
    val fridayAdhkar = listOf(
        Triple("الإكثار من الصلاة على النبي ﷺ", "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَى إِبْرَاهِيمَ وَعَلَى آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ", "رواه البخاري - يستحب الإكثار منها يوم الجمعة"),
        Triple("قراءة سورة الكهف", "من قرأ سورة الكهف يوم الجمعة أضاء له من النور ما بين الجمعتين", "رواه الحاكم والبيهقي"),
        Triple("الدعاء في ساعة الإجابة", "في الجمعة ساعة لا يوافقها عبد مسلم وهو قائم يصلي يسأل الله شيئًا إلا أعطاه إياه", "رواه البخاري ومسلم"),
        Triple("دعاء يوم الجمعة", "اللَّهُمَّ إِنِّي أَسْأَلُكَ بِأَنَّ لَكَ الْحَمْدَ لَا إِلَهَ إِلَّا أَنْتَ الْمَنَّانُ بَدِيعُ السَّمَوَاتِ وَالْأَرْضِ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ يَا حَيُّ يَا قَيُّومُ", "رواه الترمذي وأبو داود"),
    )

    val sundnahFriday = listOf(
        "الاغتسال يوم الجمعة",
        "التبكير إلى المسجد",
        "قراءة سورة الكهف",
        "الإكثار من الصلاة على النبي ﷺ",
        "الإكثار من الدعاء بعد العصر",
        "لبس أحسن الثياب",
        "استخدام السواك",
        "التطيب"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("يوم الجمعة 🕌", style = MaterialTheme.typography.titleLarge, color = Color.White) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenIslamic)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF5F0E8)).padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GreenIslamic)) {
                    Text("﴿ يَا أَيُّهَا الَّذِينَ آمَنُوا إِذَا نُودِيَ لِلصَّلَاةِ مِن يَوْمِ الْجُمُعَةِ فَاسْعَوْا إِلَىٰ ذِكْرِ اللَّهِ ﴾\nالجمعة: 9",
                        color = Color.White, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(16.dp), lineHeight = 28.sp)
                }
            }

            item {
                Text("سنن يوم الجمعة", fontWeight = FontWeight.Bold, color = GreenIslamic,
                    fontSize = 18.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        sundnahFriday.forEachIndexed { i, sunnah ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                                Text(sunnah, fontWeight = FontWeight.Medium, textAlign = TextAlign.End)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("✓", color = GreenIslamic, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            if (i < sundnahFriday.size - 1) Divider(color = Color(0xFFEEEEEE))
                        }
                    }
                }
            }

            item {
                Text("أذكار وأدعية يوم الجمعة", fontWeight = FontWeight.Bold, color = GreenIslamic,
                    fontSize = 18.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
            }

            fridayAdhkar.forEach { (title, text, ref) ->
                item {
                    var count by remember { mutableStateOf(0) }
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(title, fontWeight = FontWeight.Bold, color = GreenIslamic, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                            Divider(color = Color(0xFFEEEEEE))
                            Text(text, color = Color(0xFF1A1A1A), textAlign = TextAlign.Center, lineHeight = 28.sp)
                            Text(ref, color = Color(0xFF888888), fontSize = 12.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("$count مرة", color = GreenIslamic, fontWeight = FontWeight.Bold)
                                Button(onClick = { count++ },
                                    colors = ButtonDefaults.buttonColors(containerColor = GreenIslamic),
                                    shape = RoundedCornerShape(50)) { Text("عدّ") }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ════════════════════════════════
//  تكبيرات العيد وذي الحجة
// ════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EidTakberatScreen(navController: NavController) {
    var takberatCount by remember { mutableStateOf(0) }
    var selectedTab by remember { mutableStateOf(0) }

    val takberat = listOf(
        "اللَّهُ أَكْبَرُ اللَّهُ أَكْبَرُ لَا إِلَهَ إِلَّا اللَّهُ وَاللَّهُ أَكْبَرُ اللَّهُ أَكْبَرُ وَلِلَّهِ الْحَمْدُ",
        "اللَّهُ أَكْبَرُ كَبِيرًا وَالْحَمْدُ لِلَّهِ كَثِيرًا وَسُبْحَانَ اللَّهِ بُكْرَةً وَأَصِيلًا",
        "اللَّهُ أَكْبَرُ اللَّهُ أَكْبَرُ اللَّهُ أَكْبَرُ لَا إِلَهَ إِلَّا اللَّهُ اللَّهُ أَكْبَرُ اللَّهُ أَكْبَرُ وَلِلَّهِ الْحَمْدُ"
    )

    val dhulHijjahFadl = listOf(
        "ما من أيام العمل الصالح فيها أحب إلى الله من هذه الأيام العشر" to "رواه البخاري",
        "صيام يوم عرفة يكفر السنة الماضية والقادمة" to "رواه مسلم",
        "الإكثار من التهليل والتكبير والتحميد في أيام العشر" to "رواه أحمد",
        "يوم النحر أعظم الأيام عند الله" to "رواه أبو داود"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تكبيرات العيد وذي الحجة", style = MaterialTheme.typography.titleLarge, color = Color.White) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GoldDark)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().background(Color(0xFFFFFBE6)).padding(padding)) {
            TabRow(selectedTabIndex = selectedTab, containerColor = Color(0xFFEEE8C0), contentColor = GoldDark) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("تكبيرات العيد", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("فضل العشر", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
            }

            if (selectedTab == 0) {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = GoldDark)) {
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("عداد التكبيرات", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("$takberatCount", fontSize = 64.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Button(onClick = { takberatCount++ },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                        shape = RoundedCornerShape(50)) {
                                        Text("الله أكبر", color = GoldDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                    OutlinedButton(onClick = { takberatCount = 0 },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                        border = BorderStroke(1.dp, Color.White)) {
                                        Text("صفر")
                                    }
                                }
                            }
                        }
                    }

                    takberat.forEachIndexed { i, text ->
                        item {
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                    Text("صيغة ${i + 1}", color = GoldDark, fontWeight = FontWeight.Bold,
                                        modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text, textAlign = TextAlign.Center, lineHeight = 30.sp,
                                        fontSize = 16.sp, modifier = Modifier.fillMaxWidth())
                                }
                            }
                        }
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = GoldDark)) {
                            Text("فضل عشر ذي الحجة", color = Color.White, fontWeight = FontWeight.Bold,
                                fontSize = 18.sp, modifier = Modifier.fillMaxWidth().padding(16.dp), textAlign = TextAlign.Center)
                        }
                    }
                    dhulHijjahFadl.forEach { (text, ref) ->
                        item {
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                    Text(text, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth(), lineHeight = 24.sp)
                                    Text(ref, color = GoldDark, fontSize = 12.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
