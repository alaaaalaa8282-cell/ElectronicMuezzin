package com.yousefalaa.electronicmuezzin.utils

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object SoundManager {
    private var mediaPlayer: MediaPlayer? = null
    private const val TAG = "SoundManager"

    fun play(context: Context, soundName: String) {
        stop()
        if (soundName.isEmpty() || soundName == "silent_mode") return
        try {
            // 1. res/raw
            val resId = context.resources.getIdentifier(soundName, "raw", context.packageName)
            if (resId != 0) {
                mediaPlayer = MediaPlayer.create(context, resId)
                mediaPlayer?.start()
                Log.d(TAG, "Playing from res/raw: $soundName")
                return
            }
            // 2. downloaded file
            val file = File(context.filesDir, "$soundName.mp3")
            if (file.exists()) {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(file.absolutePath)
                    prepare()
                    start()
                }
                Log.d(TAG, "Playing downloaded: $soundName")
                return
            }
            // 3. fallback to azan_makkah
            val fallback = context.resources.getIdentifier("azan_makkah", "raw", context.packageName)
            if (fallback != 0) {
                mediaPlayer = MediaPlayer.create(context, fallback)
                mediaPlayer?.start()
                Log.w(TAG, "Fallback to azan_makkah for: $soundName")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing $soundName: ${e.message}")
        }
    }

    fun stop() {
        try {
            mediaPlayer?.apply { if (isPlaying) stop(); release() }
        } catch (e: Exception) {}
        mediaPlayer = null
    }

    fun isDownloaded(context: Context, soundName: String): Boolean {
        if (soundName.isEmpty() || soundName == "silent_mode") return true
        val resId = context.resources.getIdentifier(soundName, "raw", context.packageName)
        if (resId != 0) return true
        return File(context.filesDir, "$soundName.mp3").exists()
    }

    suspend fun download(
        context: Context,
        soundName: String,
        url: String,
        onProgress: (Int) -> Unit
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(context.filesDir, "$soundName.mp3")
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.connectTimeout = 15000
            conn.readTimeout = 30000
            conn.connect()
            val total = conn.contentLength
            var downloaded = 0
            conn.inputStream.use { input ->
                FileOutputStream(file).use { output ->
                    val buffer = ByteArray(8192)
                    var count: Int
                    while (input.read(buffer).also { count = it } != -1) {
                        output.write(buffer, 0, count)
                        downloaded += count
                        if (total > 0) onProgress((downloaded * 100 / total))
                    }
                }
            }
            Log.d(TAG, "Downloaded: $soundName to ${file.absolutePath}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Download failed $soundName: ${e.message}")
            false
        }
    }
}
