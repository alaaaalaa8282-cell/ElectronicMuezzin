package com.yousefalaa.electronicmuezzin.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.yousefalaa.electronicmuezzin.ui.theme.*

data class MoreItem(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val route: String
)

@Composable
fun MoreScreen(navController: NavController) {
    val items = listOf(
        MoreItem("📿", "عداد التسبيح", "سبحان الله • الحمد لله • الله أكبر", "tasbih"),
        MoreItem("🌙", "شهر رمضان", "تنبيه الإفطار والسحور وعداد الأيام", "ramadan"),
        MoreItem("📖", "ختم القرآن", "تتبع ختمتك وتنظيم وردك اليومي", "quran_khatma"),
        MoreItem("⚙️", "الإعدادات", "طريقة الحساب وإعدادات الأذان والموقع", "settings")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A1628), Color(0xFF0D1B2A))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // رأس الصفحة
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF0D3349), Color(0xFF091A26))
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "المزيد",
                    style = MaterialTheme.typography.headlineMedium,
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items.forEach { item ->
                    MoreItemCard(item) { navController.navigate(item.route) }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // بطاقة الإصدار والإهداء
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
                        Text(text = "☪", fontSize = 32.sp, color = GoldPrimary)
                        Text(
                            text = "المؤذن الإلكتروني",
                            style = MaterialTheme.typography.titleMedium,
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "الإصدار 1.0",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF888888)
                        )
                        Divider(color = GoldDark.copy(alpha = 0.3f))
                        Text(
                            text = "تطبيق إسلامي شامل لمواقيت الصلاة والأذان والأذكار",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFAAAAAA),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MoreItemCard(item: MoreItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2E40)),
        border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "›", fontSize = 22.sp, color = GoldPrimary)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f),
            ) {
                Text(text = item.emoji, fontSize = 36.sp)
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF888888)
                    )
                }
            }
        }
    }
}
