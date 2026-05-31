package com.yousefalaa.electronicmuezzin.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.yousefalaa.electronicmuezzin.data.models.DhikrCategory
import com.yousefalaa.electronicmuezzin.ui.theme.*
import com.yousefalaa.electronicmuezzin.ui.viewmodels.AdhkarViewModel

@Composable
fun AdhkarScreen(navController: NavController) {
    val categories = listOf(
        Triple(DhikrCategory.MORNING, "أذكار الصباح", "🌅"),
        Triple(DhikrCategory.EVENING, "أذكار المساء", "🌙"),
        Triple(DhikrCategory.AFTER_PRAYER, "أذكار بعد الصلاة", "🤲"),
        Triple(DhikrCategory.SLEEP, "أذكار النوم", "😴"),
        Triple(DhikrCategory.WAKE, "أذكار الاستيقاظ", "☀️"),
        Triple(DhikrCategory.EATING, "أذكار الطعام", "🍽️"),
        Triple(DhikrCategory.QURAN, "أذكار قرآنية", "📖"),
        Triple(DhikrCategory.GENERAL, "أذكار عامة", "✨")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF5F0E8), Color(0xFFEDE8DC))
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF2E7D32), Color(0xFF1B5E20))
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "📿",
                        fontSize = 40.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "الأذكار اليومية",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "حصن المسلم",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFB9F6CA)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories) { (category, name, emoji) ->
                    AdhkarCategoryCard(
                        name = name,
                        emoji = emoji,
                        onClick = {
                            navController.navigate("adhkar_list/${category.name}")
                        }
                    )
                }

                item {
                    // بطاقة التسبيح
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("tasbih") },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A2E40)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "سبحان الله • الحمد لله • الله أكبر",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFAAAAAA),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "عداد التسبيح  📿",
                                style = MaterialTheme.typography.titleMedium,
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdhkarCategoryCard(name: String, emoji: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "›",
                fontSize = 24.sp,
                color = Color(0xFF2E7D32)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Bold
                )
                Text(text = emoji, fontSize = 28.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdhkarListScreen(
    categoryName: String,
    navController: NavController,
    viewModel: AdhkarViewModel = hiltViewModel()
) {
    val category = DhikrCategory.valueOf(categoryName)
    val adhkar by viewModel.getAdhkarByCategory(category).collectAsState(initial = emptyList())
    var currentIndex by remember { mutableStateOf(0) }
    var currentCount by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        category.nameAr,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32)
                )
            )
        }
    ) { padding ->
        if (adhkar.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GreenIslamic)
            }
        } else {
            val dhikr = adhkar[currentIndex % adhkar.size]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF5F0E8))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // العداد
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${currentIndex + 1} / ${adhkar.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF666666)
                    )
                    Text(
                        text = "الذكر",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF666666)
                    )
                }

                // نص الذكر
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = dhikr.text,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFF1A1A1A),
                            textAlign = TextAlign.Center,
                            lineHeight = 36.sp
                        )

                        if (dhikr.reference.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(color = Color(0xFFDDD8CC))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = dhikr.reference,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF2E7D32),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // عدد التكرار والتنقل
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // السابق
                    OutlinedButton(
                        onClick = {
                            if (currentIndex > 0) {
                                currentIndex--
                                currentCount = 0
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF2E7D32)
                        )
                    ) {
                        Text("‹ السابق")
                    }

                    // عداد التكرار
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$currentCount / ${dhikr.count}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = GreenIslamic,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "مرة",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF888888)
                        )
                    }

                    // التالي
                    Button(
                        onClick = {
                            if (currentCount < dhikr.count) {
                                currentCount++
                            }
                            if (currentCount >= dhikr.count && currentIndex < adhkar.size - 1) {
                                currentIndex++
                                currentCount = 0
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32)
                        )
                    ) {
                        Text("التالي ›")
                    }
                }
            }
        }
    }
}
