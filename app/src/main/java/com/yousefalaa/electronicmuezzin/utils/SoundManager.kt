package com.yousefalaa.electronicmuezzin.utils

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

object SoundManager {
    private var mediaPlayer: MediaPlayer? = null
    private const val TAG = "SoundManager"

    // ── خريطة الأصوات: اسم الملف → رابط التحميل ──────────
    val AZAN_DOWNLOAD_URLS = mapOf(
        // الأصوات الموجودة بالفعل في res/raw
        "azan_makkah"   to null,  // موجود محلياً
        "azan_fajr"     to null,  // موجود محلياً
        "azan_madinah"  to null,  // موجود محلياً
        "azan_egypt"    to null,  // موجود محلياً
        "azan_short"    to null,  // موجود محلياً

        // أصوات للتحميل
        "azan_makkah_sudais"   to "https://www.islamcan.com/audio/adhan/azan1.mp3",
        "azan_makkah_shuraim"  to "https://www.islamcan.com/audio/adhan/azan2.mp3",
        "azan_madinah_budayr"  to "https://www.islamcan.com/audio/adhan/azan3.mp3",
        "azan_abdulbasit"      to "https://www.islamcan.com/audio/adhan/azan4.mp3",
        "azan_minshawi"        to "https://www.islamcan.com/audio/adhan/azan5.mp3",
        "azan_egypt_classic"   to "https://www.islamcan.com/audio/adhan/azan6.mp3",
        "azan_husary"          to "https://www.islamcan.com/audio/adhan/azan7.mp3",
        "azan_tablawi"         to "https://www.islamcan.com/audio/adhan/azan8.mp3"
    )

    // أصوات الصلاة على النبي
    val SALAH_DOWNLOAD_URLS = mapOf(
        "salah_salli"          to "https://www.islamcan.com/audio/salah/salah1.mp3",
        "salah_allahumma_salli"to "https://www.islamcan.com/audio/salah/salah2.mp3",
        "salah_ibrahim"        to "https://www.islamcan.com/audio/salah/salah3.mp3",
        "salah_quran"          to "https://www.islamcan.com/audio/salah/salah4.mp3",
        "salah_rassoul"        to "https://www.islamcan.com/audio/salah/salah5.mp3",
        "salah_innallah"       to "https://www.islamcan.com/audio/salah/salah6.mp3",
        "salah_reminder"       to "https://www.islamcan.com/audio/salah/salah7.mp3"
    )

    /** تشغيل صوت من res/raw أو من ملف محمّل */
    fun play(context: Context, soundName: String) {
        stop()
        try {
            // أولاً: ابحث في res/raw
            val resId = context.resources.getIdentifier(soundName, "raw", context.packageName)
            if (resId != 0) {
                mediaPlayer = MediaPlayer.create(context, resId)
                mediaPlayer?.start()
                return
            }

            // ثانياً: ابحث في ملفات محمّلة
            val downloaded = File(context.filesDir, "$soundName.mp3")
            if (downloaded.exists()) {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(downloaded.absolutePath)
                    prepare()
                    start()
                }
                return
            }

            // مش موجود - ابلّغ
            Log.w(TAG, "Sound not found: $soundName")
        } catch (e: Exception) {
            Log.e(TAG, "Error playing $soundName: ${e.message}")
        }
    }

    fun stop() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                release()
            }
        } catch (e: Exception) { }
        mediaPlayer = null
    }

    fun isDownloaded(context: Context, soundName: String): Boolean {
        // موجود في res/raw
        val resId = context.resources.getIdentifier(soundName, "raw", context.packageName)
        if (resId != 0) return true
        // أو محمّل
        return File(context.filesDir, "$soundName.mp3").exists()
    }

    /** تحميل صوت من الإنترنت */
    suspend fun download(
        context: Context,
        soundName: String,
        url: String,
        onProgress: (Int) -> Unit
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(context.filesDir, "$soundName.mp3")
            val connection = URL(url).openConnection()
            connection.connect()
            val total = connection.contentLength
            var downloaded = 0

            connection.getInputStream().use { input ->
                FileOutputStream(file).use { output ->
                    val buffer = ByteArray(4096)
                    var count: Int
                    while (input.read(buffer).also { count = it } != -1) {
                        output.write(buffer, 0, count)
                        downloaded += count
                        if (total > 0) {
                            onProgress((downloaded * 100 / total))
                        }
                    }
                }
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Download failed for $soundName: ${e.message}")
            false
        }
    }
}

