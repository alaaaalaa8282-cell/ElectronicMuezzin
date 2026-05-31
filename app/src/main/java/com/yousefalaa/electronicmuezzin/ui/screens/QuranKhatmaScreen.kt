package com.yousefalaa.electronicmuezzin.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.yousefalaa.electronicmuezzin.data.models.QuranKhatma
import com.yousefalaa.electronicmuezzin.ui.theme.*
import com.yousefalaa.electronicmuezzin.ui.viewmodels.QuranKhatmaViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranKhatmaScreen(
    navController: NavController,
    viewModel: QuranKhatmaViewModel = hiltViewModel()
) {
    val activeKhatma by viewModel.activeKhatma.collectAsState(initial = null)
    val allKhatmat by viewModel.allKhatmat.collectAsState(initial = emptyList())
    var showNewDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("ختم القرآن الكريم 📖", style = MaterialTheme.typography.titleLarge, color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showNewDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "ختمة جديدة", tint = GoldPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D1B2A))
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = listOf(Color(0xFF0A1628), Color(0xFF0D1B2A))))
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // الختمة النشطة
                activeKhatma?.let { khatma ->
                    item {
                        ActiveKhatmaCard(
                            khatma = khatma,
                            onUpdate = { showUpdateDialog = true },
                            onComplete = { viewModel.completeKhatma(khatma) }
                        )
                    }
                }

                if (activeKhatma == null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2E40)),
                            border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(text = "📖", fontSize = 48.sp)
                                Text(
                                    text = "لا توجد ختمة نشطة",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = GoldPrimary
                                )
                                Text(
                                    text = "ابدأ ختمتك الجديدة الآن",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF888888)
                                )
                                Button(
                                    onClick = { showNewDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = GreenIslamic)
                                ) {
                                    Text("ابدأ ختمة جديدة")
                                }
                            }
                        }
                    }
                }

                // الختمات المكتملة
                val completed = allKhatmat.filter { it.isCompleted }
                if (completed.isNotEmpty()) {
                    item {
                        Text(
                            text = "الختمات المكتملة ✓",
                            style = MaterialTheme.typography.titleMedium,
                            color = GoldPrimary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    }
                    items(completed) { khatma ->
                        CompletedKhatmaCard(khatma)
                    }
                }

                // فضل قراءة القرآن
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2E40)),
                        border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "اقْرَأْ وَارْقَ وَرَتِّلْ",
                                style = MaterialTheme.typography.titleMedium,
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "«مَنْ قَرَأَ حَرْفًا مِنْ كِتَابِ اللَّهِ فَلَهُ بِهِ حَسَنَةٌ، وَالْحَسَنَةُ بِعَشْرِ أَمْثَالِهَا»",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GoldLight,
                                textAlign = TextAlign.Center,
                                lineHeight = 28.sp
                            )
                            Text(
                                text = "رواه الترمذي",
                                style = MaterialTheme.typography.bodySmall,
                                color = GreenLight
                            )
                        }
                    }
                }
            }
        }
    }

    // نافذة ختمة جديدة
    if (showNewDialog) {
        NewKhatmaDialog(
            onDismiss = { showNewDialog = false },
            onConfirm = { name, days ->
                viewModel.createKhatma(name, days)
                showNewDialog = false
            }
        )
    }

    // نافذة تحديث التقدم
    if (showUpdateDialog) {
        activeKhatma?.let { khatma ->
            UpdateProgressDialog(
                khatma = khatma,
                onDismiss = { showUpdateDialog = false },
                onUpdate = { juz, page ->
                    viewModel.updateProgress(khatma, juz, page)
                    showUpdateDialog = false
                }
            )
        }
    }
}

@Composable
fun ActiveKhatmaCard(khatma: QuranKhatma, onUpdate: () -> Unit, onComplete: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("ar"))
    val daysPassed = ((System.currentTimeMillis() - khatma.startDate) / 86400000L).toInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3A1A)),
        border = BorderStroke(1.dp, GreenLight.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "📖 نشطة",
                    style = MaterialTheme.typography.bodySmall,
                    color = GreenLight
                )
                Text(
                    text = khatma.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            // شريط التقدم
            LinearProgressIndicator(
                progress = { khatma.progressPercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = GreenLight,
                trackColor = Color(0xFF1A2E40)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${khatma.progressPercent.toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GreenLight
                )
                Text(
                    text = "صفحة ${khatma.currentPage} / 604",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFAAAAAA)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "يوم $daysPassed من ${khatma.targetDays}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF888888)
                )
                Text(
                    text = "الجزء ${khatma.currentJuz}",
                    style = MaterialTheme.typography.bodySmall,
                    color = GoldPrimary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onUpdate,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenIslamic)
                ) {
                    Text("تحديث التقدم")
                }
                OutlinedButton(
                    onClick = onComplete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary)
                ) {
                    Text("اكتملت 🎉")
                }
            }
        }
    }
}

@Composable
fun CompletedKhatmaCard(khatma: QuranKhatma) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("ar"))
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2E40)),
        border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = khatma.completedDate?.let { "✓ ${dateFormat.format(Date(it))}" } ?: "✓ مكتملة",
                style = MaterialTheme.typography.bodySmall,
                color = GreenLight
            )
            Text(
                text = khatma.name,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun NewKhatmaDialog(onDismiss: () -> Unit, onConfirm: (String, Int) -> Unit) {
    var name by remember { mutableStateOf("ختمتي الجديدة") }
    var days by remember { mutableStateOf("30") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A2E40),
        title = {
            Text("ختمة جديدة", color = GoldPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم الختمة", color = Color(0xFF888888)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = GoldDark
                    )
                )
                OutlinedTextField(
                    value = days,
                    onValueChange = { days = it.filter { c -> c.isDigit() } },
                    label = { Text("عدد أيام الختمة", color = Color(0xFF888888)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = GoldDark
                    )
                )
                Text(
                    text = "يُنصح: ختمة في 30 يوم = صفحة أو صفحتان يومياً",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF888888),
                    textAlign = TextAlign.End
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, days.toIntOrNull() ?: 30) },
                colors = ButtonDefaults.buttonColors(containerColor = GreenIslamic)
            ) { Text("ابدأ") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = Color(0xFF888888))
            }
        }
    )
}

@Composable
fun UpdateProgressDialog(
    khatma: QuranKhatma,
    onDismiss: () -> Unit,
    onUpdate: (Int, Int) -> Unit
) {
    var juz by remember { mutableStateOf("${khatma.currentJuz}") }
    var page by remember { mutableStateOf("${khatma.currentPage}") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A2E40),
        title = {
            Text("تحديث التقدم", color = GoldPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = juz,
                    onValueChange = { juz = it.filter { c -> c.isDigit() } },
                    label = { Text("الجزء الحالي (1-30)", color = Color(0xFF888888)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = GoldDark
                    )
                )
                OutlinedTextField(
                    value = page,
                    onValueChange = { page = it.filter { c -> c.isDigit() } },
                    label = { Text("الصفحة الحالية (1-604)", color = Color(0xFF888888)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = GoldDark
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onUpdate(
                        juz.toIntOrNull()?.coerceIn(1, 30) ?: khatma.currentJuz,
                        page.toIntOrNull()?.coerceIn(1, 604) ?: khatma.currentPage
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = GreenIslamic)
            ) { Text("حفظ") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = Color(0xFF888888))
            }
        }
    )
}
