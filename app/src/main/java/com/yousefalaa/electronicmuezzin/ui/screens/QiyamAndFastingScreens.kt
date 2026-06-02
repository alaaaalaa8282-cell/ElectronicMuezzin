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
//  قيام الليل
// ════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiyamScreen(navController: NavController) {
    val adhkar = listOf(
        Triple("دعاء قيام الليل", "اللَّهُمَّ لَكَ الْحَمْدُ أَنْتَ نُورُ السَّمَوَاتِ وَالْأَرْضِ وَمَنْ فِيهِنَّ، وَلَكَ الْحَمْدُ أَنْتَ قَيِّمُ السَّمَوَاتِ وَالْأَرْضِ وَمَنْ فِيهِنَّ", "رواه البخاري ومسلم"),
        Triple("دعاء الاستفتاح في قيام الليل", "اللَّهُمَّ رَبَّ جِبْرَائِيلَ وَمِيكَائِيلَ وَإِسْرَافِيلَ، فَاطِرَ السَّمَوَاتِ وَالْأَرْضِ، عَالِمَ الْغَيْبِ وَالشَّهَادَةِ، أَنْتَ تَحْكُمُ بَيْنَ عِبَادِكَ فِيمَا كَانُوا فِيهِ يَخْتَلِفُونَ", "رواه مسلم"),
        Triple("دعاء الوتر - القنوت", "اللَّهُمَّ اهْدِنِي فِيمَنْ هَدَيْتَ، وَعَافِنِي فِيمَنْ عَافَيْتَ، وَتَوَلَّنِي فِيمَنْ تَوَلَّيْتَ، وَبَارِكْ لِي فِيمَا أَعْطَيْتَ، وَقِنِي شَرَّ مَا قَضَيْتَ", "رواه الترمذي وأبو داود"),
        Triple("دعاء السجود في قيام الليل", "اللَّهُمَّ اجْعَلْ فِي قَلْبِي نُورًا، وَفِي سَمْعِي نُورًا، وَفِي بَصَرِي نُورًا، وَعَنْ يَمِينِي نُورًا، وَعَنْ يَسَارِي نُورًا", "رواه مسلم"),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("قيام الليل 🌙", style = MaterialTheme.typography.titleLarge, color = Color.White) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A1A3A))
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .background(Brush.verticalGradient(colors = listOf(Color(0xFF1A1A3A), Color(0xFF0D0D20))))
                .padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A5A))
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🌙", fontSize = 40.sp)
                        Text("﴿ وَمِنَ اللَّيْلِ فَتَهَجَّدْ بِهِ نَافِلَةً لَّكَ ﴾",
                            color = GoldPrimary, textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(8.dp))
                        Text("الإسراء: 79", color = Color(0xFF888888), fontSize = 13.sp)
                    }
                }
            }

            // فضل قيام الليل
            item {
                Text("فضل قيام الليل", color = GoldPrimary, fontWeight = FontWeight.Bold,
                    fontSize = 18.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E4A))) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            "أفضل الصلاة بعد الفريضة صلاة الليل" to "رواه مسلم",
                            "من قام رمضان إيمانًا واحتسابًا غفر له ما تقدم من ذنبه" to "رواه البخاري",
                            "ركعتان في جوف الليل خير من الدنيا وما فيها" to "رواه الترمذي"
                        ).forEach { (text, ref) ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(ref, color = GreenLight, fontSize = 11.sp)
                                Text("• $text", color = Color.White, textAlign = TextAlign.End, modifier = Modifier.weight(1f).padding(end = 8.dp), fontSize = 13.sp)
                            }
                            Divider(color = Color(0xFF3A3A6A), thickness = 0.5.dp)
                        }
                    }
                }
            }

            // أذكار قيام الليل
            item {
                Text("أذكار قيام الليل", color = GoldPrimary, fontWeight = FontWeight.Bold,
                    fontSize = 18.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
            }

            adhkar.forEach { (title, text, ref) ->
                item {
                    var count by remember { mutableStateOf(0) }
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E4A))) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(title, color = GoldPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                            Text(text, color = Color.White, textAlign = TextAlign.Center, lineHeight = 28.sp)
                            Text(ref, color = GreenLight, fontSize = 12.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("$count مرة", color = GoldPrimary, fontWeight = FontWeight.Bold)
                                Button(onClick = { count++ },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A6A)),
                                    shape = RoundedCornerShape(50)) { Text("عدّ", color = GoldPrimary) }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ════════════════════════════════
//  صيام التطوع
// ════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FastingScreen(navController: NavController) {
    val fastingTypes = listOf(
        Triple("صيام الاثنين والخميس", "قال النبي ﷺ: تُعرض الأعمال يوم الاثنين والخميس فأحب أن يعرض عملي وأنا صائم", "رواه الترمذي"),
        Triple("صيام ثلاثة أيام من كل شهر", "صيام ثلاثة أيام من كل شهر صيام الدهر كله - الأيام البيض: 13، 14، 15 من كل شهر هجري", "رواه البخاري ومسلم"),
        Triple("صيام يوم عرفة", "صيام يوم عرفة يكفر السنة الماضية والسنة القادمة - لغير الحاج", "رواه مسلم"),
        Triple("صيام يوم عاشوراء", "صيام يوم عاشوراء يكفر السنة الماضية - مع صيام يوم قبله أو بعده", "رواه مسلم"),
        Triple("صيام ست من شوال", "من صام رمضان ثم أتبعه ستًا من شوال كان كصيام الدهر", "رواه مسلم"),
        Triple("صيام تاسوعاء وعاشوراء", "صم يوم عاشوراء وخالف اليهود صم قبله يومًا أو بعده يومًا", "رواه أحمد"),
        Triple("صيام المحرم", "أفضل الصيام بعد رمضان شهر الله المحرم", "رواه مسلم"),
        Triple("صيام داوود عليه السلام", "أحب الصلاة إلى الله صلاة داوود - كان يصوم يومًا ويفطر يومًا", "رواه البخاري ومسلم"),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("صيام التطوع 🌙", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2E7D32))
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF5F0E8)).padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20))) {
                    Text("﴿ وَأَن تَصُومُوا خَيْرٌ لَّكُمْ إِن كُنتُمْ تَعْلَمُونَ ﴾\nالبقرة: 184",
                        color = Color.White, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(16.dp), lineHeight = 28.sp)
                }
            }

            fastingTypes.forEach { (title, desc, ref) ->
                item {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32),
                                textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth(), fontSize = 16.sp)
                            Divider(color = Color(0xFFE0E0E0))
                            Text(desc, color = Color(0xFF333333), textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth(), lineHeight = 24.sp)
                            Text(ref, color = Color(0xFF888888), fontSize = 12.sp,
                                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
                        }
                    }
                }
            }
        }
    }
}
