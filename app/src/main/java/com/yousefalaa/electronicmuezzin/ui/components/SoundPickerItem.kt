package com.yousefalaa.electronicmuezzin.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yousefalaa.electronicmuezzin.data.models.AzanSound
import com.yousefalaa.electronicmuezzin.utils.SoundManager
import kotlinx.coroutines.launch

val AppRedColor = Color(0xFFD4573A)

@Composable
fun SoundPickerItem(
    sound     : AzanSound,
    isSelected: Boolean,
    onSelect  : () -> Unit
) {
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()

    var isPlaying    by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }
    var progress     by remember { mutableStateOf(0) }
    var isDownloaded by remember { mutableStateOf(false) }

    // تحقق إذا كان الصوت موجوداً
    LaunchedEffect(sound.key) {
        if (sound.key.isNotEmpty()) {
            isDownloaded = SoundManager.isDownloaded(context, sound.key)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        // أزرار التشغيل والتحميل (يسار - في RTL يظهر على اليمين)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            // زر التشغيل
            if (sound.key.isNotEmpty() && sound.key != "silent_mode") {
                if (isDownloaded) {
                    // زر تشغيل حقيقي
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                if (isPlaying) AppRedColor.copy(alpha = 0.2f) else Color.Transparent,
                                CircleShape
                            )
                            .clickable {
                                if (isPlaying) {
                                    SoundManager.stop()
                                    isPlaying = false
                                } else {
                                    SoundManager.stop()
                                    SoundManager.play(context, sound.key)
                                    isPlaying = true
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (isPlaying) "⏹" else "▶",
                            color = AppRedColor,
                            fontSize = 16.sp
                        )
                    }
                } else if (sound.url != null) {
                    // زر التحميل
                    if (isDownloading) {
                        Box(
                            modifier = Modifier.size(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { progress / 100f },
                                modifier = Modifier.size(24.dp),
                                color    = AppRedColor,
                                strokeWidth = 2.dp
                            )
                            Text("$progress", fontSize = 7.sp, color = AppRedColor)
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFF0F0F0), CircleShape)
                                .clickable {
                                    scope.launch {
                                        isDownloading = true
                                        val ok = SoundManager.download(
                                            context, sound.key, sound.url
                                        ) { p -> progress = p }
                                        isDownloading = false
                                        if (ok) isDownloaded = true
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⬇", color = AppRedColor, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // الاسم + الـ radio button
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    sound.label,
                    color      = Color(0xFF1A1A1A),
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    textAlign  = TextAlign.End
                )
                if (sound.url != null && !isDownloaded && !isDownloading) {
                    Text(
                        "اضغط ⬇ للتحميل",
                        color    = Color(0xFF888888),
                        fontSize = 11.sp
                    )
                }
            }
            RadioButton(
                selected  = isSelected,
                onClick   = onSelect,
                colors    = RadioButtonDefaults.colors(selectedColor = AppRedColor)
            )
        }
    }
}

// ─────────────────────────────────────────────
//  شاشة اختيار الصوت الكاملة (مشتركة)
// ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundPickerScreen(
    title      : String,
    sounds     : List<AzanSound>,
    currentKey : String,
    onSelect   : (String) -> Unit,
    onBack     : () -> Unit
) {
    val grouped = sounds.groupBy { it.group }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        title,
                        fontWeight = FontWeight.Bold,
                        color      = Color(0xFF1A1A1A),
                        modifier   = Modifier.fillMaxWidth(),
                        textAlign  = TextAlign.End
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("›", fontSize = 28.sp, color = Color(0xFF1A1A1A))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { pad ->
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF2F2F7))
                .padding(pad)
        ) {
            grouped.forEach { (group, groupSounds) ->
                if (group.isNotEmpty()) {
                    item {
                        Text(
                            group,
                            modifier  = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            textAlign = TextAlign.End,
                            color     = Color(0xFF888888),
                            fontSize  = 13.sp
                        )
                    }
                }
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape  = RoundedCornerShape(12.dp)
                    ) {
                        Column {
                            groupSounds.forEachIndexed { i, sound ->
                                SoundPickerItem(
                                    sound      = sound,
                                    isSelected = currentKey == sound.key,
                                    onSelect   = { onSelect(sound.key) }
                                )
                                if (i < groupSounds.size - 1)
                                    HorizontalDivider(
                                        modifier  = Modifier.padding(horizontal = 16.dp),
                                        color     = Color(0xFFEEEEEE)
                                    )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}
