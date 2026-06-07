package com.yousefalaa.electronicmuezzin.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.*
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.yousefalaa.electronicmuezzin.ElectronicMuezzinApp.Companion.CHANNEL_AZAN
import com.yousefalaa.electronicmuezzin.MainActivity
import com.yousefalaa.electronicmuezzin.R
import com.yousefalaa.electronicmuezzin.ui.screens.AzanFullScreenActivity
import java.io.File

class AzanService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var audioManager: AudioManager? = null
    private var audioFocusRequest: AudioFocusRequest? = null
    private var wakeLock: PowerManager.WakeLock? = null

    companion object {
        const val EXTRA_PRAYER_NAME = "prayer_name"
        const val EXTRA_AZAN_SOUND  = "azan_sound"
        const val ACTION_STOP_AZAN  = "com.yousefalaa.electronicmuezzin.STOP_AZAN"
        const val NOTIF_ID = 1001

        fun start(context: Context, prayerName: String, azanSound: String) {
            val intent = Intent(context, AzanService::class.java).apply {
                putExtra(EXTRA_PRAYER_NAME, prayerName)
                putExtra(EXTRA_AZAN_SOUND, azanSound)
                putExtra("from_alarm", true)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.startForegroundService(intent)
            else
                context.startService(intent)
        }

        fun stop(context: Context) {
            context.startService(Intent(context, AzanService::class.java).apply {
                action = ACTION_STOP_AZAN
            })
        }
    }

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ElectronicMuezzin::AzanWakeLock")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_AZAN) { stopAzan(); return START_NOT_STICKY }

        val prayerName = intent?.getStringExtra(EXTRA_PRAYER_NAME) ?: "الصلاة"
        val azanSound  = intent?.getStringExtra(EXTRA_AZAN_SOUND)  ?: "azan_makkah"

        if (azanSound == "silent_mode") { stopSelf(); return START_NOT_STICKY }

        // لو intent == null معناه Android أعاد تشغيل الـ Service تلقائياً — لا نشغّل أذان
        if (intent == null) { stopSelf(); return START_NOT_STICKY }

        startForeground(NOTIF_ID, buildNotification(prayerName))
        wakeLock?.acquire(10 * 60 * 1000L)
        requestAudioFocus { playAzan(azanSound) }

        // شاشة الأذان الكاملة — بس لو جاي من alarm حقيقي
        val fromAlarm = intent.getBooleanExtra("from_alarm", false)
        if (fromAlarm) {
            startActivity(Intent(this, AzanFullScreenActivity::class.java).also { i ->
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                i.putExtra(EXTRA_PRAYER_NAME, prayerName)
            })
        }

        return START_NOT_STICKY
    }

    private fun requestAudioFocus(onGranted: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val attr = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
            val req = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                .setAudioAttributes(attr).setOnAudioFocusChangeListener {}.build()
            audioFocusRequest = req
            if (audioManager?.requestAudioFocus(req) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) onGranted()
        } else {
            @Suppress("DEPRECATION")
            if (audioManager?.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) onGranted()
        }
    }

    private fun playAzan(soundName: String) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer()

            // 1. ابحث في res/raw
            val resId = resources.getIdentifier(soundName, "raw", packageName)
            if (resId != 0) {
                val afd = resources.openRawResourceFd(resId)
                mediaPlayer!!.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
            } else {
                // 2. ابحث في ملفات محمّلة
                val file = File(filesDir, "$soundName.mp3")
                if (file.exists()) {
                    mediaPlayer!!.setDataSource(file.absolutePath)
                } else {
                    // 3. فول باك: azan_makkah
                    val fallback = resources.getIdentifier("azan_makkah", "raw", packageName)
                    if (fallback != 0) {
                        val afd = resources.openRawResourceFd(fallback)
                        mediaPlayer!!.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                        afd.close()
                    } else { stopAzan(); return }
                }
            }

            mediaPlayer!!.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
            )
            mediaPlayer!!.setOnCompletionListener { stopAzan() }
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
        } catch (e: Exception) { e.printStackTrace(); stopAzan() }
    }

    private fun stopAzan() {
        try { mediaPlayer?.apply { if (isPlaying) stop(); release() } } catch (e: Exception) {}
        mediaPlayer = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            audioFocusRequest?.let { audioManager?.abandonAudioFocusRequest(it) }
        else @Suppress("DEPRECATION") audioManager?.abandonAudioFocus(null)
        wakeLock?.takeIf { it.isHeld }?.release()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun buildNotification(prayerName: String): Notification {
        val stopPending = PendingIntent.getService(
            this, 0,
            Intent(this, AzanService::class.java).apply { action = ACTION_STOP_AZAN },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val mainPending = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_AZAN)
            .setContentTitle("حان وقت $prayerName")
            .setContentText("اضغط لإيقاف الأذان")
            .setSmallIcon(R.drawable.ic_mosque)
            .setContentIntent(mainPending)
            .addAction(R.drawable.ic_stop, "إيقاف", stopPending)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onDestroy() { stopAzan(); super.onDestroy() }
}
