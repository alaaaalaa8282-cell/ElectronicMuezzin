package com.yousefalaa.electronicmuezzin.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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

data class Surah(val number: Int, val name: String, val ayat: Int, val page: Int, val type: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranSurahIndexScreen(navController: NavController) {
    val surahs = listOf(
        Surah(1,"الفَاتِحَة",7,1,"مكية"), Surah(2,"البَقَرَة",286,2,"مدنية"),
        Surah(3,"آل عِمرَان",200,50,"مدنية"), Surah(4,"النِّسَاء",176,77,"مدنية"),
        Surah(5,"المَائِدَة",120,106,"مدنية"), Surah(6,"الأَنعَام",165,128,"مكية"),
        Surah(7,"الأَعرَاف",206,151,"مكية"), Surah(8,"الأَنفَال",75,177,"مدنية"),
        Surah(9,"التَّوبَة",129,187,"مدنية"), Surah(10,"يُونُس",109,208,"مكية"),
        Surah(11,"هُود",123,221,"مكية"), Surah(12,"يُوسُف",111,235,"مكية"),
        Surah(13,"الرَّعد",43,249,"مدنية"), Surah(14,"إِبرَاهِيم",52,255,"مكية"),
        Surah(15,"الحِجر",99,262,"مكية"), Surah(16,"النَّحل",128,267,"مكية"),
        Surah(17,"الإِسرَاء",111,282,"مكية"), Surah(18,"الكَهف",110,293,"مكية"),
        Surah(19,"مَريَم",98,305,"مكية"), Surah(20,"طه",135,312,"مكية"),
        Surah(21,"الأَنبِيَاء",112,322,"مكية"), Surah(22,"الحَج",78,332,"مدنية"),
        Surah(23,"المُؤمِنُون",118,342,"مكية"), Surah(24,"النُّور",64,350,"مدنية"),
        Surah(25,"الفُرقَان",77,359,"مكية"), Surah(26,"الشُّعَرَاء",227,367,"مكية"),
        Surah(27,"النَّمل",93,377,"مكية"), Surah(28,"القَصَص",88,385,"مكية"),
        Surah(29,"العَنكَبُوت",69,396,"مكية"), Surah(30,"الرُّوم",60,404,"مكية"),
        Surah(31,"لُقمَان",34,411,"مكية"), Surah(32,"السَّجدَة",30,415,"مكية"),
        Surah(33,"الأَحزَاب",73,418,"مدنية"), Surah(34,"سَبَأ",54,428,"مكية"),
        Surah(35,"فَاطِر",45,434,"مكية"), Surah(36,"يس",83,440,"مكية"),
        Surah(37,"الصَّافَّات",182,446,"مكية"), Surah(38,"ص",88,453,"مكية"),
        Surah(39,"الزُّمَر",75,458,"مكية"), Surah(40,"غَافِر",85,467,"مكية"),
        Surah(41,"فُصِّلَت",54,477,"مكية"), Surah(42,"الشُّورَى",53,483,"مكية"),
        Surah(43,"الزُّخرُف",89,489,"مكية"), Surah(44,"الدُّخَان",59,496,"مكية"),
        Surah(45,"الجَاثِيَة",37,499,"مكية"), Surah(46,"الأَحقَاف",35,502,"مكية"),
        Surah(47,"مُحَمَّد",38,507,"مدنية"), Surah(48,"الفَتح",29,511,"مدنية"),
        Surah(49,"الحُجُرَات",18,515,"مدنية"), Surah(50,"ق",45,518,"مكية"),
        Surah(51,"الذَّارِيَات",60,520,"مكية"), Surah(52,"الطُّور",49,523,"مكية"),
        Surah(53,"النَّجم",62,526,"مكية"), Surah(54,"القَمَر",55,528,"مكية"),
        Surah(55,"الرَّحمَن",78,531,"مدنية"), Surah(56,"الوَاقِعَة",96,534,"مكية"),
        Surah(57,"الحَدِيد",29,537,"مدنية"), Surah(58,"المُجَادَلَة",22,542,"مدنية"),
        Surah(59,"الحَشر",24,545,"مدنية"), Surah(60,"المُمتَحَنَة",13,549,"مدنية"),
        Surah(61,"الصَّف",14,551,"مدنية"), Surah(62,"الجُمُعَة",11,553,"مدنية"),
        Surah(63,"المُنَافِقُون",11,554,"مدنية"), Surah(64,"التَّغَابُن",18,556,"مدنية"),
        Surah(65,"الطَّلَاق",12,558,"مدنية"), Surah(66,"التَّحرِيم",12,560,"مدنية"),
        Surah(67,"المُلك",30,562,"مكية"), Surah(68,"القَلَم",52,564,"مكية"),
        Surah(69,"الحَاقَّة",52,566,"مكية"), Surah(70,"المَعَارِج",44,568,"مكية"),
        Surah(71,"نُوح",28,570,"مكية"), Surah(72,"الجِن",28,572,"مكية"),
        Surah(73,"المُزَّمِّل",20,574,"مكية"), Surah(74,"المُدَّثِّر",56,575,"مكية"),
        Surah(75,"القِيَامَة",40,577,"مكية"), Surah(76,"الإِنسَان",31,578,"مدنية"),
        Surah(77,"المُرسَلَات",50,580,"مكية"), Surah(78,"النَّبَأ",40,582,"مكية"),
        Surah(79,"النَّازِعَات",46,583,"مكية"), Surah(80,"عَبَسَ",42,585,"مكية"),
        Surah(81,"التَّكوِير",29,586,"مكية"), Surah(82,"الإِنفِطَار",19,587,"مكية"),
        Surah(83,"المُطَفِّفِين",36,587,"مكية"), Surah(84,"الإِنشِقَاق",25,589,"مكية"),
        Surah(85,"البُرُوج",22,590,"مكية"), Surah(86,"الطَّارِق",17,591,"مكية"),
        Surah(87,"الأَعلَى",19,591,"مكية"), Surah(88,"الغَاشِيَة",26,592,"مكية"),
        Surah(89,"الفَجر",30,593,"مكية"), Surah(90,"البَلَد",20,594,"مكية"),
        Surah(91,"الشَّمس",15,595,"مكية"), Surah(92,"اللَّيل",21,595,"مكية"),
        Surah(93,"الضُّحَى",11,596,"مكية"), Surah(94,"الشَّرح",8,596,"مكية"),
        Surah(95,"التِّين",8,597,"مكية"), Surah(96,"العَلَق",19,597,"مكية"),
        Surah(97,"القَدر",5,598,"مكية"), Surah(98,"البَيِّنَة",8,598,"مدنية"),
        Surah(99,"الزَّلزَلَة",8,599,"مدنية"), Surah(100,"العَادِيَات",11,599,"مكية"),
        Surah(101,"القَارِعَة",11,600,"مكية"), Surah(102,"التَّكَاثُر",8,600,"مكية"),
        Surah(103,"العَصر",3,601,"مكية"), Surah(104,"الهُمَزَة",9,601,"مكية"),
        Surah(105,"الفِيل",5,601,"مكية"), Surah(106,"قُرَيش",4,602,"مكية"),
        Surah(107,"المَاعُون",7,602,"مكية"), Surah(108,"الكَوثَر",3,602,"مكية"),
        Surah(109,"الكَافِرُون",6,603,"مكية"), Surah(110,"النَّصر",3,603,"مدنية"),
        Surah(111,"المَسَد",5,603,"مكية"), Surah(112,"الإِخلَاص",4,604,"مكية"),
        Surah(113,"الفَلَق",5,604,"مكية"), Surah(114,"النَّاس",6,604,"مكية")
    )

    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("خاتم القرآن الكريم", style = MaterialTheme.typography.titleLarge, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GoldDark)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFFBE6))
                .padding(padding)
        ) {
            // تبويبات
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFFEEE8C0),
                contentColor = GoldDark
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("الفهرس", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("المرجعيات", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
            }

            if (selectedTab == 0) {
                // رأس الجدول
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFD4C060))
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("النوع", "رقمها", "آياتها", "اسم السورة", "#").forEach { col ->
                        Text(col, fontWeight = FontWeight.Bold, fontSize = 12.sp,
                            color = Color(0xFF4A3000), modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center)
                    }
                }

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(surahs) { index, surah ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (index % 2 == 0) Color(0xFFFFFBE6) else Color(0xFFF5EEC8))
                                .padding(horizontal = 8.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // النوع (مكية/مدنية)
                            Text(
                                text = if (surah.type == "مكية") "🕋" else "🕌",
                                fontSize = 18.sp,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                            Text("${surah.page}", fontSize = 12.sp,
                                modifier = Modifier.weight(1f), textAlign = TextAlign.Center,
                                color = Color(0xFF4A3000))
                            Text("${surah.ayat}", fontSize = 12.sp,
                                modifier = Modifier.weight(1f), textAlign = TextAlign.Center,
                                color = Color(0xFF4A3000))
                            Text(surah.name, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(2f), textAlign = TextAlign.Center,
                                color = Color(0xFF2A1800))
                            Text("${surah.number}", fontSize = 12.sp,
                                modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center,
                                color = Color(0xFF888860))
                        }
                        Divider(color = Color(0xFFD4C060), thickness = 0.5.dp)
                    }
                }
            } else {
                // تبويبة المرجعيات - تتبع الختمة
                QuranKhatmaScreen(navController)
            }
        }
    }
}

