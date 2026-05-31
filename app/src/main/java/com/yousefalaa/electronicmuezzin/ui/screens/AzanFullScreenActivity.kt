package com.yousefalaa.electronicmuezzin.ui.screens

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yousefalaa.electronicmuezzin.services.AzanService
import com.yousefalaa.electronicmuezzin.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AzanFullScreenActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // إظهار على شاشة القفل
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        val prayerName = intent.getStringExtra(AzanService.EXTRA_PRAYER_NAME) ?: "الصلاة"

        setContent {
            ElectronicMuezzinTheme(darkTheme = true) {
                AzanFullScreenContent(
                    prayerName = prayerName,
                    onStop = {
                        AzanService.stop(this)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun AzanFullScreenContent(
    prayerName: String,
    onStop: () -> Unit
) {
    // تأثير النبض
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // تأثير توهج
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0D3349),
                        Color(0xFF071520),
                        Color.Black
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            // هلال ونجمة
            Text(
                text = "☪",
                fontSize = 72.sp,
                color = GoldPrimary,
                modifier = Modifier.scale(scale)
            )

            // بسم الله الرحمن الرحيم
            Text(
                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                style = MaterialTheme.typography.titleLarge,
                color = GoldLight,
                textAlign = TextAlign.Center
            )

            // الصلاة خير من النوم (للفجر)
            if (prayerName == "الفجر") {
                Text(
                    text = "الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ",
                    style = MaterialTheme.typography.headlineMedium,
                    color = GoldPrimary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // حان وقت الصلاة
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "حانَ وَقْتُ",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "صَلَاةِ $prayerName",
                    style = MaterialTheme.typography.displayMedium,
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.scale(scale)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // نبضات خلفية زخرفية
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .border(
                        2.dp,
                        GoldPrimary.copy(alpha = alpha),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .border(
                            2.dp,
                            GoldPrimary.copy(alpha = alpha * 0.6f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🕌", fontSize = 48.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // الله أكبر
            Text(
                text = "اللَّهُ أَكْبَرُ",
                style = MaterialTheme.typography.displayMedium,
                color = GoldPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // زر إيقاف الأذان
            Button(
                onClick = onStop,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B0000).copy(alpha = 0.85f),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(52.dp)
            ) {
                Text(
                    text = "إيقاف الأذان",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
