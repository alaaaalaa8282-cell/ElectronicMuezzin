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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yousefalaa.electronicmuezzin.ui.theme.*
import com.yousefalaa.electronicmuezzin.ui.viewmodels.QuranKhatmaViewModel

data class Surah(
    val number: Int,
    val name: String,
    val ayat: Int,
    val page: Int,
    val type: String,
    val juz: Int,
    val meaning: String = ""
)

val ALL_SURAHS = listOf(
    Surah(1,"الفَاتِحَة",7,1,"مكية",1,"الفاتحة"),
    Surah(2,"البَقَرَة",286,2,"مدنية",1,"البقرة"),
    Surah(3,"آل عِمرَان",200,50,"مدنية",3,"آل عمران"),
    Surah(4,"النِّسَاء",176,77,"مدنية",4,"النساء"),
    Surah(5,"المَائِدَة",120,106,"مدنية",6,"المائدة"),
    Surah(6,"الأَنعَام",165,128,"مكية",7,"الأنعام"),
    Surah(7,"الأَعرَاف",206,151,"مكية",8,"الأعراف"),
    Surah(8,"الأَنفَال",75,177,"مدنية",9,"الأنفال"),
    Surah(9,"التَّوبَة",129,187,"مدنية",10,"التوبة"),
    Surah(10,"يُونُس",109,208,"مكية",11,"يونس"),
    Surah(11,"هُود",123,221,"مكية",11,"هود"),
    Surah(12,"يُوسُف",111,235,"مكية",12,"يوسف"),
    Surah(13,"الرَّعد",43,249,"مدنية",13,"الرعد"),
    Surah(14,"إِبرَاهِيم",52,255,"مكية",13,"إبراهيم"),
    Surah(15,"الحِجر",99,262,"مكية",14,"الحجر"),
    Surah(16,"النَّحل",128,267,"مكية",14,"النحل"),
    Surah(17,"الإِسرَاء",111,282,"مكية",15,"الإسراء"),
    Surah(18,"الكَهف",110,293,"مكية",15,"الكهف"),
    Surah(19,"مَريَم",98,305,"مكية",16,"مريم"),
    Surah(20,"طه",135,312,"مكية",16,"طه"),
    Surah(21,"الأَنبِيَاء",112,322,"مكية",17,"الأنبياء"),
    Surah(22,"الحَج",78,332,"مدنية",17,"الحج"),
    Surah(23,"المُؤمِنُون",118,342,"مكية",18,"المؤمنون"),
    Surah(24,"النُّور",64,350,"مدنية",18,"النور"),
    Surah(25,"الفُرقَان",77,359,"مكية",18,"الفرقان"),
    Surah(26,"الشُّعَرَاء",227,367,"مكية",19,"الشعراء"),
    Surah(27,"النَّمل",93,377,"مكية",19,"النمل"),
    Surah(28,"القَصَص",88,385,"مكية",20,"القصص"),
    Surah(29,"العَنكَبُوت",69,396,"مكية",20,"العنكبوت"),
    Surah(30,"الرُّوم",60,404,"مكية",21,"الروم"),
    Surah(31,"لُقمَان",34,411,"مكية",21,"لقمان"),
    Surah(32,"السَّجدَة",30,415,"مكية",21,"السجدة"),
    Surah(33,"الأَحزَاب",73,418,"مدنية",21,"الأحزاب"),
    Surah(34,"سَبَأ",54,428,"مكية",22,"سبأ"),
    Surah(35,"فَاطِر",45,434,"مكية",22,"فاطر"),
    Surah(36,"يس",83,440,"مكية",22,"يس"),
    Surah(37,"الصَّافَّات",182,446,"مكية",23,"الصافات"),
    Surah(38,"ص",88,453,"مكية",23,"ص"),
    Surah(39,"الزُّمَر",75,458,"مكية",23,"الزمر"),
    Surah(40,"غَافِر",85,467,"مكية",24,"غافر"),
    Surah(41,"فُصِّلَت",54,477,"مكية",24,"فصلت"),
    Surah(42,"الشُّورَى",53,483,"مكية",25,"الشورى"),
    Surah(43,"الزُّخرُف",89,489,"مكية",25,"الزخرف"),
    Surah(44,"الدُّخَان",59,496,"مكية",25,"الدخان"),
    Surah(45,"الجَاثِيَة",37,499,"مكية",25,"الجاثية"),
    Surah(46,"الأَحقَاف",35,502,"مكية",26,"الأحقاف"),
    Surah(47,"مُحَمَّد",38,507,"مدنية",26,"محمد"),
    Surah(48,"الفَتح",29,511,"مدنية",26,"الفتح"),
    Surah(49,"الحُجُرَات",18,515,"مدنية",26,"الحجرات"),
    Surah(50,"ق",45,518,"مكية",26,"ق"),
    Surah(51,"الذَّارِيَات",60,520,"مكية",26,"الذاريات"),
    Surah(52,"الطُّور",49,523,"مكية",27,"الطور"),
    Surah(53,"النَّجم",62,526,"مكية",27,"النجم"),
    Surah(54,"القَمَر",55,528,"مكية",27,"القمر"),
    Surah(55,"الرَّحمَن",78,531,"مدنية",27,"الرحمن"),
    Surah(56,"الوَاقِعَة",96,534,"مكية",27,"الواقعة"),
    Surah(57,"الحَدِيد",29,537,"مدنية",27,"الحديد"),
    Surah(58,"المُجَادَلَة",22,542,"مدنية",28,"المجادلة"),
    Surah(59,"الحَشر",24,545,"مدنية",28,"الحشر"),
    Surah(60,"المُمتَحَنَة",13,549,"مدنية",28,"الممتحنة"),
    Surah(61,"الصَّف",14,551,"مدنية",28,"الصف"),
    Surah(62,"الجُمُعَة",11,553,"مدنية",28,"الجمعة"),
    Surah(63,"المُنَافِقُون",11,554,"مدنية",28,"المنافقون"),
    Surah(64,"التَّغَابُن",18,556,"مدنية",28,"التغابن"),
    Surah(65,"الطَّلَاق",12,558,"مدنية",28,"الطلاق"),
    Surah(66,"التَّحرِيم",12,560,"مدنية",28,"التحريم"),
    Surah(67,"المُلك",30,562,"مكية",29,"الملك"),
    Surah(68,"القَلَم",52,564,"مكية",29,"القلم"),
    Surah(69,"الحَاقَّة",52,566,"مكية",29,"الحاقة"),
    Surah(70,"المَعَارِج",44,568,"مكية",29,"المعارج"),
    Surah(71,"نُوح",28,570,"مكية",29,"نوح"),
    Surah(72,"الجِن",28,572,"مكية",29,"الجن"),
    Surah(73,"المُزَّمِّل",20,574,"مكية",29,"المزمل"),
    Surah(74,"المُدَّثِّر",56,575,"مكية",29,"المدثر"),
    Surah(75,"القِيَامَة",40,577,"مكية",29,"القيامة"),
    Surah(76,"الإِنسَان",31,578,"مدنية",29,"الإنسان"),
    Surah(77,"المُرسَلَات",50,580,"مكية",29,"المرسلات"),
    Surah(78,"النَّبَأ",40,582,"مكية",30,"النبأ"),
    Surah(79,"النَّازِعَات",46,583,"مكية",30,"النازعات"),
    Surah(80,"عَبَسَ",42,585,"مكية",30,"عبس"),
    Surah(81,"التَّكوِير",29,586,"مكية",30,"التكوير"),
    Surah(82,"الإِنفِطَار",19,587,"مكية",30,"الانفطار"),
    Surah(83,"المُطَفِّفِين",36,587,"مكية",30,"المطففين"),
    Surah(84,"الإِنشِقَاق",25,589,"مكية",30,"الانشقاق"),
    Surah(85,"البُرُوج",22,590,"مكية",30,"البروج"),
    Surah(86,"الطَّارِق",17,591,"مكية",30,"الطارق"),
    Surah(87,"الأَعلَى",19,591,"مكية",30,"الأعلى"),
    Surah(88,"الغَاشِيَة",26,592,"مكية",30,"الغاشية"),
    Surah(89,"الفَجر",30,593,"مكية",30,"الفجر"),
    Surah(90,"البَلَد",20,594,"مكية",30,"البلد"),
    Surah(91,"الشَّمس",15,595,"مكية",30,"الشمس"),
    Surah(92,"اللَّيل",21,595,"مكية",30,"الليل"),
    Surah(93,"الضُّحَى",11,596,"مكية",30,"الضحى"),
    Surah(94,"الشَّرح",8,596,"مكية",30,"الشرح"),
    Surah(95,"التِّين",8,597,"مكية",30,"التين"),
    Surah(96,"العَلَق",19,597,"مكية",30,"العلق"),
    Surah(97,"القَدر",5,598,"مكية",30,"القدر"),
    Surah(98,"البَيِّنَة",8,598,"مدنية",30,"البينة"),
    Surah(99,"الزَّلزَلَة",8,599,"مدنية",30,"الزلزلة"),
    Surah(100,"العَادِيَات",11,599,"مكية",30,"العاديات"),
    Surah(101,"القَارِعَة",11,600,"مكية",30,"القارعة"),
    Surah(102,"التَّكَاثُر",8,600,"مكية",30,"التكاثر"),
    Surah(103,"العَصر",3,601,"مكية",30,"العصر"),
    Surah(104,"الهُمَزَة",9,601,"مكية",30,"الهمزة"),
    Surah(105,"الفِيل",5,601,"مكية",30,"الفيل"),
    Surah(106,"قُرَيش",4,602,"مكية",30,"قريش"),
    Surah(107,"المَاعُون",7,602,"مكية",30,"الماعون"),
    Surah(108,"الكَوثَر",3,602,"مكية",30,"الكوثر"),
    Surah(109,"الكَافِرُون",6,603,"مكية",30,"الكافرون"),
    Surah(110,"النَّصر",3,603,"مدنية",30,"النصر"),
    Surah(111,"المَسَد",5,603,"مكية",30,"المسد"),
    Surah(112,"الإِخلَاص",4,604,"مكية",30,"الإخلاص"),
    Surah(113,"الفَلَق",5,604,"مكية",30,"الفلق"),
    Surah(114,"النَّاس",6,604,"مكية",30,"الناس")
)

// ════════════════════════════════════════════════════
//  شاشة فهرس القرآن الرئيسية
// ════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranSurahIndexScreen(
    navController: NavController,
    khatmaVm: QuranKhatmaViewModel = hiltViewModel()
) {
    var selectedTab   by remember { mutableStateOf(0) }
    var selectedSurah by remember { mutableStateOf<Surah?>(null) }

    // لو فتح تفاصيل سورة
    selectedSurah?.let { surah ->
        SurahDetailScreen(
            surah   = surah,
            khatmaVm = khatmaVm,
            onBack  = { selectedSurah = null }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("خاتم القرآن الكريم 📖", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
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
                ) {
                    listOf("النوع","رقمها","آياتها","اسم السورة","#").forEach { col ->
                        Text(
                            col,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF4A3000),
                            modifier = Modifier.weight(if (col == "اسم السورة") 2f else 1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // قائمة السور
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(ALL_SURAHS) { index, surah ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (index % 2 == 0) Color(0xFFFFFBE6)
                                    else Color(0xFFF5EEC8)
                                )
                                .clickable { selectedSurah = surah }
                                .padding(horizontal = 8.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                if (surah.type == "مكية") "🕋" else "🕌",
                                fontSize = 18.sp,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "${surah.page}",
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                color = Color(0xFF4A3000)
                            )
                            Text(
                                "${surah.ayat}",
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                color = Color(0xFF4A3000)
                            )
                            Text(
                                surah.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(2f),
                                textAlign = TextAlign.Center,
                                color = Color(0xFF2A1800)
                            )
                            Text(
                                "${surah.number}",
                                fontSize = 12.sp,
                                modifier = Modifier.weight(0.7f),
                                textAlign = TextAlign.Center,
                                color = Color(0xFF888860)
                            )
                        }
                        HorizontalDivider(color = Color(0xFFD4C060), thickness = 0.5.dp)
                    }
                }
            } else {
                // تبويبة المرجعيات - ختمة القرآن
                KhatmaTab(khatmaVm)
            }
        }
    }
}

// ════════════════════════════════════════════════════
//  شاشة تفاصيل السورة
// ════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahDetailScreen(
    surah    : Surah,
    khatmaVm : QuranKhatmaViewModel,
    onBack   : () -> Unit
) {
    val activeKhatma by khatmaVm.activeKhatma.collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "سورة ${surah.name}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GoldDark)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFFBE6))
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // بطاقة معلومات السورة
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GoldDark)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = surah.name,
                            fontSize = 36.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            fontSize = 16.sp,
                            color = Color(0xFFFFF0B0),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // تفاصيل السورة
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        listOf(
                            "رقم السورة"  to "${surah.number}",
                            "عدد الآيات"  to "${surah.ayat} آية",
                            "رقم الصفحة"  to "صفحة ${surah.page}",
                            "الجزء"       to "الجزء ${surah.juz}",
                            "نوع السورة"  to "${surah.type} ${if (surah.type == "مكية") "🕋" else "🕌"}",
                            "ترتيب النزول" to if (surah.type == "مكية") "نزلت بمكة المكرمة" else "نزلت بالمدينة المنورة"
                        ).forEachIndexed { i, (label, value) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    value,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldDark,
                                    fontSize = 15.sp
                                )
                                Text(
                                    label,
                                    color = Color(0xFF666666),
                                    fontSize = 15.sp
                                )
                            }
                            if (i < 5) HorizontalDivider(color = Color(0xFFEEEEEE))
                        }
                    }
                }
            }

            // تحديث الختمة
            activeKhatma?.let { khatma ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FFF0)),
                        border = BorderStroke(1.dp, GreenIslamic.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "تحديث الختمة: ${khatma.name}",
                                fontWeight = FontWeight.Bold,
                                color = GreenIslamic,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                            Text(
                                "الوصول إلى سورة ${surah.name} - صفحة ${surah.page}",
                                color = Color(0xFF444444),
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = {
                                    khatmaVm.updateProgress(khatma, surah.juz, surah.page)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GreenIslamic),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    "تسجيل وصولت لهذه السورة ✓",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // فضل السورة (للسور المشهورة)
            val fadl = getSurahFadl(surah.number)
            if (fadl.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "فضل السورة",
                                fontWeight = FontWeight.Bold,
                                color = GoldDark,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End,
                                fontSize = 16.sp
                            )
                            HorizontalDivider(color = Color(0xFFEEEEEE))
                            fadl.forEach { (text, ref) ->
                                Text(
                                    text,
                                    color = Color(0xFF333333),
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth(),
                                    lineHeight = 24.sp
                                )
                                Text(
                                    ref,
                                    color = GreenIslamic,
                                    fontSize = 12.sp,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.End
                                )
                                if (fadl.indexOf(text to ref) < fadl.size - 1)
                                    HorizontalDivider(color = Color(0xFFEEEEEE))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════
//  تبويبة الختمة
// ════════════════════════════════════════════════════
@Composable
fun KhatmaTab(vm: QuranKhatmaViewModel) {
    val activeKhatma by vm.activeKhatma.collectAsState(initial = null)
    val allKhatmat   by vm.allKhatmat.collectAsState(initial = emptyList())
    var showNewDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // الختمة النشطة
        activeKhatma?.let { khatma ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FFF4)),
                    border = BorderStroke(1.dp, GreenIslamic.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("📖 نشطة", color = GreenIslamic, fontSize = 13.sp)
                            Text(khatma.name, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                        }

                        LinearProgressIndicator(
                            progress = { khatma.progressPercent / 100f },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = GreenIslamic,
                            trackColor = Color(0xFFCCCCCC)
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${khatma.progressPercent.toInt()}%", color = GreenIslamic, fontWeight = FontWeight.Bold)
                            Text("صفحة ${khatma.currentPage} من 604", color = Color(0xFF666666), fontSize = 13.sp)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("الجزء ${khatma.currentJuz}", color = GoldDark, fontSize = 13.sp)
                            val days = ((System.currentTimeMillis() - khatma.startDate) / 86400000L).toInt()
                            Text("يوم $days من ${khatma.targetDays}", color = Color(0xFF888888), fontSize = 13.sp)
                        }

                        Button(
                            onClick = { vm.completeKhatma(khatma) },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldDark),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(50)
                        ) { Text("اكتملت الختمة 🎉", fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }

        if (activeKhatma == null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("📖", fontSize = 40.sp)
                        Text("لا توجد ختمة نشطة", fontWeight = FontWeight.Bold, color = GoldDark)
                        Button(
                            onClick = { showNewDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenIslamic)
                        ) { Text("ابدأ ختمة جديدة") }
                    }
                }
            }
        } else {
            item {
                OutlinedButton(
                    onClick = { showNewDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenIslamic)
                ) { Text("+ ختمة جديدة") }
            }
        }

        // الختمات المكتملة
        val completed = allKhatmat.filter { it.isCompleted }
        if (completed.isNotEmpty()) {
            item {
                Text(
                    "الختمات المكتملة ✓",
                    color = GoldDark,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }
            items(completed.size) { i ->
                val k = completed[i]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("✓", color = GreenIslamic, fontWeight = FontWeight.Bold)
                        Text(k.name, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }

    if (showNewDialog) {
        NewKhatmaDialog(
            onDismiss = { showNewDialog = false },
            onConfirm = { name, days ->
                vm.createKhatma(name, days)
                showNewDialog = false
            }
        )
    }
}

// فضل السور المشهورة
fun getSurahFadl(number: Int): List<Pair<String, String>> = when (number) {
    1  -> listOf("أم القرآن، ولا صلاة لمن لم يقرأ بها" to "رواه البخاري ومسلم")
    2  -> listOf("لا تجعلوا بيوتكم مقابر، إن الشيطان يفر من البيت الذي تُقرأ فيه سورة البقرة" to "رواه مسلم",
                 "اقرأوا سورة البقرة فإن أخذها بركة وتركها حسرة ولا تستطيعها البطلة" to "رواه مسلم")
    3  -> listOf("اقرأوا الزهراوين البقرة وآل عمران فإنهما تأتيان يوم القيامة كأنهما غمامتان" to "رواه مسلم")
    18 -> listOf("من قرأ سورة الكهف يوم الجمعة أضاء له من النور ما بين الجمعتين" to "رواه الحاكم",
                 "من حفظ عشر آيات من أول سورة الكهف عُصم من الدجال" to "رواه مسلم")
    36 -> listOf("إن لكل شيء قلباً وقلب القرآن يس" to "رواه الترمذي")
    55 -> listOf("لكل شيء عروس وعروس القرآن الرحمن" to "رواه البيهقي")
    56 -> listOf("من قرأ سورة الواقعة في كل ليلة لم تصبه فاقة أبداً" to "رواه البيهقي")
    67 -> listOf("سورة تبارك هي المانعة من عذاب القبر" to "رواه الحاكم",
                 "إن سورة في القرآن ثلاثون آية شفعت لرجل حتى غُفر له وهي تبارك الذي بيده الملك" to "رواه الترمذي")
    112 -> listOf("قل هو الله أحد تعدل ثلث القرآن" to "رواه البخاري ومسلم")
    113 -> listOf("المعوذتان خير ما تعوذ بهما المتعوذون" to "رواه النسائي")
    114 -> listOf("المعوذتان خير ما تعوذ بهما المتعوذون" to "رواه النسائي")
    else -> emptyList()
}
